-- =============================================
-- 线索管理模块表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 线索、收藏、审核相关表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 行业分类表
-- =============================================
CREATE TABLE `lead_categories` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父分类ID',
    `level` TINYINT NOT NULL DEFAULT 1 COMMENT '分类层级',
    `path` VARCHAR(500) COMMENT '分类路径',
    `icon` VARCHAR(100) COMMENT '图标',
    `description` VARCHAR(200) COMMENT '分类描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_category_name` (`category_name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='行业分类表';

-- =============================================
-- 地区表
-- =============================================
CREATE TABLE `lead_regions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '地区ID',
    `region_name` VARCHAR(100) NOT NULL COMMENT '地区名称',
    `region_code` VARCHAR(20) NOT NULL COMMENT '地区编码',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父地区ID',
    `level` TINYINT NOT NULL DEFAULT 1 COMMENT '地区层级（1：省，2：市，3：区县）',
    `path` VARCHAR(500) COMMENT '地区路径',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_region_code` (`region_code`),
    KEY `idx_region_name` (`region_name`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_level` (`level`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地区表';

-- =============================================
-- 线索表
-- =============================================
CREATE TABLE `leads` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '线索ID',
    `title` VARCHAR(200) NOT NULL COMMENT '线索标题',
    `description` TEXT COMMENT '线索描述',
    `company_name` VARCHAR(200) NOT NULL COMMENT '企业名称',
    `company_type` VARCHAR(50) COMMENT '企业类型',
    `business_license` VARCHAR(100) COMMENT '营业执照号',
    `credit_code` VARCHAR(50) COMMENT '统一社会信用代码',
    `legal_person` VARCHAR(100) COMMENT '法人代表',
    `registered_capital` DECIMAL(15,2) COMMENT '注册资本（万元）',
    `established_date` DATE COMMENT '成立日期',
    `contact_person` VARCHAR(100) COMMENT '联系人',
    `contact_phone` VARCHAR(255) COMMENT '联系电话（加密存储）',
    `contact_email` VARCHAR(255) COMMENT '联系邮箱（加密存储）',
    `contact_address` VARCHAR(500) COMMENT '联系地址',
    `investment_amount` DECIMAL(15,2) COMMENT '投资金额（万元）',
    `investment_stage` VARCHAR(50) COMMENT '投资阶段',
    `category_id` BIGINT NOT NULL COMMENT '行业分类ID',
    `region_id` BIGINT NOT NULL COMMENT '地区ID',
    `longitude` DECIMAL(10,7) COMMENT '经度',
    `latitude` DECIMAL(10,7) COMMENT '纬度',
    `tags` VARCHAR(500) COMMENT '标签（JSON格式）',
    `attachments` TEXT COMMENT '附件信息（JSON格式）',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态（DRAFT：草稿，PENDING：待审核，APPROVED：已通过，REJECTED：已拒绝，PUBLISHED：已发布，EXCHANGING：交换中，COMPLETED：已成交，OFFLINE：已下架）',
    `rating` VARCHAR(10) COMMENT '评级（A/B/C/D）',
    `rating_score` DECIMAL(5,2) COMMENT '评级分数',
    `rating_details` TEXT COMMENT '评级详情（JSON格式）',
    `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `exchange_count` INT NOT NULL DEFAULT 0 COMMENT '交换次数',
    `priority` TINYINT NOT NULL DEFAULT 0 COMMENT '优先级（0：普通，1：重要，2：紧急）',
    `expire_time` DATETIME COMMENT '过期时间',
    `published_time` DATETIME COMMENT '发布时间',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `auditor_id` BIGINT COMMENT '审核人ID',
    `audit_time` DATETIME COMMENT '审核时间',
    `audit_opinion` VARCHAR(500) COMMENT '审核意见',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `idx_title` (`title`),
    KEY `idx_company_name` (`company_name`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_region_id` (`region_id`),
    KEY `idx_status` (`status`),
    KEY `idx_rating` (`rating`),
    KEY `idx_publisher_id` (`publisher_id`),
    KEY `idx_published_time` (`published_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_priority` (`priority`),
    KEY `idx_location` (`longitude`, `latitude`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_composite_search` (`status`, `category_id`, `region_id`, `published_time`),
    FULLTEXT KEY `ft_title_description` (`title`, `description`),
    CONSTRAINT `fk_leads_category_id` FOREIGN KEY (`category_id`) REFERENCES `lead_categories` (`id`),
    CONSTRAINT `fk_leads_region_id` FOREIGN KEY (`region_id`) REFERENCES `lead_regions` (`id`),
    CONSTRAINT `fk_leads_publisher_id` FOREIGN KEY (`publisher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线索表';

-- =============================================
-- 线索收藏表
-- =============================================
CREATE TABLE `lead_favorites` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `lead_id` BIGINT NOT NULL COMMENT '线索ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_lead` (`user_id`, `lead_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_lead_id` (`lead_id`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_lead_favorites_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_lead_favorites_lead_id` FOREIGN KEY (`lead_id`) REFERENCES `leads` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线索收藏表';

-- =============================================
-- 线索审核日志表
-- =============================================
CREATE TABLE `lead_audit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `lead_id` BIGINT NOT NULL COMMENT '线索ID',
    `auditor_id` BIGINT NOT NULL COMMENT '审核人ID',
    `action` VARCHAR(20) NOT NULL COMMENT '操作类型（SUBMIT：提交审核，APPROVE：通过，REJECT：拒绝，RETURN：退回）',
    `from_status` VARCHAR(20) COMMENT '原状态',
    `to_status` VARCHAR(20) COMMENT '目标状态',
    `opinion` VARCHAR(500) COMMENT '审核意见',
    `attachments` TEXT COMMENT '附件信息（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_lead_id` (`lead_id`),
    KEY `idx_auditor_id` (`auditor_id`),
    KEY `idx_action` (`action`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_lead_audit_logs_lead_id` FOREIGN KEY (`lead_id`) REFERENCES `leads` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_lead_audit_logs_auditor_id` FOREIGN KEY (`auditor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线索审核日志表';

-- =============================================
-- 线索浏览记录表
-- =============================================
CREATE TABLE `lead_views` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '浏览ID',
    `user_id` BIGINT COMMENT '用户ID（可为空，支持匿名浏览）',
    `lead_id` BIGINT NOT NULL COMMENT '线索ID',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `view_duration` INT COMMENT '浏览时长（秒）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_lead_id` (`lead_id`),
    KEY `idx_ip_address` (`ip_address`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_composite_stats` (`lead_id`, `create_time`),
    CONSTRAINT `fk_lead_views_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
    CONSTRAINT `fk_lead_views_lead_id` FOREIGN KEY (`lead_id`) REFERENCES `leads` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='线索浏览记录表';