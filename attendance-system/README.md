# 员工考勤管理系统

基于SpringBoot开发的员工考勤管理系统，支持员工信息管理、打卡记录、考勤统计等功能。

## 技术栈

- **后端框架**: SpringBoot 2.7.x
- **ORM框架**: MyBatis-Plus 3.5.x
- **数据库**: MySQL 8.0
- **缓存**: Redis 6.x
- **配置中心**: Nacos 2.2.x
- **限流熔断**: Sentinel 1.8.x
- **Excel处理**: EasyExcel 3.3.x

## 功能特性

### 核心功能
1. **员工信息管理**
   - 员工信息的增删改查
   - Excel导入导出员工信息
   - 按部门查询员工

2. **打卡记录管理**
   - 手动录入打卡记录
   - 批量Excel导入打卡记录
   - 自动计算考勤状态（迟到、早退、旷工）

3. **考勤规则配置**
   - 上班/下班时间配置
   - 迟到/早退阈值设置
   - 默认规则管理

4. **考勤统计报表**
   - 月度考勤统计生成
   - 迟到/早退/旷工次数统计
   - 报表导出功能

### 技术特性
1. **Redis缓存**
   - 员工信息缓存（TTL=24h）
   - 考勤规则缓存（TTL=24h）

2. **Sentinel限流**
   - 打卡录入接口QPS限制为30
   - 异常时返回"当前请求过多，请稍后再试"

3. **Nacos配置中心**
   - 所有配置托管到Nacos
   - 动态配置更新

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.3+

### 数据库初始化

1. 创建数据库
```sql
CREATE DATABASE attendance_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 执行建表语句
```bash
mysql -u root -p attendance_db < docs/sql/create_tables.sql
```

### Nacos配置

1. 启动Nacos服务
```bash
# 下载并启动Nacos
sh startup.sh -m standalone
```

2. 导入配置到Nacos
   - 访问Nacos控制台: http://localhost:8848/nacos
   - 创建以下配置文件：
     - `datasource-config.yaml`
     - `redis-config.yaml` 
     - `attendance-config.yaml`

### Redis配置

1. 启动Redis服务
```bash
redis-server
```

### 项目启动

1. 编译项目
```bash
mvn clean compile
```

2. 运行项目
```bash
mvn spring-boot:run
```

3. 访问应用
```
http://localhost:8080/attendance
```

## API接口文档

### 员工管理接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /employee/list | 获取所有员工列表 |
| GET | /employee/{id} | 根据ID获取员工信息 |
| GET | /employee/no/{employeeNo} | 根据员工编号获取员工信息 |
| GET | /employee/department/{department} | 根据部门查询员工 |
| POST | /employee/add | 新增员工 |
| PUT | /employee/update | 修改员工信息 |
| DELETE | /employee/{id} | 删除员工 |
| POST | /employee/import | 导入员工信息Excel |
| GET | /employee/export | 导出员工信息到Excel |

### 打卡记录接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /record/clockin | 手动录入打卡记录 |
| POST | /record/import | 批量导入打卡记录Excel |
| GET | /record/employee/{employeeId}/date/{recordDate} | 根据员工ID和日期查询打卡记录 |
| GET | /record/range | 根据日期范围查询打卡记录 |
| PUT | /record/update | 修改打卡记录 |
| DELETE | /record/{id} | 删除打卡记录 |

### 考勤规则接口

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | /rule/default | 获取默认考勤规则 |
| GET | /rule/enabled | 获取所有启用的考勤规则 |
| POST | /rule/add | 新增考勤规则 |
| PUT | /rule/update | 修改考勤规则 |
| PUT | /rule/default/{ruleId} | 设置默认规则 |

### 考勤统计接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /statistics/generate/{month} | 生成月度考勤统计 |
| GET | /statistics/month/{month} | 根据月份查询统计结果 |
| GET | /statistics/employee/{employeeId}/month/{month} | 根据员工ID和月份查询统计结果 |
| GET | /statistics/export/{month} | 导出月度统计报表 |

## 配置说明

### 数据库配置

在Nacos中配置`datasource-config.yaml`:
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/attendance_db?useUnicode=true&characterEncoding=utf8
    username: root
    password: 123456
```

### Redis配置

在Nacos中配置`redis-config.yaml`:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
```

### 业务配置

在Nacos中配置`attendance-config.yaml`:
```yaml
attendance:
  default-rule:
    work-start-time: "09:00:00"
    work-end-time: "18:00:00"
    late-threshold: 30
    early-leave-threshold: 30
```

## 缓存策略

### Redis缓存使用

1. **员工信息缓存**
   - Key格式: `employee:{employeeNo}`
   - TTL: 24小时

2. **考勤规则缓存**
   - Key格式: `attendanceRule:default`
   - TTL: 24小时

### 缓存清除时机
- 新增/修改/删除员工信息时清除相关缓存
- 修改考勤规则时清除规则缓存

## 限流配置

### Sentinel规则

1. **打卡接口限流**
   - 资源名: `clockIn`
   - QPS限制: 30
   - 降级策略: 直接拒绝，返回429状态码

2. **配置方式**
```java
@SentinelResource(value = "clockIn", blockHandler = "clockInBlockHandler")
public boolean clockIn(AttendanceRecord record) {
    // 业务逻辑
}
```

## 项目结构

```
attendance-system/
├── src/main/java/com/example/attendance/
│   ├── entity/                 # 实体类
│   ├── mapper/                 # MyBatis Mapper接口
│   ├── service/               # 服务层接口和实现
│   ├── controller/             # 控制器层
│   ├── config/                # 配置类
│   └── AttendanceSystemApplication.java # 启动类
├── src/main/resources/
│   ├── application.yml         # 应用配置
│   └── bootstrap.yml          # Spring Cloud配置
├── docs/
│   ├── sql/                   # 数据库脚本
│   └── nacos/                 # Nacos配置文件
└── pom.xml                    # Maven依赖配置
```

## 开发说明

### 代码规范

1. **命名规范**
   - 实体类使用大驼峰命名法
   - 数据库表名和字段名使用下划线命名法
   - 接口方法名要有明确的业务含义

2. **注释规范**
   - 类和方法要有详细的JavaDoc注释
   - 复杂业务逻辑要有行内注释
   - 关键配置要有配置说明

### 扩展性设计

1. **微服务预留**
   - 使用Nacos作为配置中心，便于微服务拆分
   - 接口设计符合RESTful规范
   - 服务间调用预留Feign客户端接口

2. **配置化设计**
   - 所有业务参数配置化
   - 支持动态配置更新
   - 配置项有合理的默认值

## 常见问题

### Q: 项目启动失败
A: 检查以下配置：
- MySQL服务是否启动
- Redis服务是否启动  
- Nacos服务是否启动
- 数据库连接配置是否正确

### Q: 导入Excel失败
A: 检查以下内容：
- Excel文件格式是否正确
- 必填字段是否完整
- 员工编号是否重复

### Q: 缓存不生效
A: 检查以下配置：
- Redis连接是否正常
- 缓存注解是否正确使用
- 缓存Key是否冲突

## 版本信息

- v1.0.0: 基础功能实现
  - 员工信息管理
  - 打卡记录管理
  - 考勤统计报表
  - Redis缓存集成
  - Sentinel限流集成
  - Nacos配置中心集成

## 联系方式

如有问题或建议，请联系开发团队。