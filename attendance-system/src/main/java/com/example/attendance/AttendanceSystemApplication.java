package com.example.attendance;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * 员工考勤管理系统启动类
 * 
 * @author System
 * @date 2025-03-04
 */
@SpringBootApplication
@MapperScan("com.example.attendance.mapper")
@EnableCaching
public class AttendanceSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AttendanceSystemApplication.class, args);
        System.out.println("==========================================");
        System.out.println("员工考勤管理系统启动成功！");
        System.out.println("服务地址: http://localhost:8080/attendance");
        System.out.println("==========================================");
    }
}