package com.example.attendance.controller;

import com.example.attendance.entity.AttendanceRecord;
import com.example.attendance.service.AttendanceRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

/**
 * 打卡记录控制器
 * 
 * @author System
 * @date 2025-03-04
 */
@Slf4j
@RestController
@RequestMapping("/record")
public class AttendanceRecordController {
    
    @Autowired
    private AttendanceRecordService attendanceRecordService;
    
    /**
     * 手动录入打卡记录
     */
    @PostMapping("/clockin")
    public boolean clockIn(@RequestBody AttendanceRecord record) {
        log.info("录入打卡记录：员工ID={}, 日期={}", record.getEmployeeId(), record.getRecordDate());
        return attendanceRecordService.clockIn(record);
    }
    
    /**
     * 批量导入打卡记录Excel
     */
    @PostMapping("/import")
    public String importRecords(@RequestParam("file") MultipartFile file) {
        log.info("导入打卡记录Excel：文件名={}", file.getOriginalFilename());
        return attendanceRecordService.importRecords(file);
    }
    
    /**
     * 根据员工ID和日期查询打卡记录
     */
    @GetMapping("/employee/{employeeId}/date/{recordDate}")
    public AttendanceRecord getRecord(@PathVariable Long employeeId, @PathVariable String recordDate) {
        log.info("查询打卡记录：员工ID={}, 日期={}", employeeId, recordDate);
        return attendanceRecordService.getRecordByEmployeeAndDate(employeeId, LocalDate.parse(recordDate));
    }
    
    /**
     * 根据日期范围查询打卡记录
     */
    @GetMapping("/range")
    public List<AttendanceRecord> getRecordsByRange(@RequestParam String startDate, @RequestParam String endDate) {
        log.info("查询打卡记录范围：{} 到 {}", startDate, endDate);
        return attendanceRecordService.getRecordsByDateRange(
            LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
    
    /**
     * 修改打卡记录
     */
    @PutMapping("/update")
    public boolean updateRecord(@RequestBody AttendanceRecord record) {
        log.info("修改打卡记录：ID={}", record.getId());
        return attendanceRecordService.updateById(record);
    }
    
    /**
     * 删除打卡记录
     */
    @DeleteMapping("/{id}")
    public boolean deleteRecord(@PathVariable Long id) {
        log.info("删除打卡记录：ID={}", id);
        return attendanceRecordService.removeById(id);
    }
}