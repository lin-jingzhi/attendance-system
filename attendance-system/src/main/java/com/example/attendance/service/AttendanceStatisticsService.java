package com.example.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.attendance.entity.AttendanceStatistics;

import java.util.List;

/**
 * 月度考勤统计服务接口
 * 
 * @author System
 * @date 2025-03-04
 */
public interface AttendanceStatisticsService extends IService<AttendanceStatistics> {
    
    /**
     * 生成月度考勤统计
     * 
     * @param month 统计月份（格式：yyyy-MM）
     * @return 统计结果
     */
    boolean generateMonthlyStatistics(String month);
    
    /**
     * 根据月份查询统计结果
     * 
     * @param month 统计月份
     * @return 统计列表
     */
    List<AttendanceStatistics> getStatisticsByMonth(String month);
    
    /**
     * 根据员工ID和月份查询统计结果
     * 
     * @param employeeId 员工ID
     * @param month 统计月份
     * @return 统计结果
     */
    AttendanceStatistics getStatisticsByEmployeeAndMonth(Long employeeId, String month);
    
    /**
     * 导出月度统计报表
     * 
     * @param month 统计月份
     * @return 报表文件路径
     */
    String exportMonthlyReport(String month);
}