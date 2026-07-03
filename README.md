# 🏥 医疗预约挂号系统

> 基于 Spring Boot + MyBatis + 微信小程序的医疗预约挂号平台

---

## 📋 项目概述

本项目是一个功能完备的医疗预约挂号系统，提供患者在线预约、医生排班管理、诊间叫号、问诊记录、AI导诊等功能。系统分为三个终端：

- **微信小程序端** — 患者注册登录、预约挂号、问诊记录、AI导诊
- **医生端（PC浏览器）** — 排班管理、号源管理、诊间叫号、问诊诊断
- **管理端（PC浏览器）** — 科室/医生/用户管理、问诊记录管理、系统概览

---

## 🛠 技术栈

| 层 | 技术 | 版本 |
|---|------|:----:|
| 后端框架 | Spring Boot | 2.7.x |
| ORM | MyBatis | 2.2.x |
| 数据库 | MySQL | 8.0 |
| 前端（管理/医生） | 纯 HTML + CSS + JavaScript | - |
| 前端（患者） | 微信小程序 | - |
| 构建工具 | Maven | 3.x |
| JDK | OpenJDK | 1.8 |
| AI接口 | DeepSeek API | - |

> ⚠️ 课设要求使用 Vue3 + Unibest，但因时间紧迫选用了当前技术栈。详见 `docs/08-答辩文档.md`。

---

## 🚀 快速开始

### 1. 环境准备

- JDK 1.8+
- MySQL 8.0+
- Maven 3.x
- 微信开发者工具（用于小程序端）

### 2. 数据库初始化

```sql
-- 执行数据库初始化脚本
source sql/database.sql;

-- （可选）执行问诊单表创建
source sql/create_consultation_form.sql;

-- （可选）执行科室区域诊室字段更新
source sql/update_dept_table.sql;

-- （可选）插入测试用户数据
source sql/insert_test_users.sql;
```

### 3. 配置文件

编辑 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/hospital_registration_system?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码

# AI导诊（可选）
ai.deepseek.apiKey=你的deepseek_api_key
```

### 4. 启动项目

```bash
mvn spring-boot:run
# 或直接运行 AppointmentApplication.java
```

启动后访问：
- 管理端：http://localhost:8083/admin/login.html
- 医生端：http://localhost:8083/doctor/login.html
- 微信小程序：在微信开发者工具中导入 `frontend/wechat` 目录

### 5. 默认账号

| 角色 | 账号 | 密码 |
|------|------|:----:|
| 管理员 | admin | admin123 |
| 医生 | D001 | 123456 |
| 用户 | 13800138003 | 123456 |

---

## 📁 项目结构

```
Appointment_Registration_System/
├── src/main/java/com/appointment/
│   ├── AppointmentApplication.java       # 启动类
│   ├── config/
│   │   ├── SecurityConfig.java           # Spring Security安全配置
│   │   └── WebMvcConfig.java             # 静态资源映射
│   ├── controller/
│   │   ├── TestController.java           # 数据库测试
│   │   ├── AiTriageController.java       # AI导诊接口
│   │   ├── admin/
│   │   │   ├── AdminLoginController.java # 管理员登录
│   │   │   ├── AdminStatsController.java # 系统概览统计
│   │   │   ├── DeptController.java       # 科室管理
│   │   │   ├── DoctorController.java     # 医生管理
│   │   │   ├── UserController.java       # 用户管理
│   │   │   ├── ConsultationManageController.java # 问诊记录管理
│   │   │   ├── LogController.java        # 系统日志
│   │   │   └── SysParamController.java   # 系统参数
│   │   ├── doctor/
│   │   │   ├── DoctorLoginController.java # 医生登录
│   │   │   └── DoctorWorkController.java  # 医生工作台(排班/叫号/诊断)
│   │   └── user/
│   │       ├── UserLoginController.java   # 用户登录/注册
│   │       ├── UserInfoController.java    # 用户信息/头像
│   │       ├── AppointmentController.java # 预约/支付/取消
│   │       └── ConsultationFormController.java # 问诊单
│   ├── entity/                           # 实体类
│   ├── mapper/                           # MyBatis Mapper接口
│   ├── scheduler/
│   │   ├── ScheduleInitializer.java      # 启动初始化排班
│   │   └── ScheduleAutoTask.java         # 每日自动排班
│   ├── util/JwtUtil.java                 # JWT工具
│   └── vo/ResponseVo.java               # 统一返回值
├── src/main/resources/
│   ├── application.properties            # 主配置
│   ├── application-secret.properties     # 敏感配置(已gitignore)
│   └── mapper/                           # MyBatis XML映射
├── frontend/
│   ├── admin/                            # 管理端HTML
│   │   ├── login.html                    # 登录页
│   │   └── index.html                    # 后台管理页
│   ├── doctor/                           # 医生端HTML
│   │   ├── login.html                    # 登录页
│   │   └── index.html                    # 医生工作台
│   └── wechat/                           # 微信小程序
│       ├── app.js / app.json / app.wxss
│       └── pages/
│           ├── index/                    # 首页
│           ├── login/                    # 登录
│           ├── register/                 # 注册
│           ├── booking/                  # 预约挂号(多步流程)
│           ├── appointment/              # 选择时段
│           ├── appointment-list/         # 预约列表
│           ├── dept/                     # 科室列表
│           ├── doctor/                   # 医生列表
│           ├── profile/                  # 个人中心
│           ├── edit-profile/             # 编辑个人信息
│           ├── schedule/                 # 排班
│           ├── consultation-list/        # 问诊历史列表
│           ├── consultation-detail/      # 问诊详情
│           └── ai-triage/               # AI导诊
├── sql/                                  # SQL 脚本
├── uploads/avatars/                      # 头像上传目录(已gitignore)
└── docs/                                 # 项目文档
```

---

## 🎯 核心功能

### 患者端（微信小程序）
- 🔐 **注册登录** — 手机号注册/登录
- 📅 **预约挂号** — 多步选择(科室→日期→医生→时段)，填写问诊信息
- 💳 **支付** — 模拟支付流程
- 📋 **预约管理** — 查看预约列表，按状态筛选，取消预约
- 📝 **问诊记录** — 查看问诊历史、详情，支持搜索
- 🤖 **AI导诊** — 输入症状，AI推荐科室
- 👤 **个人信息** — 编辑资料、上传头像

### 医生端
- 📊 **工作台** — 今日概览统计
- 🗓️ **排班管理** — 自动/手动排班，号源管理
- 🔊 **诊间叫号** — 叫号、重叫、语音播报
- 💊 **问诊诊断** — 查看患者问诊信息，填写检查/诊断/治疗
- 📋 **预约列表** — 查询、编辑、取消

### 管理端
- 📊 **系统概览** — 统计数据
- 🏥 **科室管理** — 增删改查
- 👨‍⚕️ **医生管理** — 增删改查，自动排班
- 👥 **用户管理** — 增删改查，筛选
- 📄 **问诊记录管理** — 查询、编辑、状态管理

---

## 📚 文档索引

| 文档 | 说明 |
|------|------|
| `docs/01-需求规格说明书.md` | 功能/非功能需求、用例图、数据字典 |
| `docs/02-业务流程图.md` | PlantUML业务流程图 |
| `docs/03-数据库设计文档.md` | E-R图、表结构、索引设计 |
| `docs/04-接口文档.md` | API接口说明、参数、调用示例 |
| `docs/05-测试用例与问题记录.md` | 测试用例、开发问题记录 |
| `docs/07-演示PPT.md` | 项目演示幻灯片 |
| `docs/08-答辩文档.md` | 答辩要点、技术选型说明 |

---

## ⚙️ 配置说明

### AI 导诊配置
在 `application-secret.properties` 中配置（不会提交到 Git）：
```properties
ai.deepseek.apiKey=你的DeepSeek API Key
```

### 敏感信息保护
- API Key 存放在 `application-secret.properties`（已加入 `.gitignore`）
- `application.properties` 通过 `spring.config.import` 引入
- 生产环境中建议使用环境变量

---

## 👨‍💻 开发团队

- 项目名称：医疗预约挂号系统
- 开发工具：IntelliJ IDEA + 微信开发者工具 + MySQL Workbench
- 版本控制：Git + GitHub
- 技术参考：Spring Boot官方文档、微信小程序开发文档
