-- 为dept表添加区域和诊室字段
ALTER TABLE dept ADD COLUMN area VARCHAR(20) COMMENT '区域（如1区、2区等）';
ALTER TABLE dept ADD COLUMN room_number VARCHAR(20) COMMENT '诊室号';

-- 更新现有科室的区域和诊室信息
UPDATE dept SET area = '1区', room_number = '101' WHERE dept_name = '内科';
UPDATE dept SET area = '1区', room_number = '102' WHERE dept_name = '外科';
UPDATE dept SET area = '2区', room_number = '201' WHERE dept_name = '儿科';
UPDATE dept SET area = '2区', room_number = '202' WHERE dept_name = '妇产科';
UPDATE dept SET area = '3区', room_number = '301' WHERE dept_name = '眼科';