-- =============================================
-- 用户管理模块表结构
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 用户、角色、权限相关表
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 用户表
-- =============================================
CREATE TABLE `users` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `phone` VARCHAR(255) NOT NULL COMMENT '手机号（加密存储）',
    `email` VARCHAR(255) COMMENT '邮箱（加密存储）',
    `real_name` VARCHAR(100) COMMENT '真实姓名',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `company_name` VARCHAR(200) COMMENT '企业名称',
    `company_type` VARCHAR(50) COMMENT '企业类型',
    `industry` VARCHAR(100) COMMENT '所属行业',
    `position` VARCHAR(100) COMMENT '职位',
    `region_code` VARCHAR(20) COMMENT '地区编码',
    `region_name` VARCHAR(100) COMMENT '地区名称',
    `business_license` VARCHAR(100) COMMENT '营业执照号',
    `credit_code` VARCHAR(50) COMMENT '统一社会信用代码',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '用户状态（0：禁用，1：正常，2：待审核）',
    `verified` TINYINT NOT NULL DEFAULT 0 COMMENT '认证状态（0：未认证，1：已认证）',
    `last_login_time` DATETIME COMMENT '最后登录时间',
    `last_login_ip` VARCHAR(50) COMMENT '最后登录IP',
    `login_count` INT NOT NULL DEFAULT 0 COMMENT '登录次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT COMMENT '创建人ID',
    `update_by` BIGINT COMMENT '更新人ID',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记（0：未删除，1：已删除）',
    `version` INT NOT NULL DEFAULT 1 COMMENT '版本号（乐观锁）',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`),
    KEY `idx_company_name` (`company_name`),
    KEY `idx_industry` (`industry`),
    KEY `idx_region_code` (`region_code`),
    KEY `idx_status` (`status`),
    KEY `idx_verified` (`verified`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 角色表
-- =============================================
CREATE TABLE `roles` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(200) COMMENT '角色描述',
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
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_role_name` (`role_name`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =============================================
-- 权限表
-- =============================================
CREATE TABLE `permissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `permission_type` VARCHAR(20) NOT NULL COMMENT '权限类型（menu：菜单，button：按钮，api：接口）',
    `parent_id` BIGINT DEFAULT 0 COMMENT '父权限ID',
    `path` VARCHAR(200) COMMENT '路径',
    `component` VARCHAR(200) COMMENT '组件',
    `icon` VARCHAR(100) COMMENT '图标',
    `method` VARCHAR(10) COMMENT 'HTTP方法',
    `url` VARCHAR(200) COMMENT 'API地址',
    `description` VARCHAR(200) COMMENT '权限描述',
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
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_permission_name` (`permission_name`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_sort_order` (`sort_order`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- =============================================
-- 用户角色关联表
-- =============================================
CREATE TABLE `user_roles` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =============================================
-- 角色权限关联表
-- =============================================
CREATE TABLE `role_permissions` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT COMMENT '创建人ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`),
    CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_role_permissions_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';