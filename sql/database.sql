-- 创建数据库
CREATE DATABASE IF NOT EXISTS hospital_registration_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hospital_registration_system;

-- 管理员信息表
CREATE TABLE IF NOT EXISTS admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    admin_account VARCHAR(50) NOT NULL UNIQUE,
    pwd VARCHAR(100) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_time DATETIME
);

-- 用户信息表
CREATE TABLE IF NOT EXISTS user (
    id INT PRIMARY KEY AUTO_INCREMENT,
    phone VARCHAR(20) NOT NULL UNIQUE,
    pwd VARCHAR(100) NOT NULL,
    real_name VARCHAR(50) NOT NULL,
    id_card VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    last_login_time DATETIME,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-启用，1-禁用'
);

-- 科室信息表
CREATE TABLE IF NOT EXISTS dept (
    id INT PRIMARY KEY AUTO_INCREMENT,
    dept_name VARCHAR(50) NOT NULL UNIQUE,
    dept_desc VARCHAR(255),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-启用，1-禁用'
);

-- 医生信息表
CREATE TABLE IF NOT EXISTS doctor (
    id INT PRIMARY KEY AUTO_INCREMENT,
    work_no VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    title VARCHAR(50) NOT NULL,
    dept_id INT NOT NULL,
    phone VARCHAR(100) NOT NULL,
    intro VARCHAR(255),
    specialty VARCHAR(255),
    pwd VARCHAR(100) NOT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-启用，1-禁用',
    FOREIGN KEY (dept_id) REFERENCES dept(id)
);

-- 排班信息表
CREATE TABLE IF NOT EXISTS schedule (
    id INT PRIMARY KEY AUTO_INCREMENT,
    doctor_id INT NOT NULL,
    visit_date DATE NOT NULL,
    visit_time VARCHAR(50) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-已设置，1-临时停诊',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

-- 号源信息表
CREATE TABLE IF NOT EXISTS number_source (
    id INT PRIMARY KEY AUTO_INCREMENT,
    schedule_id INT NOT NULL,
    total_num INT NOT NULL,
    remain_num INT NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-已发布，1-已下架，2-已约满，3-已结束',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    FOREIGN KEY (schedule_id) REFERENCES schedule(id)
);

-- 预约信息表
CREATE TABLE IF NOT EXISTS appointment (
    id INT PRIMARY KEY AUTO_INCREMENT,
    number_source_id INT NOT NULL,
    user_id INT NOT NULL,
    patient_name VARCHAR(50) NOT NULL,
    patient_id_card VARCHAR(100) NOT NULL,
    fee DECIMAL(10,2) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-待就诊，1-已就诊',
    appointment_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    pay_time DATETIME,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    FOREIGN KEY (number_source_id) REFERENCES number_source(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 支付信息表
CREATE TABLE IF NOT EXISTS pay (
    id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    pay_type TINYINT NOT NULL DEFAULT 0 COMMENT '0-微信支付',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-未支付，1-已支付',
    wx_trade_no VARCHAR(100),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointment(id)
);

-- 消息推送日志表
CREATE TABLE IF NOT EXISTS msg_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    push_type TINYINT NOT NULL COMMENT '0-预约成功，1-就诊前提醒',
    push_content VARCHAR(500) NOT NULL,
    status TINYINT NOT NULL DEFAULT 0 COMMENT '0-失败，1-成功',
    push_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    retry_count INT NOT NULL DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES user(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operate_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    operator_id INT NOT NULL,
    operator_role TINYINT NOT NULL COMMENT '0-用户，1-医生，2-管理员',
    content VARCHAR(500) NOT NULL,
    operate_ip VARCHAR(50) NOT NULL,
    operate_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 错误日志表
CREATE TABLE IF NOT EXISTS error_log (
    id INT PRIMARY KEY AUTO_INCREMENT,
    error_type VARCHAR(100) NOT NULL,
    error_msg VARCHAR(1000) NOT NULL,
    request_url VARCHAR(255) NOT NULL,
    request_param VARCHAR(1000),
    operate_ip VARCHAR(50) NOT NULL,
    error_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 系统参数表
CREATE TABLE IF NOT EXISTS sys_param (
    id INT PRIMARY KEY AUTO_INCREMENT,
    param_name VARCHAR(100) NOT NULL UNIQUE,
    param_value VARCHAR(500) NOT NULL,
    param_desc VARCHAR(255),
    modify_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modifier_id INT
);

-- 插入初始数据
-- 管理员账号 (密码: admin123)
INSERT INTO admin (admin_account, pwd, name, phone) VALUES ('admin', 'admin123', '管理员', '13800138000');

-- 科室数据
INSERT INTO dept (dept_name, dept_desc) VALUES 
('内科', '内科是医院的基础科室，负责诊治各种内科疾病。'), 
('外科', '外科主要负责手术治疗和外科疾病的诊治。'), 
('儿科', '儿科专门负责儿童疾病的诊治和预防。'), 
('妇产科', '妇产科负责女性生殖系统疾病和孕产相关服务。'), 
('眼科', '眼科负责眼部疾病的诊治和视力保健。');

-- 医生数据 (密码: 123456)
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) VALUES
('D001', '张医生', '男', '主任医师', 1, '13800138001', '张医生从医20年，擅长内科各种疾病的诊治。', '心血管疾病、呼吸系统疾病', '123456', 0),
('D002', '李医生', '女', '副主任医师', 2, '13800138002', '李医生擅长外科手术治疗。', '普外科手术、微创手术', '123456', 0),
('D003', '王医生', '男', '主治医师', 3, '13800138003', '王医生专注于儿童常见病诊治。', '儿童呼吸道疾病、儿童消化系统疾病', '123456', 0);

-- 用户数据 (密码: 123456)
INSERT INTO user (phone, pwd, real_name, id_card, status) VALUES
('13800138003', '123456', '王小明', '410101199001011234', 0),
('13800138004', '123456', '李小红', '410101199205052345', 0);

-- 系统参数
INSERT INTO sys_param (param_name, param_value, param_desc) VALUES 
('wechat.msg.switch', '1', '微信消息推送开关'),
('visit.remind.time', '120', '就诊提醒提前时间（分钟）'),
('login.fail.lock.time', '15', '登录失败锁定时间（分钟）'),
('token.expiration', '7200', 'Token过期时间（秒）'),
('db.backup.time', '02:00', '数据库自动备份时间'),
('db.backup.days', '7', '备份文件保留天数');
