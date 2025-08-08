-- 通知模板初始化数据
-- 为交换申请相关事件创建默认通知模板

-- 插入系统通知模板
INSERT INTO notification_templates (
    template_name, template_code, template_type, event_type, 
    template_title, template_content, is_enabled, 
    created_at, updated_at
) VALUES 
-- 交换申请提交 - 系统通知
(
    '交换申请提交通知', 'EXCHANGE_APPLICATION_SUBMITTED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '您有新的交换申请',
    '用户 ${applicantName} 申请交换您的线索「${targetLeadTitle}」，提供线索：${offeredLeadTitles}。请及时处理。',
    1, NOW(), NOW()
),

-- 交换申请批准 - 系统通知
(
    '交换申请批准通知', 'EXCHANGE_APPLICATION_APPROVED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '交换申请已通过',
    '您的交换申请已被批准！线索「${targetLeadTitle}」的交换申请已通过，交换将自动执行。审核意见：${responseMessage}',
    1, NOW(), NOW()
),

-- 交换申请拒绝 - 系统通知
(
    '交换申请拒绝通知', 'EXCHANGE_APPLICATION_REJECTED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '交换申请被拒绝',
    '很遗憾，您的交换申请被拒绝。线索：${targetLeadTitle}。拒绝原因：${responseMessage}',
    1, NOW(), NOW()
),

-- 交换申请取消 - 系统通知
(
    '交换申请取消通知', 'EXCHANGE_APPLICATION_CANCELLED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '交换申请已取消',
    '交换申请已被取消。线索：${targetLeadTitle}。取消原因：${remark}',
    1, NOW(), NOW()
),

-- 交换完成 - 系统通知
(
    '交换完成通知', 'EXCHANGE_APPLICATION_COMPLETED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '线索交换已完成',
    '恭喜！线索交换已成功完成。您获得了线索「${targetLeadTitle}」，同时提供了线索：${offeredLeadTitles}。',
    1, NOW(), NOW()
),

-- 交换过期 - 系统通知
(
    '交换申请过期通知', 'EXCHANGE_APPLICATION_EXPIRED_SYSTEM', 'SYSTEM', 'EXCHANGE_APPLICATION',
    '交换申请已过期',
    '您的交换申请已过期。线索：${targetLeadTitle}。过期原因：${remark}',
    1, NOW(), NOW()
),

-- 交换申请提交 - 短信通知
(
    '交换申请提交短信', 'EXCHANGE_APPLICATION_SUBMITTED_SMS', 'SMS', 'EXCHANGE_APPLICATION',
    '线索交换申请',
    '【线索流通】您有新的交换申请，用户${applicantName}申请交换您的线索，请登录系统查看详情。',
    1, NOW(), NOW()
),

-- 交换申请批准 - 短信通知
(
    '交换申请批准短信', 'EXCHANGE_APPLICATION_APPROVED_SMS', 'SMS', 'EXCHANGE_APPLICATION',
    '交换申请通过',
    '【线索流通】恭喜！您的交换申请已通过，线索交换将自动执行，请登录查看详情。',
    1, NOW(), NOW()
),

-- 交换申请拒绝 - 短信通知
(
    '交换申请拒绝短信', 'EXCHANGE_APPLICATION_REJECTED_SMS', 'SMS', 'EXCHANGE_APPLICATION',
    '交换申请被拒绝',
    '【线索流通】您的交换申请被拒绝，请登录系统查看详细原因。',
    1, NOW(), NOW()
),

-- 交换完成 - 短信通知
(
    '交换完成短信', 'EXCHANGE_APPLICATION_COMPLETED_SMS', 'SMS', 'EXCHANGE_APPLICATION',
    '线索交换完成',
    '【线索流通】线索交换已完成，您已获得新的线索，请登录系统查看。',
    1, NOW(), NOW()
),

-- 交换申请提交 - 邮件通知
(
    '交换申请提交邮件', 'EXCHANGE_APPLICATION_SUBMITTED_EMAIL', 'EMAIL', 'EXCHANGE_APPLICATION',
    '您有新的线索交换申请',
    '<h2>线索交换申请通知</h2><p>尊敬的用户，</p><p>用户 <strong>${applicantName}</strong> 申请交换您的线索「<strong>${targetLeadTitle}</strong>」。</p><p><strong>提供的线索：</strong></p><ul>${offeredLeadList}</ul><p>请登录系统及时处理该申请。</p><p>此致<br/>线索流通平台</p>',
    1, NOW(), NOW()
),

-- 交换申请批准 - 邮件通知
(
    '交换申请批准邮件', 'EXCHANGE_APPLICATION_APPROVED_EMAIL', 'EMAIL', 'EXCHANGE_APPLICATION',
    '交换申请已通过审核',
    '<h2>交换申请通过通知</h2><p>恭喜！</p><p>您的交换申请已被批准，线索「<strong>${targetLeadTitle}</strong>」的交换将自动执行。</p><p><strong>审核意见：</strong>${responseMessage}</p><p>请登录系统查看交换详情。</p><p>此致<br/>线索流通平台</p>',
    1, NOW(), NOW()
),

-- 交换申请拒绝 - 邮件通知
(
    '交换申请拒绝邮件', 'EXCHANGE_APPLICATION_REJECTED_EMAIL', 'EMAIL', 'EXCHANGE_APPLICATION',
    '交换申请被拒绝',
    '<h2>交换申请拒绝通知</h2><p>很遗憾，您的交换申请被拒绝。</p><p><strong>申请线索：</strong>${targetLeadTitle}</p><p><strong>拒绝原因：</strong>${responseMessage}</p><p>您可以尝试申请其他线索或优化您的线索质量。</p><p>此致<br/>线索流通平台</p>',
    1, NOW(), NOW()
),

-- 交换完成 - 邮件通知
(
    '交换完成邮件', 'EXCHANGE_APPLICATION_COMPLETED_EMAIL', 'EMAIL', 'EXCHANGE_APPLICATION',
    '线索交换已成功完成',
    '<h2>交换完成通知</h2><p>恭喜！线索交换已成功完成。</p><p><strong>获得线索：</strong>${targetLeadTitle}</p><p><strong>提供线索：</strong></p><ul>${offeredLeadList}</ul><p>请登录系统查看详细的线索信息。</p><p>此致<br/>线索流通平台</p>',
    1, NOW(), NOW()
);

-- 插入默认通知设置
-- 为系统中的用户创建默认通知设置（这里假设已有用户数据）
INSERT INTO notification_settings (
    user_id, notification_type, system_enabled, sms_enabled, 
    email_enabled, wechat_enabled, do_not_disturb_start, 
    do_not_disturb_end, frequency_limit_minutes, 
    created_at, updated_at
) 
SELECT 
    u.id as user_id,
    'EXCHANGE_APPLICATION' as notification_type,
    1 as system_enabled,
    1 as sms_enabled,
    1 as email_enabled,
    0 as wechat_enabled,
    '22:00:00' as do_not_disturb_start,
    '08:00:00' as do_not_disturb_end,
    60 as frequency_limit_minutes,
    NOW() as created_at,
    NOW() as updated_at
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM notification_settings ns 
    WHERE ns.user_id = u.id AND ns.notification_type = 'EXCHANGE_APPLICATION'
);

-- 为其他通知类型创建默认设置
INSERT INTO notification_settings (
    user_id, notification_type, system_enabled, sms_enabled, 
    email_enabled, wechat_enabled, do_not_disturb_start, 
    do_not_disturb_end, frequency_limit_minutes, 
    created_at, updated_at
) 
SELECT 
    u.id as user_id,
    'SYSTEM_ANNOUNCEMENT' as notification_type,
    1 as system_enabled,
    0 as sms_enabled,
    1 as email_enabled,
    0 as wechat_enabled,
    '22:00:00' as do_not_disturb_start,
    '08:00:00' as do_not_disturb_end,
    1440 as frequency_limit_minutes, -- 24小时
    NOW() as created_at,
    NOW() as updated_at
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM notification_settings ns 
    WHERE ns.user_id = u.id AND ns.notification_type = 'SYSTEM_ANNOUNCEMENT'
);

INSERT INTO notification_settings (
    user_id, notification_type, system_enabled, sms_enabled, 
    email_enabled, wechat_enabled, do_not_disturb_start, 
    do_not_disturb_end, frequency_limit_minutes, 
    created_at, updated_at
) 
SELECT 
    u.id as user_id,
    'LEAD_RATING_UPDATE' as notification_type,
    1 as system_enabled,
    0 as sms_enabled,
    0 as email_enabled,
    0 as wechat_enabled,
    '22:00:00' as do_not_disturb_start,
    '08:00:00' as do_not_disturb_end,
    120 as frequency_limit_minutes, -- 2小时
    NOW() as created_at,
    NOW() as updated_at
FROM users u
WHERE NOT EXISTS (
    SELECT 1 FROM notification_settings ns 
    WHERE ns.user_id = u.id AND ns.notification_type = 'LEAD_RATING_UPDATE'
);

-- 提交事务
COMMIT;

-- 查询验证数据
SELECT 
    template_name,
    template_code,
    template_type,
    event_type,
    is_enabled
FROM notification_templates
ORDER BY template_type, event_type, template_code;

SELECT 
    COUNT(*) as total_templates,
    template_type,
    COUNT(CASE WHEN is_enabled = 1 THEN 1 END) as enabled_count
FROM notification_templates
GROUP BY template_type;

SELECT 
    COUNT(*) as total_settings,
    notification_type,
    COUNT(CASE WHEN system_enabled = 1 THEN 1 END) as system_enabled_count,
    COUNT(CASE WHEN sms_enabled = 1 THEN 1 END) as sms_enabled_count,
    COUNT(CASE WHEN email_enabled = 1 THEN 1 END) as email_enabled_count
FROM notification_settings
GROUP BY notification_type;