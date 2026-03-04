package com.example.attendance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.attendance.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 员工信息Mapper接口
 * 
 * @author System
 * @date 2025-03-04
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}