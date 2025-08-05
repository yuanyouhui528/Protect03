-- =============================================
-- 招商线索流通Web应用数据库创建脚本
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 创建数据库和基础配置
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `lead_exchange` 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE `lead_exchange`;

-- 设置SQL模式
SET sql_mode = 'STRICT_TRANS_TABLES,NO_ZERO_DATE,NO_ZERO_IN_DATE,ERROR_FOR_DIVISION_BY_ZERO';

-- 设置时区
SET time_zone = '+08:00';

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection = utf8mb4;