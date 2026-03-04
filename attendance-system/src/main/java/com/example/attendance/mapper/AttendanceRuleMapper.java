package com.example.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.entity.AttendanceRule;
import org.apache.ibatis.annotations.Mapper;

/**
 * 考勤规则Mapper接口
 * 
 * @author System
 * @date 2025-03-04
 */
@Mapper
public interface AttendanceRuleMapper extends BaseMapper<AttendanceRule> {
}