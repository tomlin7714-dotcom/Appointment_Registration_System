-- 为appointment表添加问诊单相关字段
ALTER TABLE appointment ADD COLUMN consultation_type VARCHAR(50) DEFAULT NULL COMMENT '问诊类型（初诊/复诊/急诊）' AFTER patient_id_card;
ALTER TABLE appointment ADD COLUMN chief_complaint VARCHAR(500) DEFAULT NULL COMMENT '主诉' AFTER consultation_type;
ALTER TABLE appointment ADD COLUMN medical_history VARCHAR(1000) DEFAULT NULL COMMENT '就诊史' AFTER chief_complaint;
ALTER TABLE appointment ADD COLUMN recovery_history VARCHAR(1000) DEFAULT NULL COMMENT '康复史' AFTER medical_history;
ALTER TABLE appointment ADD COLUMN diagnosis VARCHAR(1000) DEFAULT NULL COMMENT '诊断结果（医生填写）' AFTER recovery_history;
ALTER TABLE appointment ADD COLUMN treatment VARCHAR(1000) DEFAULT NULL COMMENT '治疗方案（医生填写）' AFTER diagnosis;
ALTER TABLE appointment ADD COLUMN doctor_advice VARCHAR(1000) DEFAULT NULL COMMENT '医嘱（医生填写）' AFTER treatment;
