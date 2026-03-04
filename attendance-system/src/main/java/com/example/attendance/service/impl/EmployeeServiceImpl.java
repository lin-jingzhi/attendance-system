package com.example.attendance.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.attendance.entity.Employee;
import com.example.attendance.mapper.EmployeeMapper;
import com.example.attendance.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 员工信息服务实现类
 * 
 * @author System
 * @date 2025-03-04
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    
    /**
     * 导入员工信息Excel
     */
    @Override
    public String importEmployees(MultipartFile file) {
        try {
            List<Employee> employeeList = new ArrayList<>();
            
            EasyExcel.read(file.getInputStream(), Employee.class, new PageReadListener<Employee>(dataList -> {
                for (Employee employee : dataList) {
                    // 验证数据完整性
                    if (StringUtils.isBlank(employee.getEmployeeNo()) || 
                        StringUtils.isBlank(employee.getName()) || 
                        StringUtils.isBlank(employee.getDepartment())) {
                        continue;
                    }
                    
                    // 设置默认值
                    if (employee.getStatus() == null) {
                        employee.setStatus(1); // 默认在职
                    }
                    if (employee.getHireDate() == null) {
                        employee.setHireDate(LocalDate.now());
                    }
                    
                    employeeList.add(employee);
                }
            })).sheet().doRead();
            
            // 批量保存
            if (!employeeList.isEmpty()) {
                this.saveBatch(employeeList);
                // 清除缓存
                clearEmployeeCache();
                return String.format("成功导入 %d 条员工记录", employeeList.size());
            }
            
            return "导入失败：Excel文件中没有有效数据";
            
        } catch (IOException e) {
            log.error("导入员工信息失败", e);
            return "导入失败：" + e.getMessage();
        }
    }
    
    /**
     * 导出员工信息到Excel
     */
    @Override
    public void exportEmployees(HttpServletResponse response) {
        try {
            // 查询所有在职员工
            List<Employee> employeeList = this.list(new QueryWrapper<Employee>()
                .eq("status", 1)
                .orderByAsc("department", "employee_no"));
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("员工信息表", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=" + fileName + ".xlsx");
            
            // 写入Excel
            EasyExcel.write(response.getOutputStream(), Employee.class)
                .sheet("员工信息")
                .doWrite(employeeList);
                
        } catch (IOException e) {
            log.error("导出员工信息失败", e);
            throw new RuntimeException("导出失败", e);
        }
    }
    
    /**
     * 根据部门查询员工
     */
    @Override
    @Cacheable(value = "employees", key = "#department")
    public List<Employee> getEmployeesByDepartment(String department) {
        log.info("查询部门员工：{}", department);
        return this.list(new QueryWrapper<Employee>()
            .eq("department", department)
            .eq("status", 1)
            .orderByAsc("employee_no"));
    }
    
    /**
     * 根据员工编号查询员工
     */
    @Override
    @Cacheable(value = "employee", key = "#employeeNo")
    public Employee getEmployeeByNo(String employeeNo) {
        log.info("查询员工信息：{}", employeeNo);
        return this.getOne(new QueryWrapper<Employee>()
            .eq("employee_no", employeeNo)
            .eq("status", 1)
            .last("LIMIT 1"));
    }
    
    /**
     * 清除员工缓存
     */
    @CacheEvict(value = {"employees", "employee"}, allEntries = true)
    public void clearEmployeeCache() {
        log.info("清除员工信息缓存");
    }
}