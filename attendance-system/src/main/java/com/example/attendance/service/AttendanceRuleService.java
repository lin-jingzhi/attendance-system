package com.example.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.attendance.entity.AttendanceRule;

import java.util.List;

/**
 * 考勤规则服务接口
 * 
 * @author System
 * @date 2025-03-04
 */
public interface AttendanceRuleService extends IService<AttendanceRule> {
    
    /**
     * 获取默认考勤规则
     * 
     * @return 默认考勤规则
     */
    AttendanceRule getDefaultRule();
    
    /**
     * 获取所有启用的考勤规则
     * 
     * @return 启用规则列表
     */
    List<AttendanceRule> getEnabledRules();
    
    /**
     * 设置默认规则
     * 
     * @param ruleId 规则ID
     * @return 设置结果
     */
    boolean setDefaultRule(Long ruleId);
}