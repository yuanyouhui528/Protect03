-- =============================================
-- 系统配置模块表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 评级规则、交换规则、系统配置、审计日志相关表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 系统配置表
-- =============================================
CREATE TABLE `system_configs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
    `config_value` TEXT NOT NULL COMMENT '配置值',
    `config_type` VARCHAR(20) NOT NULL COMMENT '配置类型（STRING：字符串，NUMBER：数字，BOOLEAN：布尔，JSON：JSON对象）',
    `config_group` VARCHAR(50) NOT NULL COMMENT '配置分组（SYSTEM：系统配置，RATING：评级配置，EXCHANGE：交换配置，NOTIFICATION：通知配置）',
    `config_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
    `description` VARCHAR(500) COMMENT '配置描述',
    `default_value` TEXT COMMENT '默认值',
    `validation_rule` VARCHAR(500) COMMENT '验证规则',
    `is_encrypted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否加密（0：否，1：是）',
    `is_public` TINYINT NOT NULL DEFAULT 0 COMMENT '是否公开（0：否，1：是）',
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
    UNIQUE KEY `uk_config_key` (`config_key`),
    KEY `idx_config_group` (`config_group`),
    KEY `idx_config_type` (`config_type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- =============================================
-- 评级规则表
-- =============================================
CREATE TABLE `rating_rules` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `rule_type` VARCHAR(20) NOT NULL COMMENT '规则类型（COMPLETENESS：完整度，QUALIFICATION：资质，SCALE：规模，INDUSTRY_VALUE：产业价值）',
    `weight` DECIMAL(5,4) NOT NULL COMMENT '权重（0-1之间）',
    `calculation_method` VARCHAR(20) NOT NULL COMMENT '计算方法（SCORE：评分，PERCENTAGE：百分比，FORMULA：公式）',
    `formula` TEXT COMMENT '计算公式',
    `parameters` TEXT COMMENT '参数配置（JSON格式）',
    `min_score` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '最小分数',
    `max_score` DECIMAL(5,2) NOT NULL DEFAULT 100.00 COMMENT '最大分数',
    `description` VARCHAR(500) COMMENT '规则描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `effective_time` DATETIME COMMENT '生效时间',
    `expire_time` DATETIME COMMENT '失效时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`),
    KEY `idx_rule_name` (`rule_name`),
    KEY `idx_rule_type` (`rule_type`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_effective_time` (`effective_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评级规则表';

-- =============================================
-- 交换规则表
-- =============================================
CREATE TABLE `exchange_rules` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '规则ID',
    `rule_name` VARCHAR(100) NOT NULL COMMENT '规则名称',
    `rule_code` VARCHAR(50) NOT NULL COMMENT '规则编码',
    `rule_type` VARCHAR(20) NOT NULL COMMENT '规则类型（CREDIT_VALUE：积分价值，EXCHANGE_RATIO：交换比例，LIMIT：限制规则，VALIDATION：验证规则）',
    `rating_level` VARCHAR(10) COMMENT '评级等级（A/B/C/D，为空表示通用规则）',
    `credit_value` DECIMAL(10,2) COMMENT '积分价值',
    `exchange_ratio` VARCHAR(100) COMMENT '交换比例（如1:2表示1个A级=2个B级）',
    `min_exchange_count` INT COMMENT '最小交换数量',
    `max_exchange_count` INT COMMENT '最大交换数量',
    `daily_limit` INT COMMENT '每日限制次数',
    `monthly_limit` INT COMMENT '每月限制次数',
    `validation_conditions` TEXT COMMENT '验证条件（JSON格式）',
    `description` VARCHAR(500) COMMENT '规则描述',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `priority` INT NOT NULL DEFAULT 0 COMMENT '优先级（数字越大优先级越高）',
    `effective_time` DATETIME COMMENT '生效时间',
    `expire_time` DATETIME COMMENT '失效时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_rule_code` (`rule_code`),
    KEY `idx_rule_name` (`rule_name`),
    KEY `idx_rule_type` (`rule_type`),
    KEY `idx_rating_level` (`rating_level`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`),
    KEY `idx_effective_time` (`effective_time`),
    KEY `idx_expire_time` (`expire_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='交换规则表';

-- =============================================
-- 系统操作审计日志表
-- =============================================
CREATE TABLE `audit_logs` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `trace_id` VARCHAR(50) COMMENT '追踪ID',
    `user_id` BIGINT COMMENT '操作用户ID',
    `username` VARCHAR(50) COMMENT '用户名',
    `operation_type` VARCHAR(20) NOT NULL COMMENT '操作类型（CREATE：创建，UPDATE：更新，DELETE：删除，QUERY：查询，LOGIN：登录，LOGOUT：登出）',
    `module` VARCHAR(50) NOT NULL COMMENT '模块名称（USER：用户管理，LEAD：线索管理，EXCHANGE：交换管理等）',
    `function` VARCHAR(100) NOT NULL COMMENT '功能名称',
    `resource_type` VARCHAR(50) COMMENT '资源类型',
    `resource_id` VARCHAR(100) COMMENT '资源ID',
    `request_method` VARCHAR(10) COMMENT '请求方法（GET/POST/PUT/DELETE）',
    `request_url` VARCHAR(500) COMMENT '请求URL',
    `request_params` TEXT COMMENT '请求参数（JSON格式）',
    `response_status` INT COMMENT '响应状态码',
    `response_message` VARCHAR(500) COMMENT '响应消息',
    `ip_address` VARCHAR(50) COMMENT 'IP地址',
    `user_agent` VARCHAR(500) COMMENT '用户代理',
    `location` VARCHAR(100) COMMENT '地理位置',
    `duration` INT COMMENT '执行时长（毫秒）',
    `success` TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功（0：失败，1：成功）',
    `error_message` VARCHAR(1000) COMMENT '错误信息',
    `old_value` TEXT COMMENT '修改前的值（JSON格式）',
    `new_value` TEXT COMMENT '修改后的值（JSON格式）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_trace_id` (`trace_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_operation_type` (`operation_type`),
    KEY `idx_module` (`module`),
    KEY `idx_resource_type` (`resource_type`),
    KEY `idx_resource_id` (`resource_id`),
    KEY `idx_ip_address` (`ip_address`),
    KEY `idx_success` (`success`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_composite_query` (`user_id`, `module`, `create_time`),
    KEY `idx_security_audit` (`operation_type`, `success`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统操作审计日志表';

-- =============================================
-- 数据字典表
-- =============================================
CREATE TABLE `data_dictionaries` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '字典ID',
    `dict_type` VARCHAR(50) NOT NULL COMMENT '字典类型',
    `dict_code` VARCHAR(50) NOT NULL COMMENT '字典编码',
    `dict_label` VARCHAR(100) NOT NULL COMMENT '字典标签',
    `dict_value` VARCHAR(100) NOT NULL COMMENT '字典值',
    `parent_code` VARCHAR(50) COMMENT '父字典编码',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `css_class` VARCHAR(100) COMMENT 'CSS类名',
    `list_class` VARCHAR(100) COMMENT '列表样式',
    `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认（0：否，1：是）',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    `description` VARCHAR(500) COMMENT '描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dict_type_code` (`dict_type`, `dict_code`),
    KEY `idx_dict_type` (`dict_type`),
    KEY `idx_dict_code` (`dict_code`),
    KEY `idx_parent_code` (`parent_code`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数据字典表';