# 员工考勤管理系统

## 项目概述

员工考勤管理系统是一个基于Spring Boot和Vue.js的现代化企业级应用，旨在帮助企业高效管理员工考勤数据，提高考勤管理的自动化水平和准确性。

## 系统架构

### 技术栈

- **后端**：Spring Boot 2.7.x、MyBatis-Plus 3.5.x、MySQL 8.0、Redis 6.x
- **前端**：Vue 3、Vite、Bootstrap 5、Chart.js、Axios
- **配置中心**：Nacos
- **限流**：Sentinel

### 系统架构图

```
+---------------------+
|      前端应用        |
| (Vue 3 + Bootstrap) |
+---------------------+
          |
          v
+---------------------+
|     REST API层      |
|  (Spring Boot)      |
+---------------------+
          |
          v
+---------------------+
|    业务逻辑层       |
|  (Service 层)       |
+---------------------+
          |
          v
+---------------------+
|    数据访问层       |
| (MyBatis-Plus)      |
+---------------------+
          |
          v
+---------------------+
|      数据库         |
|  (MySQL + Redis)    |
+---------------------+
```

## 目录结构

```
├── attendance-frontend/    # 前端项目
│   ├── src/               # 源代码
│   │   ├── components/    # 组件
│   │   ├── assets/        # 静态资源
│   │   ├── App.vue        # 主应用
│   │   └── main.js        # 入口文件
│   ├── package.json       # 依赖配置
│   └── vite.config.js     # Vite配置
├── attendance-simple/      # 简化版后端
│   ├── src/               # 源代码
│   │   └── main/          # 主代码
│   └── pom.xml            # Maven配置
├── attendance-system/      # 完整后端
│   ├── src/               # 源代码
│   │   ├── main/          # 主代码
│   │   └── test/          # 测试代码
│   ├── docs/              # 文档
│   └── pom.xml            # Maven配置
├── README.md              # 项目说明
└── README_EN.md           # 英文说明
```

## 核心功能

### 1. 员工管理

- 员工信息的添加、编辑、删除
- 员工列表展示
- 员工状态管理（在职/离职）

### 2. 考勤管理

- 员工打卡（上班/下班）
- 打卡记录查询
- 考勤状态自动计算（正常/迟到/早退/旷工）

### 3. 统计报表

- 考勤情况统计图表
- 员工部门分布图表
- 详细统计数据表格
- 支持按月份查询

### 4. 系统管理

- 登录/退出功能
- 系统配置管理
- 数据缓存管理

## 技术特点

1. **前后端分离**：采用Vue 3 + Spring Boot的前后端分离架构，提高开发效率和系统可维护性。

2. **响应式设计**：前端使用Bootstrap 5实现响应式布局，适配不同设备屏幕。

3. **数据可视化**：使用Chart.js实现数据可视化，直观展示考勤统计数据。

4. **缓存优化**：使用Redis进行数据缓存，提高系统性能。

5. **限流保护**：集成Sentinel进行API限流，保护系统安全。

6. **配置中心**：使用Nacos作为配置中心，方便系统配置管理。

## 快速开始

### 前端启动

```bash
# 进入前端目录
cd attendance-frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

### 后端启动

```bash
# 进入后端目录
cd attendance-simple

# 启动后端服务
mvn spring-boot:run
```

## 系统接口

### 员工管理接口

- `GET /attendance/employee/list` - 获取员工列表
- `POST /attendance/employee/add` - 添加员工

### 考勤管理接口

- `POST /attendance/record/clockin` - 员工打卡
- `GET /attendance/record/list` - 获取打卡记录

### 系统信息接口

- `GET /attendance/info` - 获取系统信息

## 数据库设计

### 员工表 (`employee`)

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 主键 |
| employee_no | VARCHAR | 员工编号 |
| name | VARCHAR | 姓名 |
| department | VARCHAR | 部门 |
| position | VARCHAR | 职位 |
| status | INT | 状态（1-在职，0-离职） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 考勤记录表 (`attendance_record`)

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 主键 |
| employee_id | BIGINT | 员工ID |
| record_date | DATE | 打卡日期 |
| clock_in_time | DATETIME | 上班时间 |
| clock_out_time | DATETIME | 下班时间 |
| attendance_status | INT | 考勤状态（0-正常，1-迟到，2-早退，3-旷工） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 考勤规则表 (`attendance_rule`)

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 主键 |
| work_start_time | VARCHAR | 上班时间 |
| work_end_time | VARCHAR | 下班时间 |
| late_threshold | INT | 迟到阈值（分钟） |
| early_leave_threshold | INT | 早退阈值（分钟） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 考勤统计表 (`attendance_statistics`)

| 字段名 | 数据类型 | 描述 |
|-------|---------|------|
| id | BIGINT | 主键 |
| employee_id | BIGINT | 员工ID |
| year_month | VARCHAR | 年月（格式：YYYY-MM） |
| normal_days | INT | 正常出勤天数 |
| late_count | INT | 迟到次数 |
| early_leave_count | INT | 早退次数 |
| absent_count | INT | 旷工次数 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## 系统部署

### 前端部署

```bash
# 进入前端目录
cd attendance-frontend

# 构建生产版本
npm run build

# 将dist目录部署到Web服务器
```

### 后端部署

```bash
# 进入后端目录
cd attendance-system

# 构建jar包
mvn clean package

# 运行jar包
java -jar target/attendance-system-1.0.0.jar
```

---

© 2026 员工考勤管理系统. 保留所有权利。