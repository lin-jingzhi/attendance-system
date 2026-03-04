-- ===========================================
-- 员工考勤管理系统数据库建表语句
-- 数据库：MySQL 8.0
-- 字符集：utf8mb4
-- 排序规则：utf8mb4_general_ci
-- ===========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS attendance_db 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_general_ci;

USE attendance_db;

-- 员工信息表
CREATE TABLE `employee` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
    `employee_no` varchar(32) NOT NULL COMMENT '员工编号',
    `name` varchar(64) NOT NULL COMMENT '员工姓名',
    `department` varchar(64) NOT NULL COMMENT '所属部门',
    `position` varchar(64) NOT NULL COMMENT '职位',
    `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `hire_date` date NOT NULL COMMENT '入职日期',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1-在职，0-离职',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_no` (`employee_no`),
    KEY `idx_department` (`department`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

-- 考勤规则表
CREATE TABLE `attendance_rule` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name` varchar(64) NOT NULL COMMENT '规则名称',
    `work_start_time` time NOT NULL COMMENT '上班时间',
    `work_end_time` time NOT NULL COMMENT '下班时间',
    `late_threshold` int(11) NOT NULL DEFAULT '30' COMMENT '迟到阈值（分钟）',
    `early_leave_threshold` int(11) NOT NULL DEFAULT '30' COMMENT '早退阈值（分钟）',
    `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认规则：1-是，0-否',
    `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_is_default` (`is_default`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤规则表';

-- 打卡记录表
CREATE TABLE `attendance_record` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
    `record_date` date NOT NULL COMMENT '打卡日期',
    `clock_in_time` datetime DEFAULT NULL COMMENT '上班打卡时间',
    `clock_out_time` datetime DEFAULT NULL COMMENT '下班打卡时间',
    `late_minutes` int(11) NOT NULL DEFAULT '0' COMMENT '迟到分钟数',
    `early_leave_minutes` int(11) NOT NULL DEFAULT '0' COMMENT '早退分钟数',
    `attendance_status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '考勤状态：0-正常，1-迟到，2-早退，3-旷工',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_date` (`employee_id`, `record_date`),
    KEY `idx_record_date` (`record_date`),
    KEY `idx_attendance_status` (`attendance_status`),
    KEY `idx_employee_date_status` (`employee_id`, `record_date`, `attendance_status`),
    CONSTRAINT `fk_attendance_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡记录表';

-- 月度考勤统计表
CREATE TABLE `attendance_statistics` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
    `statistics_month` varchar(7) NOT NULL COMMENT '统计月份（格式：yyyy-MM）',
    `work_days` int(11) NOT NULL DEFAULT '0' COMMENT '应出勤天数',
    `actual_days` int(11) NOT NULL DEFAULT '0' COMMENT '实际出勤天数',
    `late_count` int(11) NOT NULL DEFAULT '0' COMMENT '迟到次数',
    `early_leave_count` int(11) NOT NULL DEFAULT '0' COMMENT '早退次数',
    `absent_count` int(11) NOT NULL DEFAULT '0' COMMENT '旷工次数',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_employee_month` (`employee_id`, `statistics_month`),
    KEY `idx_statistics_month` (`statistics_month`),
    CONSTRAINT `fk_statistics_employee` FOREIGN KEY (`employee_id`) REFERENCES `employee` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度考勤统计表';

-- ===========================================
-- 初始化数据
-- ===========================================

-- 插入默认考勤规则
INSERT INTO `attendance_rule` (`rule_name`, `work_start_time`, `work_end_time`, `late_threshold`, `early_leave_threshold`, `is_default`, `status`) 
VALUES 
('标准考勤规则', '09:00:00', '18:00:00', 30, 30, 1, 1),
('弹性考勤规则', '09:30:00', '18:30:00', 15, 15, 0, 1);

-- 插入示例员工数据
INSERT INTO `employee` (`employee_no`, `name`, `department`, `position`, `phone`, `email`, `hire_date`, `status`) 
VALUES 
('EMP001', '张三', '技术部', 'Java开发工程师', '13800138001', 'zhangsan@example.com', '2024-01-15', 1),
('EMP002', '李四', '技术部', '前端开发工程师', '13800138002', 'lisi@example.com', '2024-02-20', 1),
('EMP003', '王五', '人事部', '人事专员', '13800138003', 'wangwu@example.com', '2024-03-10', 1),
('EMP004', '赵六', '财务部', '会计', '13800138004', 'zhaoliu@example.com', '2024-01-08', 1);

-- 插入示例打卡记录
INSERT INTO `attendance_record` (`employee_id`, `record_date`, `clock_in_time`, `clock_out_time`, `late_minutes`, `early_leave_minutes`, `attendance_status`) 
VALUES 
(1, '2025-03-01', '2025-03-01 08:45:00', '2025-03-01 18:05:00', 0, 0, 0),
(1, '2025-03-02', '2025-03-02 09:35:00', '2025-03-02 17:50:00', 35, 10, 1),
(2, '2025-03-01', '2025-03-01 08:50:00', '2025-03-01 18:10:00', 0, 0, 0),
(3, '2025-03-01', '2025-03-01 09:15:00', '2025-03-01 18:20:00', 15, 0, 0);

-- 插入示例统计数据
INSERT INTO `attendance_statistics` (`employee_id`, `statistics_month`, `work_days`, `actual_days`, `late_count`, `early_leave_count`, `absent_count`) 
VALUES 
(1, '2025-03', 22, 2, 1, 1, 0),
(2, '2025-03', 22, 1, 0, 0, 0),
(3, '2025-03', 22, 1, 0, 0, 0);

-- ===========================================
-- 创建索引优化查询性能
-- ===========================================

-- 为考勤记录表添加复合索引
CREATE INDEX idx_employee_month_status ON attendance_record(employee_id, record_date, attendance_status);

-- 为员工表添加姓名索引
CREATE INDEX idx_employee_name ON employee(name);

-- 为统计表添加月份索引
CREATE INDEX idx_statistics_month_employee ON attendance_statistics(statistics_month, employee_id);

-- ===========================================
-- 数据库用户权限设置（根据实际情况调整）
-- ===========================================

-- 创建应用用户（可选）
-- CREATE USER 'attendance_user'@'%' IDENTIFIED BY 'Attendance123!';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON attendance_db.* TO 'attendance_user'@'%';
-- FLUSH PRIVILEGES;

-- ===========================================
-- 数据库表结构说明
-- ===========================================

-- employee表：存储员工基本信息
-- attendance_rule表：存储考勤规则配置
-- attendance_record表：存储每日打卡记录
-- attendance_statistics表：存储月度统计结果

-- 所有表都包含逻辑删除字段(deleted)和审计字段(create_time, update_time)
-- 使用外键约束保证数据一致性
-- 合理的索引设计提升查询性能