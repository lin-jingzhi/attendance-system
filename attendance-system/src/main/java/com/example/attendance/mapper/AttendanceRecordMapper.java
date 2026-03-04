package com.example.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.entity.AttendanceRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 打卡记录Mapper接口
 * 
 * @author System
 * @date 2025-03-04
 */
@Mapper
public interface AttendanceRecordMapper extends BaseMapper<AttendanceRecord> {
}