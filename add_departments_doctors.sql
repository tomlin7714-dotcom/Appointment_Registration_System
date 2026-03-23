-- 切换到项目数据库
USE hospital_registration_system;

-- 先查看当前科室情况
SELECT * FROM dept ORDER BY id;

-- 添加缺失的科室（如果不存在）
INSERT IGNORE INTO dept (dept_name, dept_desc, status) VALUES
('口腔科', '口腔科负责口腔疾病的诊治，包括牙齿、牙龈、口腔黏膜等疾病的诊断和治疗。', 0),
('皮肤科', '皮肤科负责各种皮肤病的诊治，包括湿疹、皮炎、银屑病、痤疮等常见皮肤病。', 0),
('中医科', '中医科运用中医理论和方法诊治疾病，包括中药治疗、针灸、推拿等传统疗法。', 0),
('神经内科', '神经内科负责诊治脑血管疾病、癫痫、帕金森病、头痛、眩晕等神经系统疾病。', 0),
('心血管内科', '心血管内科负责诊治高血压、冠心病、心律失常、心力衰竭等心血管疾病。', 0),
('呼吸内科', '呼吸内科负责诊治肺炎、支气管炎、哮喘、慢性阻塞性肺病等呼吸系统疾病。', 0),
('消化内科', '消化内科负责诊治胃炎、胃溃疡、肝炎、肝硬化、胆囊炎等消化系统疾病。', 0),
('骨科', '骨科负责诊治骨折、关节疾病、脊柱疾病、运动损伤等骨骼肌肉系统疾病。', 0),
('泌尿外科', '泌尿外科负责诊治肾结石、前列腺疾病、泌尿系统肿瘤等泌尿系统疾病。', 0),
('耳鼻喉科', '耳鼻喉科负责诊治中耳炎、鼻炎、咽喉炎、听力障碍等耳鼻喉疾病。', 0),
('急诊科', '急诊科24小时接诊各类急危重症患者，提供快速诊断和抢救服务。', 0);

-- 使用子查询动态获取科室ID插入医生数据
-- 内科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D004', '陈医生', '男', '主任医师', id, '13800138004', '从医25年，内科领域资深专家。', '心血管疾病、高血压、糖尿病', '123456', 0 FROM dept WHERE dept_name = '内科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D005', '刘医生', '女', '副主任医师', id, '13800138005', '擅长内科常见病多发病诊治。', '呼吸系统疾病、老年病', '123456', 0 FROM dept WHERE dept_name = '内科' LIMIT 1;

-- 外科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D006', '赵医生', '男', '主任医师', id, '13800138006', '外科手术专家，擅长各类微创手术。', '腹腔镜手术、胃肠外科', '123456', 0 FROM dept WHERE dept_name = '外科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D007', '孙医生', '男', '副主任医师', id, '13800138007', '从事外科工作15年，经验丰富。', '甲状腺手术、乳腺外科', '123456', 0 FROM dept WHERE dept_name = '外科' LIMIT 1;

-- 儿科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D008', '周医生', '女', '主任医师', id, '13800138008', '儿科专家，擅长儿童常见病诊治。', '小儿呼吸道疾病、小儿消化系统疾病', '123456', 0 FROM dept WHERE dept_name = '儿科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D009', '吴医生', '男', '主治医师', id, '13800138009', '从事儿科工作10年。', '新生儿疾病、儿童保健', '123456', 0 FROM dept WHERE dept_name = '儿科' LIMIT 1;

-- 妇产科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D010', '郑医生', '女', '主任医师', id, '13800138010', '妇产科资深专家，擅长妇科肿瘤诊治。', '妇科肿瘤、高危妊娠', '123456', 0 FROM dept WHERE dept_name = '妇产科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D011', '钱医生', '女', '副主任医师', id, '13800138011', '从事妇产科工作18年。', '不孕不育、妇科内分泌', '123456', 0 FROM dept WHERE dept_name = '妇产科' LIMIT 1;

-- 眼科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D012', '冯医生', '男', '主任医师', id, '13800138012', '眼科专家，擅长白内障、青光眼诊治。', '白内障手术、青光眼诊治', '123456', 0 FROM dept WHERE dept_name = '眼科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D013', '何医生', '女', '副主任医师', id, '13800138013', '从事眼科工作12年。', '近视矫正、眼底病变', '123456', 0 FROM dept WHERE dept_name = '眼科' LIMIT 1;

-- 口腔科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D014', '马医生', '男', '主任医师', id, '13800138014', '口腔科专家，擅长口腔种植修复。', '口腔种植、口腔修复', '123456', 0 FROM dept WHERE dept_name = '口腔科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D015', '林医生', '女', '副主任医师', id, '13800138015', '从事口腔科工作10年。', '牙齿矫正、牙周病', '123456', 0 FROM dept WHERE dept_name = '口腔科' LIMIT 1;

-- 皮肤科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D016', '黄医生', '女', '主任医师', id, '13800138016', '皮肤科专家，擅长各类皮肤病诊治。', '湿疹、银屑病、痤疮', '123456', 0 FROM dept WHERE dept_name = '皮肤科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D017', '杨医生', '男', '副主任医师', id, '13800138017', '从事皮肤科工作15年。', '皮肤过敏、皮肤美容', '123456', 0 FROM dept WHERE dept_name = '皮肤科' LIMIT 1;

-- 中医科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D018', '徐医生', '男', '主任医师', id, '13800138018', '中医世家传承，擅长中医内科调理。', '中医内科、针灸推拿', '123456', 0 FROM dept WHERE dept_name = '中医科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D019', '朱医生', '女', '副主任医师', id, '13800138019', '从事中医工作20年。', '中医妇科、体质调理', '123456', 0 FROM dept WHERE dept_name = '中医科' LIMIT 1;

-- 神经内科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D020', '胡医生', '男', '主任医师', id, '13800138020', '神经内科专家，擅长脑血管疾病诊治。', '脑梗塞、脑出血、癫痫', '123456', 0 FROM dept WHERE dept_name = '神经内科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D021', '高医生', '女', '副主任医师', id, '13800138021', '从事神经内科工作12年。', '头痛、眩晕、帕金森病', '123456', 0 FROM dept WHERE dept_name = '神经内科' LIMIT 1;

-- 心血管内科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D022', '罗医生', '男', '主任医师', id, '13800138022', '心血管专家，擅长冠心病介入治疗。', '冠心病、心律失常、高血压', '123456', 0 FROM dept WHERE dept_name = '心血管内科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D023', '梁医生', '女', '副主任医师', id, '13800138023', '从事心血管内科工作15年。', '心力衰竭、心肌病', '123456', 0 FROM dept WHERE dept_name = '心血管内科' LIMIT 1;

-- 呼吸内科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D024', '宋医生', '男', '主任医师', id, '13800138024', '呼吸内科专家，擅长肺部疾病诊治。', '肺炎、哮喘、慢阻肺', '123456', 0 FROM dept WHERE dept_name = '呼吸内科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D025', '唐医生', '女', '副主任医师', id, '13800138025', '从事呼吸内科工作10年。', '肺部感染、睡眠呼吸暂停', '123456', 0 FROM dept WHERE dept_name = '呼吸内科' LIMIT 1;

-- 消化内科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D026', '许医生', '男', '主任医师', id, '13800138026', '消化内科专家，擅长胃肠镜检查。', '胃炎、胃溃疡、肝炎', '123456', 0 FROM dept WHERE dept_name = '消化内科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D027', '韩医生', '女', '副主任医师', id, '13800138027', '从事消化内科工作12年。', '胆囊炎、胰腺炎、消化道出血', '123456', 0 FROM dept WHERE dept_name = '消化内科' LIMIT 1;

-- 骨科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D028', '邓医生', '男', '主任医师', id, '13800138028', '骨科专家，擅长关节置换手术。', '关节置换、脊柱手术、骨折', '123456', 0 FROM dept WHERE dept_name = '骨科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D029', '曹医生', '男', '副主任医师', id, '13800138029', '从事骨科工作15年。', '运动损伤、微创骨科', '123456', 0 FROM dept WHERE dept_name = '骨科' LIMIT 1;

-- 泌尿外科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D030', '彭医生', '男', '主任医师', id, '13800138030', '泌尿外科专家，擅长微创泌尿手术。', '肾结石、前列腺疾病、泌尿肿瘤', '123456', 0 FROM dept WHERE dept_name = '泌尿外科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D031', '董医生', '男', '副主任医师', id, '13800138031', '从事泌尿外科工作10年。', '男性不育、泌尿感染', '123456', 0 FROM dept WHERE dept_name = '泌尿外科' LIMIT 1;

-- 耳鼻喉科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D032', '蒋医生', '男', '主任医师', id, '13800138032', '耳鼻喉科专家，擅长听力重建手术。', '中耳炎、鼻炎、听力障碍', '123456', 0 FROM dept WHERE dept_name = '耳鼻喉科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D033', '沈医生', '女', '副主任医师', id, '13800138033', '从事耳鼻喉科工作12年。', '咽喉疾病、睡眠呼吸暂停', '123456', 0 FROM dept WHERE dept_name = '耳鼻喉科' LIMIT 1;

-- 急诊科医生
INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D034', '韦医生', '男', '主任医师', id, '13800138034', '急诊科专家，擅长急危重症抢救。', '急危重症、创伤急救', '123456', 0 FROM dept WHERE dept_name = '急诊科' LIMIT 1;

INSERT INTO doctor (work_no, name, gender, title, dept_id, phone, intro, specialty, pwd, status) 
SELECT 'D035', '尹医生', '女', '副主任医师', id, '13800138035', '从事急诊工作10年。', '中毒急救、心肺复苏', '123456', 0 FROM dept WHERE dept_name = '急诊科' LIMIT 1;

-- 查看最终结果
SELECT d.id, d.dept_name, COUNT(doc.id) as doctor_count 
FROM dept d 
LEFT JOIN doctor doc ON d.id = doc.dept_id 
GROUP BY d.id, d.dept_name 
ORDER BY d.id;
