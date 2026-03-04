package com.example.attendance.controller;

import com.example.attendance.entity.Employee;
import com.example.attendance.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 员工信息控制器
 * 
 * @author System
 * @date 2025-03-04
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;
    
    /**
     * 获取所有员工列表
     */
    @GetMapping("/list")
    public List<Employee> list() {
        log.info("查询所有员工列表");
        return employeeService.list();
    }
    
    /**
     * 根据ID获取员工信息
     */
    @GetMapping("/{id}")
    public Employee getById(@PathVariable Long id) {
        log.info("查询员工信息：ID={}", id);
        return employeeService.getById(id);
    }
    
    /**
     * 根据员工编号获取员工信息
     */
    @GetMapping("/no/{employeeNo}")
    public Employee getByNo(@PathVariable String employeeNo) {
        log.info("查询员工信息：员工编号={}", employeeNo);
        return employeeService.getEmployeeByNo(employeeNo);
    }
    
    /**
     * 根据部门查询员工
     */
    @GetMapping("/department/{department}")
    public List<Employee> getByDepartment(@PathVariable String department) {
        log.info("查询部门员工：{}", department);
        return employeeService.getEmployeesByDepartment(department);
    }
    
    /**
     * 新增员工
     */
    @PostMapping("/add")
    public boolean add(@RequestBody Employee employee) {
        log.info("新增员工：{}", employee.getName());
        return employeeService.save(employee);
    }
    
    /**
     * 修改员工信息
     */
    @PutMapping("/update")
    public boolean update(@RequestBody Employee employee) {
        log.info("修改员工信息：ID={}", employee.getId());
        return employeeService.updateById(employee);
    }
    
    /**
     * 删除员工
     */
    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Long id) {
        log.info("删除员工：ID={}", id);
        return employeeService.removeById(id);
    }
    
    /**
     * 导入员工信息Excel
     */
    @PostMapping("/import")
    public String importEmployees(@RequestParam("file") MultipartFile file) {
        log.info("导入员工信息Excel：文件名={}", file.getOriginalFilename());
        return employeeService.importEmployees(file);
    }
    
    /**
     * 导出员工信息到Excel
     */
    @GetMapping("/export")
    public void exportEmployees(HttpServletResponse response) {
        log.info("导出员工信息到Excel");
        employeeService.exportEmployees(response);
    }
}