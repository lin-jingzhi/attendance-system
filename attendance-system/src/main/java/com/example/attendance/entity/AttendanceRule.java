package com.example.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalTime;

/**
 * 考勤规则实体类
 * 
 * @author System
 * @date 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("attendance_rule")
public class AttendanceRule extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 规则名称
     */
    @TableField("rule_name")
    private String ruleName;
    
    /**
     * 上班时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @TableField("work_start_time")
    private LocalTime workStartTime;
    
    /**
     * 下班时间
     */
    @JsonFormat(pattern = "HH:mm:ss")
    @TableField("work_end_time")
    private LocalTime workEndTime;
    
    /**
     * 迟到阈值（分钟）
     */
    @TableField("late_threshold")
    private Integer lateThreshold;
    
    /**
     * 早退阈值（分钟）
     */
    @TableField("early_leave_threshold")
    private Integer earlyLeaveThreshold;
    
    /**
     * 是否默认规则：1-是，0-否
     */
    @TableField("is_default")
    private Integer isDefault;
    
    /**
     * 状态：1-启用，0-禁用
     */
    @TableField("status")
    private Integer status;
}