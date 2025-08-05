-- =============================================
-- 通知服务模块表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 消息、通知模板、通知设置相关表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 通知模板表
-- =============================================
CREATE TABLE `notification_templates` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '模板ID',
    `template_name` VARCHAR(100) NOT NULL COMMENT '模板名称',
    `template_code` VARCHAR(50) NOT NULL COMMENT '模板编码',
    `template_type` VARCHAR(20) NOT NULL COMMENT '模板类型（SYSTEM：系统通知，SMS：短信，EMAIL：邮件，WECHAT：微信）',
    `event_type` VARCHAR(50) NOT NULL COMMENT '事件类型（USER_REGISTER：用户注册，LEAD_AUDIT：线索审核，EXCHANGE_REQUEST：交换申请等）',
    `title` VARCHAR(200) COMMENT '标题模板',
    `content` TEXT NOT NULL COMMENT '内容模板',
    `variables` TEXT COMMENT '变量说明（JSON格式）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `priority` TINYINT NOT NULL DEFAULT 0 COMMENT '优先级（0：普通，1：重要，2：紧急）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`),
    KEY `idx_template_name` (`template_name`),
    KEY `idx_template_type` (`template_type`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知模板表';

-- =============================================
-- 通知消息表
-- =============================================
CREATE TABLE `notifications` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '通知ID',
    `message_id` VARCHAR(50) NOT NULL COMMENT '消息ID（唯一标识）',
    `recipient_id` BIGINT NOT NULL COMMENT '接收者ID',
    `recipient_type` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '接收者类型（USER：用户，ADMIN：管理员，SYSTEM：系统）',
    `sender_id` BIGINT COMMENT '发送者ID',
    `notification_type` VARCHAR(20) NOT NULL COMMENT '通知类型（SYSTEM：系统通知，SMS：短信，EMAIL：邮件，WECHAT：微信）',
    `event_type` VARCHAR(50) NOT NULL COMMENT '事件类型',
    `template_id` BIGINT COMMENT '模板ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT NOT NULL COMMENT '通知内容',
    `extra_data` TEXT COMMENT '额外数据（JSON格式）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING：待发送，SENDING：发送中，SUCCESS：发送成功，FAILED：发送失败，CANCELLED：已取消）',
    `send_time` DATETIME COMMENT '发送时间',
    `read_time` DATETIME COMMENT '阅读时间',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读（0：未读，1：已读）',
    `priority` TINYINT NOT NULL DEFAULT 0 COMMENT '优先级（0：普通，1：重要，2：紧急）',
    `retry_count` INT NOT NULL DEFAULT 0 COMMENT '重试次数',
    `max_retry_count` INT NOT NULL DEFAULT 3 COMMENT '最大重试次数',
    `failure_reason` VARCHAR(500) COMMENT '失败原因',
    `expire_time` DATETIME COMMENT '过期时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_message_id` (`message_id`),
    KEY `idx_recipient_id` (`recipient_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_notification_type` (`notification_type`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_template_id` (`template_id`),
    KEY `idx_status` (`status`),
    KEY `idx_is_read` (`is_read`),
    KEY `idx_priority` (`priority`),
    KEY `idx_send_time` (`send_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_composite_query` (`recipient_id`, `is_read`, `create_time`),
    KEY `idx_pending_send` (`status`, `send_time`, `retry_count`),
    CONSTRAINT `fk_notifications_recipient_id` FOREIGN KEY (`recipient_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_notifications_sender_id` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_notifications_template_id` FOREIGN KEY (`template_id`) REFERENCES `notification_templates` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知消息表';

-- =============================================
-- 用户通知设置表
-- =============================================
CREATE TABLE `notification_settings` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设置ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `event_type` VARCHAR(50) NOT NULL COMMENT '事件类型',
    `system_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '系统通知开关（0：关闭，1：开启）',
    `sms_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '短信通知开关（0：关闭，1：开启）',
    `email_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '邮件通知开关（0：关闭，1：开启）',
    `wechat_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '微信通知开关（0：关闭，1：开启）',
    `quiet_start_time` TIME COMMENT '免打扰开始时间',
    `quiet_end_time` TIME COMMENT '免打扰结束时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_event` (`user_id`, `event_type`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_event_type` (`event_type`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_notification_settings_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户通知设置表';

-- =============================================
-- 通知发送日志表
-- =============================================
CREATE TABLE `notification_send_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `notification_id` BIGINT NOT NULL COMMENT '通知ID',
    `send_channel` VARCHAR(20) NOT NULL COMMENT '发送渠道（SYSTEM：系统，SMS：短信，EMAIL：邮件，WECHAT：微信）',
    `recipient` VARCHAR(200) NOT NULL COMMENT '接收方（手机号、邮箱等）',
    `send_status` VARCHAR(20) NOT NULL COMMENT '发送状态（SUCCESS：成功，FAILED：失败）',
    `response_code` VARCHAR(50) COMMENT '响应码',
    `response_message` VARCHAR(500) COMMENT '响应消息',
    `send_duration` INT COMMENT '发送耗时（毫秒）',
    `cost` DECIMAL(10,4) COMMENT '发送成本',
    `provider` VARCHAR(50) COMMENT '服务提供商',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_notification_id` (`notification_id`),
    KEY `idx_send_channel` (`send_channel`),
    KEY `idx_send_status` (`send_status`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_composite_stats` (`send_channel`, `send_status`, `create_time`),
    CONSTRAINT `fk_notification_send_logs_notification_id` FOREIGN KEY (`notification_id`) REFERENCES `notifications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知发送日志表';