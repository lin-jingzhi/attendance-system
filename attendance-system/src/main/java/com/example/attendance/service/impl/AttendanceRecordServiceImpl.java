package com.example.attendance.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.entity.AttendanceRule;
import com.example.attendance.entity.Employee;
import com.example.attendance.mapper.AttendanceRecordMapper;
import com.example.attendance.service.AttendanceRecordService;
import com.example.attendance.service.AttendanceRuleService;
import com.example.attendance.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 打卡记录服务实现类
 * 
 * @author System
 * @date 2025-03-04
 */
@Slf4j
@Service
public class AttendanceRecordServiceImpl extends ServiceImpl<AttendanceRecordMapper, AttendanceRecord> implements AttendanceRecordService {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private AttendanceRuleService attendanceRuleService;
    
    /**
     * 手动录入打卡记录
     */
    @Override
    @Transactional
    public boolean clockIn(AttendanceRecord record) {
        log.info("录入打卡记录：员工ID={}, 日期={}", record.getEmployeeId(), record.getRecordDate());
        
        // 验证员工是否存在
        Employee employee = employeeService.getById(record.getEmployeeId());
        if (employee == null || employee.getStatus() == 0) {
            throw new RuntimeException("员工不存在或已离职");
        }
        
        // 检查是否已存在打卡记录
        AttendanceRecord existingRecord = getRecordByEmployeeAndDate(record.getEmployeeId(), record.getRecordDate());
        if (existingRecord != null) {
            // 更新已有记录
            if (record.getClockInTime() != null) {
                existingRecord.setClockInTime(record.getClockInTime());
            }
            if (record.getClockOutTime() != null) {
                existingRecord.setClockOutTime(record.getClockOutTime());
            }
            
            // 重新计算考勤状态
            calculateAttendanceStatus(existingRecord);
            return this.updateById(existingRecord);
        } else {
            // 创建新记录
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            
            // 计算考勤状态
            calculateAttendanceStatus(record);
            return this.save(record);
        }
    }
    
    /**
     * 批量导入打卡记录Excel
     */
    @Override
    @Transactional
    public String importRecords(MultipartFile file) {
        try {
            List<AttendanceRecord> recordList = new ArrayList<>();
            
            EasyExcel.read(file.getInputStream(), AttendanceRecord.class, new PageReadListener<AttendanceRecord>(dataList -> {
                for (AttendanceRecord record : dataList) {
                    // 验证数据完整性
                    if (record.getEmployeeId() == null || record.getRecordDate() == null) {
                        continue;
                    }
                    
                    // 验证员工是否存在
                    Employee employee = employeeService.getById(record.getEmployeeId());
                    if (employee == null || employee.getStatus() == 0) {
                        continue;
                    }
                    
                    recordList.add(record);
                }
            })).sheet().doRead();
            
            // 批量处理打卡记录
            int successCount = 0;
            for (AttendanceRecord record : recordList) {
                try {
                    if (clockIn(record)) {
                        successCount++;
                    }
                } catch (Exception e) {
                    log.error("导入打卡记录失败：员工ID={}, 日期={}", record.getEmployeeId(), record.getRecordDate(), e);
                }
            }
            
            return String.format("成功导入 %d/%d 条打卡记录", successCount, recordList.size());
            
        } catch (IOException e) {
            log.error("导入打卡记录失败", e);
            return "导入失败：" + e.getMessage();
        }
    }
    
    /**
     * 根据员工ID和日期查询打卡记录
     */
    @Override
    public AttendanceRecord getRecordByEmployeeAndDate(Long employeeId, LocalDate recordDate) {
        return this.getOne(new QueryWrapper<AttendanceRecord>()
            .eq("employee_id", employeeId)
            .eq("record_date", recordDate)
            .last("LIMIT 1"));
    }
    
    /**
     * 根据日期范围查询打卡记录
     */
    @Override
    public List<AttendanceRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        return this.list(new QueryWrapper<AttendanceRecord>()
            .between("record_date", startDate, endDate)
            .orderByAsc("record_date", "employee_id"));
    }
    
    /**
     * 自动统计考勤状态
     */
    @Override
    public void calculateAttendanceStatus(AttendanceRecord record) {
        AttendanceRule defaultRule = attendanceRuleService.getDefaultRule();
        if (defaultRule == null) {
            throw new RuntimeException("未设置默认考勤规则");
        }
        
        // 初始化状态
        record.setLateMinutes(0);
        record.setEarlyLeaveMinutes(0);
        record.setAttendanceStatus(0); // 默认正常
        
        LocalDateTime clockInTime = record.getClockInTime();
        LocalDateTime clockOutTime = record.getClockOutTime();
        
        // 计算上班打卡状态
        if (clockInTime != null) {
            LocalTime actualStart = clockInTime.toLocalTime();
            LocalTime expectedStart = defaultRule.getWorkStartTime();
            
            if (actualStart.isAfter(expectedStart)) {
                long lateMinutes = ChronoUnit.MINUTES.between(expectedStart, actualStart);
                if (lateMinutes > defaultRule.getLateThreshold()) {
                    record.setLateMinutes((int) lateMinutes);
                    record.setAttendanceStatus(1); // 迟到
                }
            }
        } else {
            // 未打卡上班，视为旷工
            record.setAttendanceStatus(3); // 旷工
        }
        
        // 计算下班打卡状态
        if (clockOutTime != null) {
            LocalTime actualEnd = clockOutTime.toLocalTime();
            LocalTime expectedEnd = defaultRule.getWorkEndTime();
            
            if (actualEnd.isBefore(expectedEnd)) {
                long earlyMinutes = ChronoUnit.MINUTES.between(actualEnd, expectedEnd);
                if (earlyMinutes > defaultRule.getEarlyLeaveThreshold()) {
                    record.setEarlyLeaveMinutes((int) earlyMinutes);
                    
                    // 如果已经是迟到状态，则保持迟到；否则设置为早退
                    if (record.getAttendanceStatus() == 0) {
                        record.setAttendanceStatus(2); // 早退
                    }
                }
            }
        } else if (record.getAttendanceStatus() != 3) {
            // 未打卡下班，但已打卡上班，视为正常（可能忘记打卡）
            // 可根据业务需求调整此逻辑
        }
        
        log.debug("计算考勤状态：员工ID={}, 状态={}, 迟到={}分钟, 早退={}分钟", 
            record.getEmployeeId(), record.getAttendanceStatus(), 
            record.getLateMinutes(), record.getEarlyLeaveMinutes());
    }
}