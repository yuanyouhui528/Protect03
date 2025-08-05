-- =============================================
-- 数据库索引优化方案
-- 版本: 1.0.0
-- 创建时间: 2024-01-01
-- 描述: 针对高频查询场景的索引优化
-- =============================================

USE `lead_exchange`;

-- =============================================
-- 用户管理模块索引优化
-- =============================================

-- 用户表复合索引优化
CREATE INDEX `idx_users_status_verified` ON `users` (`status`, `verified`);
CREATE INDEX `idx_users_company_industry` ON `users` (`company_name`, `industry`);
CREATE INDEX `idx_users_region_status` ON `users` (`region_code`, `status`);
CREATE INDEX `idx_users_login_stats` ON `users` (`last_login_time`, `login_count`);

-- 用户角色关联表优化
CREATE INDEX `idx_user_roles_composite` ON `user_roles` (`user_id`, `role_id`, `create_time`);

-- 角色权限关联表优化
CREATE INDEX `idx_role_permissions_composite` ON `role_permissions` (`role_id`, `permission_id`, `create_time`);

-- =============================================
-- 线索管理模块索引优化
-- =============================================

-- 线索表核心查询索引
CREATE INDEX `idx_leads_search_main` ON `leads` (`status`, `category_id`, `region_id`, `rating`, `published_time`);
CREATE INDEX `idx_leads_publisher_status` ON `leads` (`publisher_id`, `status`, `create_time`);
CREATE INDEX `idx_leads_rating_investment` ON `leads` (`rating`, `investment_amount`, `published_time`);
CREATE INDEX `idx_leads_expire_status` ON `leads` (`expire_time`, `status`);
CREATE INDEX `idx_leads_audit_queue` ON `leads` (`status`, `create_time`) WHERE `status` IN ('PENDING', 'DRAFT');
CREATE INDEX `idx_leads_hot_ranking` ON `leads` (`view_count`, `favorite_count`, `exchange_count`, `published_time`);

-- 线索地理位置索引（空间索引）
CREATE SPATIAL INDEX `idx_leads_geo_location` ON `leads` (`longitude`, `latitude`);

-- 线索收藏表优化
CREATE INDEX `idx_lead_favorites_user_time` ON `lead_favorites` (`user_id`, `create_time`);
CREATE INDEX `idx_lead_favorites_lead_stats` ON `lead_favorites` (`lead_id`, `create_time`);

-- 线索浏览记录优化
CREATE INDEX `idx_lead_views_stats` ON `lead_views` (`lead_id`, `create_time`, `user_id`);
CREATE INDEX `idx_lead_views_user_behavior` ON `lead_views` (`user_id`, `create_time`, `view_duration`);
CREATE INDEX `idx_lead_views_ip_analysis` ON `lead_views` (`ip_address`, `create_time`);

-- 线索审核日志优化
CREATE INDEX `idx_lead_audit_logs_lead_action` ON `lead_audit_logs` (`lead_id`, `action`, `create_time`);
CREATE INDEX `idx_lead_audit_logs_auditor_stats` ON `lead_audit_logs` (`auditor_id`, `action`, `create_time`);

-- =============================================
-- 交换引擎模块索引优化
-- =============================================

-- 交换申请表优化
CREATE INDEX `idx_exchange_requests_requester_status` ON `exchange_requests` (`requester_id`, `status`, `create_time`);
CREATE INDEX `idx_exchange_requests_target_owner` ON `exchange_requests` (`target_lead_owner_id`, `status`, `create_time`);
CREATE INDEX `idx_exchange_requests_expire_pending` ON `exchange_requests` (`expire_time`, `status`) WHERE `status` = 'PENDING';
CREATE INDEX `idx_exchange_requests_credits_analysis` ON `exchange_requests` (`offered_credits`, `required_credits`, `create_time`);

-- 交换交易记录优化
CREATE INDEX `idx_exchange_transactions_parties` ON `exchange_transactions` (`requester_id`, `provider_id`, `completed_time`);
CREATE INDEX `idx_exchange_transactions_status_time` ON `exchange_transactions` (`transaction_status`, `create_time`);
CREATE INDEX `idx_exchange_transactions_lead_stats` ON `exchange_transactions` (`target_lead_id`, `completed_time`);

-- 交换历史表优化
CREATE INDEX `idx_exchange_histories_user_stats` ON `exchange_histories` (`requester_id`, `exchange_time`, `exchange_type`);
CREATE INDEX `idx_exchange_histories_provider_stats` ON `exchange_histories` (`provider_id`, `exchange_time`, `exchange_type`);
CREATE INDEX `idx_exchange_histories_lead_tracking` ON `exchange_histories` (`lead_id`, `exchange_time`);

-- 积分变动记录优化
CREATE INDEX `idx_credit_transactions_user_type` ON `credit_transactions` (`user_id`, `transaction_type`, `create_time`);
CREATE INDEX `idx_credit_transactions_source_tracking` ON `credit_transactions` (`source_type`, `source_id`, `create_time`);
CREATE INDEX `idx_credit_transactions_amount_analysis` ON `credit_transactions` (`amount`, `transaction_type`, `create_time`);

-- =============================================
-- 通知服务模块索引优化
-- =============================================

-- 通知消息表优化
CREATE INDEX `idx_notifications_recipient_read` ON `notifications` (`recipient_id`, `is_read`, `create_time`);
CREATE INDEX `idx_notifications_send_queue` ON `notifications` (`status`, `send_time`, `priority`) WHERE `status` IN ('PENDING', 'SENDING');
CREATE INDEX `idx_notifications_retry_queue` ON `notifications` (`status`, `retry_count`, `max_retry_count`) WHERE `status` = 'FAILED';
CREATE INDEX `idx_notifications_expire_cleanup` ON `notifications` (`expire_time`, `status`);
CREATE INDEX `idx_notifications_event_stats` ON `notifications` (`event_type`, `notification_type`, `create_time`);

-- 通知发送日志优化
CREATE INDEX `idx_notification_send_logs_stats` ON `notification_send_logs` (`send_channel`, `send_status`, `create_time`);
CREATE INDEX `idx_notification_send_logs_performance` ON `notification_send_logs` (`send_duration`, `create_time`);
CREATE INDEX `idx_notification_send_logs_cost` ON `notification_send_logs` (`provider`, `cost`, `create_time`);

-- =============================================
-- 系统配置模块索引优化
-- =============================================

-- 审计日志表优化
CREATE INDEX `idx_audit_logs_user_module` ON `audit_logs` (`user_id`, `module`, `create_time`);
CREATE INDEX `idx_audit_logs_operation_success` ON `audit_logs` (`operation_type`, `success`, `create_time`);
CREATE INDEX `idx_audit_logs_resource_tracking` ON `audit_logs` (`resource_type`, `resource_id`, `create_time`);
CREATE INDEX `idx_audit_logs_security_analysis` ON `audit_logs` (`ip_address`, `success`, `create_time`);
CREATE INDEX `idx_audit_logs_performance` ON `audit_logs` (`duration`, `create_time`);

-- 系统配置表优化
CREATE INDEX `idx_system_configs_group_status` ON `system_configs` (`config_group`, `status`);
CREATE INDEX `idx_system_configs_public_status` ON `system_configs` (`is_public`, `status`);

-- 评级规则表优化
CREATE INDEX `idx_rating_rules_type_status` ON `rating_rules` (`rule_type`, `status`, `effective_time`);
CREATE INDEX `idx_rating_rules_effective_period` ON `rating_rules` (`effective_time`, `expire_time`, `status`);

-- 交换规则表优化
CREATE INDEX `idx_exchange_rules_type_rating` ON `exchange_rules` (`rule_type`, `rating_level`, `status`);
CREATE INDEX `idx_exchange_rules_priority_effective` ON `exchange_rules` (`priority`, `effective_time`, `status`);

-- =============================================
-- 分区表优化（针对大数据量表）
-- =============================================

-- 线索浏览记录表按月分区
ALTER TABLE `lead_views` 
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 审计日志表按月分区
ALTER TABLE `audit_logs` 
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 通知发送日志表按月分区
ALTER TABLE `notification_send_logs` 
PARTITION BY RANGE (YEAR(create_time) * 100 + MONTH(create_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p202407 VALUES LESS THAN (202408),
    PARTITION p202408 VALUES LESS THAN (202409),
    PARTITION p202409 VALUES LESS THAN (202410),
    PARTITION p202410 VALUES LESS THAN (202411),
    PARTITION p202411 VALUES LESS THAN (202412),
    PARTITION p202412 VALUES LESS THAN (202501),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- =============================================
-- 索引使用建议和监控
-- =============================================

-- 创建索引使用统计视图
CREATE VIEW `v_index_usage_stats` AS
SELECT 
    t.TABLE_SCHEMA,
    t.TABLE_NAME,
    s.INDEX_NAME,
    s.COLUMN_NAME,
    s.SEQ_IN_INDEX,
    s.CARDINALITY,
    CASE 
        WHEN s.CARDINALITY = 0 THEN 'LOW'
        WHEN s.CARDINALITY < 1000 THEN 'MEDIUM'
        ELSE 'HIGH'
    END AS SELECTIVITY_LEVEL
FROM 
    information_schema.TABLES t
JOIN 
    information_schema.STATISTICS s ON t.TABLE_SCHEMA = s.TABLE_SCHEMA AND t.TABLE_NAME = s.TABLE_NAME
WHERE 
    t.TABLE_SCHEMA = 'lead_exchange'
    AND t.TABLE_TYPE = 'BASE TABLE'
ORDER BY 
    t.TABLE_NAME, s.INDEX_NAME, s.SEQ_IN_INDEX;

-- 创建慢查询分析建议
-- 注意：以下是建议的慢查询监控配置，需要在MySQL配置文件中设置
/*
慢查询日志配置建议：

[mysqld]
slow_query_log = 1
slow_query_log_file = /var/log/mysql/slow.log
long_query_time = 0.5
log_queries_not_using_indexes = 1
log_slow_admin_statements = 1
log_slow_slave_statements = 1
min_examined_row_limit = 100

定期执行以下查询来分析索引效果：

1. 查看未使用的索引：
SELECT * FROM sys.schema_unused_indexes WHERE object_schema = 'lead_exchange';

2. 查看重复的索引：
SELECT * FROM sys.schema_redundant_indexes WHERE table_schema = 'lead_exchange';

3. 查看表的统计信息：
SELECT * FROM sys.schema_table_statistics WHERE table_schema = 'lead_exchange';

4. 查看索引的使用情况：
SELECT * FROM sys.schema_index_statistics WHERE table_schema = 'lead_exchange';
*/