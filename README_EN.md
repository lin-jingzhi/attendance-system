# 🌟 Employee Attendance Management System 🌟

<div style="text-align: center;">

## 📋 Project Overview

　　🎉 Welcome to the Employee Attendance Management System! This is a modern enterprise application based on Spring Boot and Vue.js, designed to help businesses efficiently manage employee attendance data, improve the automation level and accuracy of attendance management. Whether you're a small company or a large group, we've got your attendance management needs covered! 🎉

## 🏗️ System Architecture

### 🛠️ Technology Stack

- **Backend**: Spring Boot 2.7.x, MyBatis-Plus 3.5.x, MySQL 8.0, Redis 6.x 🚀
- **Frontend**: Vue 3, Vite, Bootstrap 5, Chart.js, Axios 💻
- **Configuration Center**: Nacos 🔧
- **Rate Limiting**: Sentinel 🛡️

### 🏛️ System Architecture Diagram

```
+---------------------+
|    Frontend App     |
| (Vue 3 + Bootstrap) |
+---------------------+
          |
          v
+---------------------+
|    REST API Layer   |
|  (Spring Boot)      |
+---------------------+
          |
          v
+---------------------+
|  Business Logic Layer|
|  (Service Layer)    |
+---------------------+
          |
          v
+---------------------+
|  Data Access Layer  |
| (MyBatis-Plus)      |
+---------------------+
          |
          v
+---------------------+
|     Database        |
|  (MySQL + Redis)    |
+---------------------+
```

## 📁 Directory Structure

```
├── attendance-frontend/    # Frontend project
│   ├── src/               # Source code
│   │   ├── components/    # Components
│   │   ├── assets/        # Static resources
│   │   ├── App.vue        # Main application
│   │   └── main.js        # Entry file
│   ├── package.json       # Dependency configuration
│   └── vite.config.js     # Vite configuration
├── attendance-simple/      # Simplified backend
│   ├── src/               # Source code
│   │   └── main/          # Main code
│   └── pom.xml            # Maven configuration
├── attendance-system/      # Complete backend
│   ├── src/               # Source code
│   │   ├── main/          # Main code
│   │   └── test/          # Test code
│   ├── docs/              # Documentation
│   └── pom.xml            # Maven configuration
├── README.md              # Project description (Chinese)
└── README_EN.md           # Project description (English)
```

## ✨ Core Features

### 1. 👥 Employee Management

　　👨‍💼 Add, edit, and delete employee information, employee list display, employee status management (active/terminated) — manage all your company personnel with ease!

### 2. 📅 Attendance Management

　　⏰ Employee clock-in/clock-out, attendance record query, automatic attendance status calculation (normal/late/early leave/absent) — no more worrying about attendance calculation errors!

### 3. 📊 Statistical Reports

　　📈 Attendance statistics charts, employee department distribution charts, detailed statistical data tables, support for monthly query — data visualization makes management more intuitive!

### 4. ⚙️ System Management

　　🔐 Login/logout functionality, system configuration management, data cache management — ensuring system security and stable operation!

## 🌟 Technical Features

1. **Frontend-Backend Separation**: Adopts Vue 3 + Spring Boot frontend-backend separation architecture, improving development efficiency and system maintainability. 🎯

2. **Responsive Design**: Frontend uses Bootstrap 5 to implement responsive layout, adapting to different device screens. 📱

3. **Data Visualization**: Uses Chart.js to implement data visualization, intuitively displaying attendance statistical data. 📊

4. **Cache Optimization**: Uses Redis for data caching, improving system performance. ⚡

5. **Rate Limiting Protection**: Integrates Sentinel for API rate limiting, protecting system security. 🛡️

6. **Configuration Center**: Uses Nacos as configuration center, facilitating system configuration management. 🔧

## 🚀 Quick Start

### Frontend Start

```bash
# Enter frontend directory
cd attendance-frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

### Backend Start

```bash
# Enter backend directory
cd attendance-simple

# Start backend service
mvn spring-boot:run
```

## 🔌 System APIs

### Employee Management APIs

- `GET /attendance/employee/list` - Get employee list
- `POST /attendance/employee/add` - Add employee

### Attendance Management APIs

- `POST /attendance/record/clockin` - Employee clock-in
- `GET /attendance/record/list` - Get attendance records

### System Information API

- `GET /attendance/info` - Get system information

## 🗄️ Database Design

### Employee Table (`employee`)

| Field Name | Data Type | Description |
|-----------|-----------|-------------|
| id | BIGINT | Primary key |
| employee_no | VARCHAR | Employee number |
| name | VARCHAR | Name |
| department | VARCHAR | Department |
| position | VARCHAR | Position |
| status | INT | Status (1-active, 0-terminated) |
| create_time | DATETIME | Creation time |
| update_time | DATETIME | Update time |

### Attendance Record Table (`attendance_record`)

| Field Name | Data Type | Description |
|-----------|-----------|-------------|
| id | BIGINT | Primary key |
| employee_id | BIGINT | Employee ID |
| record_date | DATE | Record date |
| clock_in_time | DATETIME | Clock-in time |
| clock_out_time | DATETIME | Clock-out time |
| attendance_status | INT | Attendance status (0-normal, 1-late, 2-early leave, 3-absent) |
| create_time | DATETIME | Creation time |
| update_time | DATETIME | Update time |

### Attendance Rule Table (`attendance_rule`)

| Field Name | Data Type | Description |
|-----------|-----------|-------------|
| id | BIGINT | Primary key |
| work_start_time | VARCHAR | Work start time |
| work_end_time | VARCHAR | Work end time |
| late_threshold | INT | Late threshold (minutes) |
| early_leave_threshold | INT | Early leave threshold (minutes) |
| create_time | DATETIME | Creation time |
| update_time | DATETIME | Update time |

### Attendance Statistics Table (`attendance_statistics`)

| Field Name | Data Type | Description |
|-----------|-----------|-------------|
| id | BIGINT | Primary key |
| employee_id | BIGINT | Employee ID |
| year_month | VARCHAR | Year-month (format: YYYY-MM) |
| normal_days | INT | Normal attendance days |
| late_count | INT | Late count |
| early_leave_count | INT | Early leave count |
| absent_count | INT | Absent count |
| create_time | DATETIME | Creation time |
| update_time | DATETIME | Update time |

## 📦 System Deployment

### Frontend Deployment

```bash
# Enter frontend directory
cd attendance-frontend

# Build production version
npm run build

# Deploy dist directory to web server
```

### Backend Deployment

```bash
# Enter backend directory
cd attendance-system

# Build jar package
mvn clean package

# Run jar package
java -jar target/attendance-system-1.0.0.jar
```

---

<div style="text-align: center;">
　　🎊 © 2026 Employee Attendance Management System. All rights reserved. 🎊
</div>

</div>