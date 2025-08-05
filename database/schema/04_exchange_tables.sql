-- =============================================
-- 交换引擎模块表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 交换申请、交换历史、积分管理相关表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 用户积分表
-- =============================================
CREATE TABLE `user_credits` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '积分记录ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `total_credits` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总积分',
    `available_credits` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '可用积分',
    `frozen_credits` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '冻结积分',
    `consumed_credits` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已消费积分',
    `earned_credits` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '累计获得积分',
    `last_update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`),
    KEY `idx_total_credits` (`total_credits`),
    KEY `idx_available_credits` (`available_credits`),
    KEY `idx_last_update_time` (`last_update_time`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_user_credits_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户积分表';

-- =============================================
-- 积分变动记录表
-- =============================================
CREATE TABLE `credit_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `transaction_type` VARCHAR(20) NOT NULL COMMENT '交易类型（EARN：获得，CONSUME：消费，FREEZE：冻结，UNFREEZE：解冻，REFUND：退款）',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '变动金额',
    `balance_before` DECIMAL(10,2) NOT NULL COMMENT '变动前余额',
    `balance_after` DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    `source_type` VARCHAR(20) NOT NULL COMMENT '来源类型（LEAD_PUBLISH：发布线索，LEAD_EXCHANGE：线索交换，SYSTEM_REWARD：系统奖励，ADMIN_ADJUST：管理员调整）',
    `source_id` BIGINT COMMENT '来源ID（如线索ID、交换ID等）',
    `description` VARCHAR(200) COMMENT '交易描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_transaction_type` (`transaction_type`),
    KEY `idx_source_type` (`source_type`),
    KEY `idx_source_id` (`source_id`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_composite_query` (`user_id`, `transaction_type`, `create_time`),
    CONSTRAINT `fk_credit_transactions_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分变动记录表';

-- =============================================
-- 交换申请表
-- =============================================
CREATE TABLE `exchange_requests` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请ID',
    `request_no` VARCHAR(50) NOT NULL COMMENT '申请编号',
    `requester_id` BIGINT NOT NULL COMMENT '申请人ID',
    `target_lead_id` BIGINT NOT NULL COMMENT '目标线索ID',
    `target_lead_owner_id` BIGINT NOT NULL COMMENT '目标线索所有者ID',
    `offered_lead_ids` TEXT NOT NULL COMMENT '提供的线索ID列表（JSON格式）',
    `offered_credits` DECIMAL(10,2) NOT NULL COMMENT '提供的积分',
    `required_credits` DECIMAL(10,2) NOT NULL COMMENT '需要的积分',
    `credit_difference` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '积分差额',
    `exchange_reason` VARCHAR(500) COMMENT '交换理由',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态（PENDING：待处理，APPROVED：已同意，REJECTED：已拒绝，CANCELLED：已取消，COMPLETED：已完成，EXPIRED：已过期）',
    `response_message` VARCHAR(500) COMMENT '响应消息',
    `expire_time` DATETIME NOT NULL COMMENT '过期时间',
    `approved_time` DATETIME COMMENT '同意时间',
    `completed_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_request_no` (`request_no`),
    KEY `idx_requester_id` (`requester_id`),
    KEY `idx_target_lead_id` (`target_lead_id`),
    KEY `idx_target_lead_owner_id` (`target_lead_owner_id`),
    KEY `idx_status` (`status`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_composite_query` (`requester_id`, `status`, `create_time`),
    CONSTRAINT `fk_exchange_requests_requester_id` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_exchange_requests_target_lead_id` FOREIGN KEY (`target_lead_id`) REFERENCES `leads` (`id`),
    CONSTRAINT `fk_exchange_requests_target_lead_owner_id` FOREIGN KEY (`target_lead_owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交换申请表';

-- =============================================
-- 交换交易记录表
-- =============================================
CREATE TABLE `exchange_transactions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '交易ID',
    `transaction_no` VARCHAR(50) NOT NULL COMMENT '交易编号',
    `exchange_request_id` BIGINT NOT NULL COMMENT '交换申请ID',
    `requester_id` BIGINT NOT NULL COMMENT '申请人ID',
    `provider_id` BIGINT NOT NULL COMMENT '提供方ID',
    `target_lead_id` BIGINT NOT NULL COMMENT '目标线索ID',
    `offered_lead_ids` TEXT NOT NULL COMMENT '提供的线索ID列表（JSON格式）',
    `credit_amount` DECIMAL(10,2) NOT NULL COMMENT '积分金额',
    `transaction_status` VARCHAR(20) NOT NULL DEFAULT 'PROCESSING' COMMENT '交易状态（PROCESSING：处理中，SUCCESS：成功，FAILED：失败，ROLLBACK：已回滚）',
    `failure_reason` VARCHAR(500) COMMENT '失败原因',
    `completed_time` DATETIME COMMENT '完成时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_transaction_no` (`transaction_no`),
    UNIQUE KEY `uk_exchange_request_id` (`exchange_request_id`),
    KEY `idx_requester_id` (`requester_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_target_lead_id` (`target_lead_id`),
    KEY `idx_transaction_status` (`transaction_status`),
    KEY `idx_completed_time` (`completed_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`),
    CONSTRAINT `fk_exchange_transactions_exchange_request_id` FOREIGN KEY (`exchange_request_id`) REFERENCES `exchange_requests` (`id`),
    CONSTRAINT `fk_exchange_transactions_requester_id` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_exchange_transactions_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_exchange_transactions_target_lead_id` FOREIGN KEY (`target_lead_id`) REFERENCES `leads` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交换交易记录表';

-- =============================================
-- 交换历史表
-- =============================================
CREATE TABLE `exchange_histories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '历史ID',
    `exchange_transaction_id` BIGINT NOT NULL COMMENT '交换交易ID',
    `requester_id` BIGINT NOT NULL COMMENT '申请人ID',
    `provider_id` BIGINT NOT NULL COMMENT '提供方ID',
    `lead_id` BIGINT NOT NULL COMMENT '线索ID',
    `lead_title` VARCHAR(200) NOT NULL COMMENT '线索标题',
    `lead_rating` VARCHAR(10) COMMENT '线索评级',
    `credit_value` DECIMAL(10,2) NOT NULL COMMENT '积分价值',
    `exchange_type` VARCHAR(20) NOT NULL COMMENT '交换类型（GIVE：给出，RECEIVE：获得）',
    `exchange_time` DATETIME NOT NULL COMMENT '交换时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_exchange_transaction_id` (`exchange_transaction_id`),
    KEY `idx_requester_id` (`requester_id`),
    KEY `idx_provider_id` (`provider_id`),
    KEY `idx_lead_id` (`lead_id`),
    KEY `idx_exchange_type` (`exchange_type`),
    KEY `idx_exchange_time` (`exchange_time`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_composite_stats` (`requester_id`, `exchange_time`),
    CONSTRAINT `fk_exchange_histories_exchange_transaction_id` FOREIGN KEY (`exchange_transaction_id`) REFERENCES `exchange_transactions` (`id`),
    CONSTRAINT `fk_exchange_histories_requester_id` FOREIGN KEY (`requester_id`) REFERENCES `users` (`id`),
    CONSTRAINT `fk_exchange_histories_provider_id` FOREIGN KEY (`provider_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交换历史表';