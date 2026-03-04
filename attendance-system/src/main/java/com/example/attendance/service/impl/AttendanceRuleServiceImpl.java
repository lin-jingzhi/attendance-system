package com.example.attendance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.attendance.entity.AttendanceRule;
import com.example.attendance.mapper.AttendanceRuleMapper;
import com.example.attendance.service.AttendanceRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 考勤规则服务实现类
 * 
 * @author System
 * @date 2025-03-04
 */
@Slf4j
@Service
public class AttendanceRuleServiceImpl extends ServiceImpl<AttendanceRuleMapper, AttendanceRule> implements AttendanceRuleService {
    
    /**
     * 获取默认考勤规则
     */
    @Override
    @Cacheable(value = "attendanceRule", key = "'default'")
    public AttendanceRule getDefaultRule() {
        log.info("查询默认考勤规则");
        return this.getOne(new QueryWrapper<AttendanceRule>()
            .eq("is_default", 1)
            .eq("status", 1)
            .last("LIMIT 1"));
    }
    
    /**
     * 获取所有启用的考勤规则
     */
    @Override
    @Cacheable(value = "attendanceRules", key = "'enabled'")
    public List<AttendanceRule> getEnabledRules() {
        log.info("查询所有启用的考勤规则");
        return this.list(new QueryWrapper<AttendanceRule>()
            .eq("status", 1)
            .orderByDesc("is_default", "create_time"));
    }
    
    /**
     * 设置默认规则
     */
    @Override
    @Transactional
    @CacheEvict(value = {"attendanceRule", "attendanceRules"}, allEntries = true)
    public boolean setDefaultRule(Long ruleId) {
        log.info("设置默认考勤规则：{}", ruleId);
        
        // 1. 取消当前默认规则
        AttendanceRule currentDefault = this.getDefaultRule();
        if (currentDefault != null) {
            currentDefault.setIsDefault(0);
            this.updateById(currentDefault);
        }
        
        // 2. 设置新的默认规则
        AttendanceRule newDefault = this.getById(ruleId);
        if (newDefault == null) {
            throw new RuntimeException("考勤规则不存在");
        }
        
        newDefault.setIsDefault(1);
        return this.updateById(newDefault);
    }
    
    /**
     * 清除考勤规则缓存
     */
    @CacheEvict(value = {"attendanceRule", "attendanceRules"}, allEntries = true)
    public void clearRuleCache() {
        log.info("清除考勤规则缓存");
    }
}