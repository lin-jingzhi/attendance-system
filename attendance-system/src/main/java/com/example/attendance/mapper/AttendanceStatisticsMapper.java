package com.example.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.entity.AttendanceStatistics;
import org.apache.ibatis.annotations.Mapper;

/**
 * 月度考勤统计Mapper接口
 * 
 * @author System
 * @date 2025-03-04
 */
@Mapper
public interface AttendanceStatisticsMapper extends BaseMapper<AttendanceStatistics> {
}