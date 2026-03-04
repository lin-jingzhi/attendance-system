package com.example.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简化版员工考勤管理系统
 */
@SpringBootApplication
public class AttendanceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
        System.out.println("==========================================");
        System.out.println("员工考勤管理系统启动成功！");
        System.out.println("服务地址: http://localhost:8080/attendance");
        System.out.println("==========================================");
    }
}

/**
 * 员工实体类
 */
class Employee {
    private Long id;
    private String employeeNo;
    private String name;
    private String department;
    private String position;
    private Integer status; // 1-在职，0-离职
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEmployeeNo() { return employeeNo; }
    public void setEmployeeNo(String employeeNo) { this.employeeNo = employeeNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

/**
 * 打卡记录实体类
 */
class AttendanceRecord {
    private Long id;
    private Long employeeId;
    private LocalDate recordDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Integer attendanceStatus; // 0-正常，1-迟到，2-早退，3-旷工
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }
    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }
    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }
    public Integer getAttendanceStatus() { return attendanceStatus; }
    public void setAttendanceStatus(Integer attendanceStatus) { this.attendanceStatus = attendanceStatus; }
}

/**
 * 考勤规则实体类
 */
class AttendanceRule {
    private String workStartTime;
    private String workEndTime;
    private Integer lateThreshold;
    private Integer earlyLeaveThreshold;
    
    public String getWorkStartTime() { return workStartTime; }
    public void setWorkStartTime(String workStartTime) { this.workStartTime = workStartTime; }
    public String getWorkEndTime() { return workEndTime; }
    public void setWorkEndTime(String workEndTime) { this.workEndTime = workEndTime; }
    public Integer getLateThreshold() { return lateThreshold; }
    public void setLateThreshold(Integer lateThreshold) { this.lateThreshold = lateThreshold; }
    public Integer getEarlyLeaveThreshold() { return earlyLeaveThreshold; }
    public void setEarlyLeaveThreshold(Integer earlyLeaveThreshold) { this.earlyLeaveThreshold = earlyLeaveThreshold; }
}

/**
 * 考勤管理控制器
 */
@RestController
@RequestMapping("/attendance")
class AttendanceController {
    
    // 内存存储
    private Map<Long, Employee> employees = new ConcurrentHashMap<>();
    private Map<Long, AttendanceRecord> records = new ConcurrentHashMap<>();
    private long nextId = 1;
    
    // 默认考勤规则
    private AttendanceRule defaultRule = new AttendanceRule();
    
    public AttendanceController() {
        // 初始化默认规则
        defaultRule.setWorkStartTime("09:00:00");
        defaultRule.setWorkEndTime("18:00:00");
        defaultRule.setLateThreshold(30);
        defaultRule.setEarlyLeaveThreshold(30);
        
        // 初始化示例数据
        Employee emp1 = new Employee();
        emp1.setId(nextId++);
        emp1.setEmployeeNo("EMP001");
        emp1.setName("张三");
        emp1.setDepartment("技术部");
        emp1.setPosition("Java开发工程师");
        emp1.setStatus(1);
        employees.put(emp1.getId(), emp1);
        
        Employee emp2 = new Employee();
        emp2.setId(nextId++);
        emp2.setEmployeeNo("EMP002");
        emp2.setName("李四");
        emp2.setDepartment("技术部");
        emp2.setPosition("前端开发工程师");
        emp2.setStatus(1);
        employees.put(emp2.getId(), emp2);
    }
    
    /**
     * 获取所有员工
     */
    @GetMapping("/employee/list")
    public List<Employee> listEmployees() {
        return new ArrayList<>(employees.values());
    }
    
    /**
     * 添加员工
     */
    @PostMapping("/employee/add")
    public Employee addEmployee(@RequestBody Employee employee) {
        employee.setId(nextId++);
        employees.put(employee.getId(), employee);
        return employee;
    }
    
    /**
     * 打卡
     */
    @PostMapping("/record/clockin")
    public AttendanceRecord clockIn(@RequestBody AttendanceRecord record) {
        record.setId(nextId++);
        // 简单的考勤状态计算
        record.setAttendanceStatus(0); // 默认为正常
        records.put(record.getId(), record);
        return record;
    }
    
    /**
     * 获取所有打卡记录
     */
    @GetMapping("/record/list")
    public List<AttendanceRecord> listRecords() {
        return new ArrayList<>(records.values());
    }
    
    /**
     * 获取系统信息
     */
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        Map<String, Object> info = new ConcurrentHashMap<>();
        info.put("employees", employees.size());
        info.put("records", records.size());
        info.put("rule", defaultRule);
        info.put("status", "running");
        return info;
    }
}