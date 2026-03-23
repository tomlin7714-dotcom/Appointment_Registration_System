# 医疗预约系统 - 前端界面

本项目包含三个前端界面：管理员网页端、医生网页端、用户微信小程序端。

## 目录结构

```
frontend/
├── admin/          # 管理员网页端
│   ├── login.html  # 登录页面
│   └── index.html  # 管理后台主页面
├── doctor/         # 医生网页端
│   ├── login.html  # 登录页面
│   └── index.html  # 医生工作台主页面
└── wechat/         # 用户微信小程序端
    ├── app.js      # 小程序入口
    ├── app.json    # 小程序配置
    ├── app.wxss    # 全局样式
    ├── pages/      # 页面目录
    │   ├── index/          # 首页
    │   ├── login/          # 登录页
    │   ├── register/       # 注册页
    │   ├── appointment/    # 预约挂号页
    │   ├── appointment-list/ # 预约列表页
    │   ├── profile/        # 个人中心页
    │   └── ...
    └── images/     # 图片资源
```

## 使用说明

### 1. 管理员网页端

**访问地址：**
- 登录页面：`http://localhost:8083/admin/login.html`
- 管理后台：`http://localhost:8083/admin/index.html`

**默认账号：**
- 账号：admin
- 密码：admin123

**功能模块：**
- 系统概览（数据统计）
- 科室管理（增删改查）
- 医生管理（增删改查）
- 用户管理（查看、禁用）
- 系统日志
- 系统配置

### 2. 医生网页端

**访问地址：**
- 登录页面：`http://localhost:8083/doctor/login.html`
- 工作台：`http://localhost:8083/doctor/index.html`

**默认账号：**
- 账号：doctor1
- 密码：123456

**功能模块：**
- 工作台概览
- 排班管理（设置出诊时间）
- 号源管理（发布号源）
- 预约列表（查看预约）
- 诊间叫号（语音叫号）
- 个人信息管理

### 3. 用户微信小程序端

**开发环境：**
- 微信开发者工具
- 小程序AppID（需要申请）

**使用步骤：**
1. 打开微信开发者工具
2. 选择"导入项目"
3. 选择 `frontend/wechat` 目录
4. 填写AppID（或使用测试号）
5. 点击"导入"

**功能模块：**
- 首页（科室推荐、医生推荐）
- 科室列表
- 医生列表
- 预约挂号
- 预约管理
- 个人中心

## 后端接口配置

所有前端页面默认连接的后端地址为：`http://localhost:8083`

如需修改，请编辑以下文件：
- 管理员/医生端：`login.html` 和 `index.html` 中的 `API_BASE_URL`
- 小程序端：`app.js` 中的 `apiBaseUrl`

## 启动步骤

### 1. 启动后端服务

```bash
cd c:\java\Appointment_Registration_System
mvn spring-boot:run
```

### 2. 访问网页端

直接在浏览器中打开：
- 管理员：`http://localhost:8083/admin/login.html`
- 医生：`http://localhost:8083/doctor/login.html`

### 3. 运行小程序

1. 打开微信开发者工具
2. 导入 `frontend/wechat` 项目
3. 点击"编译"运行

## 注意事项

1. **跨域问题**：后端已配置CORS，支持跨域访问
2. **登录状态**：网页端使用localStorage存储token，小程序使用wx.setStorageSync
3. **图片资源**：小程序需要准备tabBar图标，放在images目录下
4. **后端接口**：确保后端服务运行在8083端口

## 技术栈

- **管理员/医生端**：HTML5 + CSS3 + JavaScript（原生）
- **小程序端**：微信小程序原生框架
- **后端**：SpringBoot + MyBatis + MySQL

## 后续优化建议

1. 使用Vue.js或React重构网页端，提升开发效率
2. 添加前端路由管理
3. 实现响应式布局，适配移动端
4. 添加表单验证和错误处理
5. 优化UI设计，提升用户体验