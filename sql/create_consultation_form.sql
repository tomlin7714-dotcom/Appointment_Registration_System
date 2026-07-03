-- 问诊表单表（用于存储用户填写的问诊信息和医生诊断）
CREATE TABLE IF NOT EXISTS consultation_form (
    id INT PRIMARY KEY AUTO_INCREMENT,
    appointment_id INT NOT NULL UNIQUE,
    consultation_type VARCHAR(50) DEFAULT NULL COMMENT '问诊类型（初诊/复诊/急诊）',
    chief_complaint VARCHAR(500) DEFAULT NULL COMMENT '主诉',
    medical_history VARCHAR(1000) DEFAULT NULL COMMENT '就诊史',
    recovery_history VARCHAR(1000) DEFAULT NULL COMMENT '康复史',
    diagnosis VARCHAR(1000) DEFAULT NULL COMMENT '诊断结果（医生填写）',
    treatment VARCHAR(1000) DEFAULT NULL COMMENT '治疗方案（医生填写）',
    doctor_advice VARCHAR(1000) DEFAULT NULL COMMENT '医嘱（医生填写）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME,
    FOREIGN KEY (appointment_id) REFERENCES appointment(id)
);
