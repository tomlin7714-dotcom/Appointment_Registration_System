-- 删除新添加的医生及其相关数据
-- 注意：请确保在MySQL命令行或其他MySQL客户端中执行此脚本

USE hospital_registration_system;

-- 禁用安全更新模式
SET SQL_SAFE_UPDATES = 0;

-- 1. 首先删除与这些医生相关的预约记录
DELETE FROM appointment 
WHERE number_source_id IN (
    SELECT ns.id FROM number_source ns
    WHERE ns.schedule_id IN (
        SELECT s.id FROM schedule s
        WHERE s.doctor_id IN (
            SELECT d.id FROM doctor d 
            WHERE d.work_no IN ('D201','D202','D203','D204','D205','D206','D207','D208','D209','D210','D211','D212','D213','D214','D215')
        )
    )
);

-- 2. 删除与这些医生相关的号源
DELETE FROM number_source 
WHERE schedule_id IN (
    SELECT s.id FROM schedule s
    WHERE s.doctor_id IN (
        SELECT d.id FROM doctor d 
        WHERE d.work_no IN ('D201','D202','D203','D204','D205','D206','D207','D208','D209','D210','D211','D212','D213','D214','D215')
    )
);

-- 3. 删除与这些医生相关的排班
DELETE FROM schedule 
WHERE doctor_id IN (
    SELECT d.id FROM doctor d 
    WHERE d.work_no IN ('D201','D202','D203','D204','D205','D206','D207','D208','D209','D210','D211','D212','D213','D214','D215')
);

-- 4. 最后删除这些医生
DELETE FROM doctor 
WHERE work_no IN ('D201','D202','D203','D204','D205','D206','D207','D208','D209','D210','D211','D212','D213','D214','D215');

-- 重新启用安全更新模式
SET SQL_SAFE_UPDATES = 1;

-- 验证删除结果
SELECT COUNT(*) as remaining_doctors FROM doctor;
SELECT id, work_no, name, title, dept_id FROM doctor ORDER BY id;
