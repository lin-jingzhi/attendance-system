package com.example.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.attendance.entity.AttendanceRecord;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡记录服务接口
 * 
 * @author System
 * @date 2025-03-04
 */
public interface AttendanceRecordService extends IService<AttendanceRecord> {
    
    /**
     * 手动录入打卡记录
     * 
     * @param record 打卡记录
     * @return 录入结果
     */
    boolean clockIn(AttendanceRecord record);
    
    /**
     * 批量导入打卡记录Excel
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    String importRecords(MultipartFile file);
    
    /**
     * 根据员工ID和日期查询打卡记录
     * 
     * @param employeeId 员工ID
     * @param recordDate 打卡日期
     * @return 打卡记录
     */
    AttendanceRecord getRecordByEmployeeAndDate(Long employeeId, LocalDate recordDate);
    
    /**
     * 根据日期范围查询打卡记录
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 打卡记录列表
     */
    List<AttendanceRecord> getRecordsByDateRange(LocalDate startDate, LocalDate endDate);
    
    /**
     * 自动统计考勤状态
     * 
     * @param record 打卡记录
     */
    void calculateAttendanceStatus(AttendanceRecord record);
}