package com.example.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 月度考勤统计实体类
 * 
 * @author System
 * @date 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_statistics")
public class AttendanceStatistics extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;
    
    /**
     * 统计月份（格式：yyyy-MM）
     */
    @TableField("statistics_month")
    private String statisticsMonth;
    
    /**
     * 应出勤天数
     */
    @TableField("work_days")
    private Integer workDays;
    
    /**
     * 实际出勤天数
     */
    @TableField("actual_days")
    private Integer actualDays;
    
    /**
     * 迟到次数
     */
    @TableField("late_count")
    private Integer lateCount;
    
    /**
     * 早退次数
     */
    @TableField("early_leave_count")
    private Integer earlyLeaveCount;
    
    /**
     * 旷工次数
     */
    @TableField("absent_count")
    private Integer absentCount;
}