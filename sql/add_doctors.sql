-- 添加医生数据
-- 注意：请确保在MySQL命令行或其他MySQL客户端中执行此脚本
-- 数据库编码应为UTF-8 (utf8mb4)

USE hospital_registration_system;

-- 设置字符集
SET NAMES utf8mb4;

-- 口腔科 (dept_id = 8) - 添加3名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D201', '张明华', '男', '主任医师', 8, '13800138201', '从事口腔科工作20年，擅长口腔常见病、多发病的诊治', '口腔种植、牙齿矫正', '123456', NOW(), 0),
('D202', '李秀芳', '女', '副主任医师', 8, '13800138202', '从事口腔科工作15年，擅长口腔正畸、种植牙', '口腔正畸、种植牙', '123456', NOW(), 0),
('D203', '王建国', '男', '主治医师', 8, '13800138203', '从事口腔科工作8年，擅长儿童口腔保健', '儿童口腔保健、龋齿治疗', '123456', NOW(), 0);

-- 皮肤科 (dept_id = 9) - 添加3名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D204', '陈晓红', '女', '主任医师', 9, '13800138204', '从事皮肤科工作25年，擅长各类皮肤病的诊治', '湿疹、银屑病、痤疮', '123456', NOW(), 0),
('D205', '刘志强', '男', '副主任医师', 9, '13800138205', '从事皮肤科工作12年，擅长过敏性皮肤病', '过敏性皮肤病、皮炎', '123456', NOW(), 0),
('D206', '赵丽娜', '女', '主治医师', 9, '13800138206', '从事皮肤科工作6年，擅长痤疮、湿疹等常见皮肤病', '痤疮、湿疹、皮肤美容', '123456', NOW(), 0);

-- 中医科 (dept_id = 10) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D207', '孙伟民', '男', '主任医师', 10, '13800138207', '从事中医工作30年，擅长中医内科常见病的诊治', '中医内科、针灸推拿', '123456', NOW(), 0),
('D208', '周雅琴', '女', '副主任医师', 10, '13800138208', '从事中医工作18年，擅长中医针灸、推拿', '针灸、推拿、理疗', '123456', NOW(), 0);

-- 神经内科 (dept_id = 11) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D209', '吴国平', '男', '主任医师', 11, '13800138209', '从事神经内科工作22年，擅长神经系统疾病的诊治', '脑血管病、头痛、眩晕', '123456', NOW(), 0),
('D210', '郑小燕', '女', '副主任医师', 11, '13800138210', '从事神经内科工作10年，擅长癫痫、帕金森病', '癫痫、帕金森病、失眠', '123456', NOW(), 0);

-- 心血管内科 (dept_id = 12) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D211', '马志刚', '男', '主任医师', 12, '13800138211', '从事心血管内科工作28年，擅长心血管疾病的诊治', '冠心病、高血压、心律失常', '123456', NOW(), 0),
('D212', '林雪梅', '女', '副主任医师', 12, '13800138212', '从事心血管内科工作15年，擅长心力衰竭治疗', '心力衰竭、心肌病', '123456', NOW(), 0);

-- 呼吸内科 (dept_id = 13) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D213', '黄建军', '男', '主任医师', 13, '13800138213', '从事呼吸内科工作20年，擅长呼吸系统疾病的诊治', '哮喘、肺炎、慢阻肺', '123456', NOW(), 0),
('D214', '杨晓丽', '女', '副主任医师', 13, '13800138214', '从事呼吸内科工作12年，擅长肺部感染性疾病', '肺部感染、支气管炎', '123456', NOW(), 0);

-- 消化内科 (dept_id = 14) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D215', '徐国华', '男', '主任医师', 14, '13800138215', '从事消化内科工作25年，擅长消化系统疾病的诊治', '胃炎、胃溃疡、肝炎', '123456', NOW(), 0),
('D216', '何美玲', '女', '副主任医师', 14, '13800138216', '从事消化内科工作10年，擅长胃肠镜检查', '胃肠镜、消化道肿瘤', '123456', NOW(), 0);

-- 骨科 (dept_id = 15) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D217', '朱志远', '男', '主任医师', 15, '13800138217', '从事骨科工作22年，擅长骨科疾病的诊治', '骨折、关节置换、脊柱疾病', '123456', NOW(), 0),
('D218', '许文静', '女', '副主任医师', 15, '13800138218', '从事骨科工作12年，擅长运动损伤治疗', '运动损伤、关节镜', '123456', NOW(), 0);

-- 耳鼻喉科 (dept_id = 17) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D219', '郭明辉', '男', '主任医师', 17, '13800138219', '从事耳鼻喉科工作18年，擅长耳鼻喉科疾病的诊治', '中耳炎、鼻炎、咽喉疾病', '123456', NOW(), 0),
('D220', '曾丽华', '女', '副主任医师', 17, '13800138220', '从事耳鼻喉科工作10年，擅长听力障碍治疗', '听力障碍、耳鸣', '123456', NOW(), 0);

-- 急诊科 (dept_id = 18) - 添加2名医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, create_time, status) VALUES
('D221', '彭国强', '男', '主任医师', 18, '13800138221', '从事急诊医学工作15年，擅长急诊急救', '急诊急救、危重症', '123456', NOW(), 0),
('D222', '董小燕', '女', '副主任医师', 18, '13800138222', '从事急诊医学工作8年，擅长急性中毒救治', '急性中毒、创伤急救', '123456', NOW(), 0);

-- 查看添加结果
SELECT d.id, d.work_no, d.name, d.title, dept.dept_name, d.phone 
FROM doctor d 
LEFT JOIN dept ON d.dept_id = dept.id 
ORDER BY d.id;
