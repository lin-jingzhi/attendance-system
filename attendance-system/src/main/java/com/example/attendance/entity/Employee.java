package com.example.attendance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

/**
 * 员工信息实体类
 * 
 * @author System
 * @date 2025-03-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("employee")
public class Employee extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 员工编号
     */
    @TableField("employee_no")
    private String employeeNo;
    
    /**
     * 员工姓名
     */
    @TableField("name")
    private String name;
    
    /**
     * 所属部门
     */
    @TableField("department")
    private String department;
    
    /**
     * 职位
     */
    @TableField("position")
    private String position;
    
    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;
    
    /**
     * 邮箱
     */
    @TableField("email")
    private String email;
    
    /**
     * 入职日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField("hire_date")
    private LocalDate hireDate;
    
    /**
     * 状态：1-在职，0-离职
     */
    @TableField("status")
    private Integer status;
}