-- =============================================
-- 评级历史记录表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 线索评级变更历史记录表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 评级历史记录表
-- =============================================
CREATE TABLE `rating_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '历史记录ID',
    `lead_id` BIGINT NOT NULL COMMENT '线索ID',
    `old_rating` VARCHAR(10) COMMENT '变更前评级（A/B/C/D）',
    `new_rating` VARCHAR(10) NOT NULL COMMENT '变更后评级（A/B/C/D）',
    `old_score` DECIMAL(5,2) COMMENT '变更前分数',
    `new_score` DECIMAL(5,2) NOT NULL COMMENT '变更后分数',
    `change_reason` VARCHAR(50) NOT NULL COMMENT '变更原因（AUTO_RATING：自动评级，MANUAL_ADJUST：手动调整，INFO_UPDATE：信息更新，RULE_CHANGE：规则变更等）',
    `operator_id` BIGINT COMMENT '操作人ID（自动评级时为空）',
    `operator_name` VARCHAR(50) COMMENT '操作人姓名',
    `change_description` VARCHAR(500) COMMENT '变更说明',
    `rating_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评级时间',
    `rating_details` TEXT COMMENT '评级详情（JSON格式，包含各维度分数）',
    `rating_version` VARCHAR(20) COMMENT '评级版本',
    `system_version` VARCHAR(20) COMMENT '系统版本',
    `ip_address` VARCHAR(50) COMMENT '操作IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_lead_id` (`lead_id`),
    KEY `idx_new_rating` (`new_rating`),
    KEY `idx_change_reason` (`change_reason`),
    KEY `idx_operator_id` (`operator_id`),
    KEY `idx_rating_time` (`rating_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_composite_query` (`lead_id`, `rating_time`, `deleted`),
    KEY `idx_statistics_query` (`new_rating`, `change_reason`, `rating_time`),
    CONSTRAINT `fk_rating_histories_lead_id` FOREIGN KEY (`lead_id`) REFERENCES `leads` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_rating_histories_operator_id` FOREIGN KEY (`operator_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评级历史记录表';

-- =============================================
-- 创建分区（按月分区，提高查询性能）
-- =============================================
-- ALTER TABLE `rating_histories` PARTITION BY RANGE (YEAR(rating_time) * 100 + MONTH(rating_time)) (
--     PARTITION p202401 VALUES LESS THAN (202402),
--     PARTITION p202402 VALUES LESS THAN (202403),
--     PARTITION p202403 VALUES LESS THAN (202404),
--     PARTITION p202404 VALUES LESS THAN (202405),
--     PARTITION p202405 VALUES LESS THAN (202406),
--     PARTITION p202406 VALUES LESS THAN (202407),
--     PARTITION p202407 VALUES LESS THAN (202408),
--     PARTITION p202408 VALUES LESS THAN (202409),
--     PARTITION p202409 VALUES LESS THAN (202410),
--     PARTITION p202410 VALUES LESS THAN (202411),
--     PARTITION p202411 VALUES LESS THAN (202412),
--     PARTITION p202412 VALUES LESS THAN (202501),
--     PARTITION p_future VALUES LESS THAN MAXVALUE
-- );

-- =============================================
-- 初始化测试数据
-- =============================================
INSERT INTO `rating_histories` (
    `lead_id`, `old_rating`, `new_rating`, `old_score`, `new_score`, 
    `change_reason`, `operator_id`, `operator_name`, `change_description`, 
    `rating_details`, `rating_version`
) VALUES 
(1, NULL, 'B', NULL, 75.50, 'AUTO_RATING', NULL, 'SYSTEM', '系统自动评级', 
 '{"completeness": 80, "qualification": 70, "scale": 75, "industry_value": 80, "location": 70, "timeliness": 85, "reputation": 75}', '1.0.0'),
(2, NULL, 'A', NULL, 88.20, 'AUTO_RATING', NULL, 'SYSTEM', '系统自动评级', 
 '{"completeness": 90, "qualification": 85, "scale": 90, "industry_value": 95, "location": 85, "timeliness": 90, "reputation": 85}', '1.0.0'),
(3, NULL, 'C', NULL, 65.30, 'AUTO_RATING', NULL, 'SYSTEM', '系统自动评级', 
 '{"completeness": 70, "qualification": 60, "scale": 65, "industry_value": 70, "location": 60, "timeliness": 75, "reputation": 65}', '1.0.0'),
(1, 'B', 'A', 75.50, 85.80, 'INFO_UPDATE', 1, '管理员', '线索信息完善，评级提升', 
 '{"completeness": 90, "qualification": 80, "scale": 85, "industry_value": 90, "location": 80, "timeliness": 85, "reputation": 85}', '1.0.0'),
(2, 'A', 'B', 88.20, 78.50, 'MANUAL_ADJUST', 1, '管理员', '手动调整评级', 
 '{"completeness": 85, "qualification": 75, "scale": 80, "industry_value": 85, "location": 75, "timeliness": 80, "reputation": 75}', '1.0.0');