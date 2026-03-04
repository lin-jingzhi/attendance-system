package com.example.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 打卡记录实体类
 * 
 * @author System
 * @date 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_record")
public class AttendanceRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 员工ID
     */
    @TableField("employee_id")
    private Long employeeId;
    
    /**
     * 打卡日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("record_date")
    private LocalDate recordDate;
    
    /**
     * 上班打卡时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("clock_in_time")
    private LocalDateTime clockInTime;
    
    /**
     * 下班打卡时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("clock_out_time")
    private LocalDateTime clockOutTime;
    
    /**
     * 迟到分钟数
     */
    @TableField("late_minutes")
    private Integer lateMinutes;
    
    /**
     * 早退分钟数
     */
    @TableField("early_leave_minutes")
    private Integer earlyLeaveMinutes;
    
    /**
     * 考勤状态：0-正常，1-迟到，2-早退，3-旷工
     */
    @TableField("attendance_status")
    private Integer attendanceStatus;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}