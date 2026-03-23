-- 为user表添加avatar字段
ALTER TABLE user ADD COLUMN avatar VARCHAR(500) DEFAULT NULL COMMENT '头像URL' AFTER id_card;
