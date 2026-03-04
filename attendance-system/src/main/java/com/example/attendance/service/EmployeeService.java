package com.example.attendance.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.attendance.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 员工信息服务接口
 * 
 * @author System
 * @date 2025-03-04
 */
public interface EmployeeService extends IService<Employee> {
    
    /**
     * 导入员工信息Excel
     * 
     * @param file Excel文件
     * @return 导入结果
     */
    String importEmployees(MultipartFile file);
    
    /**
     * 导出员工信息到Excel
     * 
     * @param response HTTP响应
     */
    void exportEmployees(HttpServletResponse response);
    
    /**
     * 根据部门查询员工
     * 
     * @param department 部门名称
     * @return 员工列表
     */
    List<Employee> getEmployeesByDepartment(String department);
    
    /**
     * 根据员工编号查询员工
     * 
     * @param employeeNo 员工编号
     * @return 员工信息
     */
    Employee getEmployeeByNo(String employeeNo);
}