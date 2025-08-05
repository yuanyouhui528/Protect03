-- =============================================
-- 招商线索流通系统 - 测试数据初始化脚本
-- 版本: 1.0
-- 创建时间: 2024-01-01
-- 说明: 用于开发和测试环境的基础数据初始化
-- =============================================

USE lead_exchange;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 系统配置数据
-- =============================================

-- 系统基础配置
INSERT INTO system_configs (config_key, config_value, config_type, config_group, config_name, description, is_encrypted, validation_rule, default_value, sort_order, created_by, updated_by) VALUES
('system.name', '招商线索流通平台', 'STRING', 'SYSTEM', '系统名称', '系统显示名称', 0, NULL, '招商线索流通平台', 1, 1, 1),
('system.version', '1.0.0', 'STRING', 'SYSTEM', '系统版本', '当前系统版本号', 0, NULL, '1.0.0', 2, 1, 1),
('system.logo', '/static/images/logo.png', 'STRING', 'SYSTEM', '系统Logo', '系统Logo图片路径', 0, NULL, '/static/images/logo.png', 3, 1, 1),
('system.copyright', '© 2024 招商线索流通平台', 'STRING', 'SYSTEM', '版权信息', '系统版权信息', 0, NULL, '© 2024 招商线索流通平台', 4, 1, 1),
('jwt.secret', 'leadexchange2024secret', 'STRING', 'SECURITY', 'JWT密钥', 'JWT令牌签名密钥', 1, NULL, 'leadexchange2024secret', 10, 1, 1),
('jwt.expiration', '86400', 'INTEGER', 'SECURITY', 'JWT过期时间', 'JWT令牌过期时间(秒)', 0, 'min:3600,max:604800', '86400', 11, 1, 1),
('upload.max.size', '10485760', 'INTEGER', 'UPLOAD', '文件上传大小限制', '单个文件最大上传大小(字节)', 0, 'min:1048576,max:104857600', '10485760', 20, 1, 1),
('upload.allowed.types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'STRING', 'UPLOAD', '允许上传文件类型', '允许上传的文件扩展名', 0, NULL, 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 21, 1, 1);

-- 评级规则配置
INSERT INTO rating_rules (rule_name, rule_type, weight, calculation_method, formula, min_value, max_value, description, is_active, sort_order, created_by, updated_by) VALUES
('信息完整度', 'COMPLETENESS', 0.30, 'PERCENTAGE', '(填写字段数/总字段数)*100', 0, 100, '线索信息完整度评分', 1, 1, 1, 1),
('企业资质', 'QUALIFICATION', 0.25, 'SCORE', '认证状态*50 + 营业执照*30 + 行业资质*20', 0, 100, '企业资质认证评分', 1, 2, 1, 1),
('企业规模', 'SCALE', 0.25, 'SCORE', '注册资本权重*40 + 员工规模权重*35 + 年营收权重*25', 0, 100, '企业规模评分', 1, 3, 1, 1),
('产业价值', 'VALUE', 0.20, 'SCORE', '投资金额权重*60 + 行业热度权重*40', 0, 100, '产业投资价值评分', 1, 4, 1, 1);

-- 交换规则配置
INSERT INTO exchange_rules (rule_name, rule_type, credit_value, exchange_ratio, min_credits, max_credits, daily_limit, monthly_limit, description, is_active, sort_order, created_by, updated_by) VALUES
('A级线索交换', 'RATING_A', 8, '1:1', 8, 8, 10, 100, 'A级线索交换规则，价值8积分', 1, 1, 1, 1),
('B级线索交换', 'RATING_B', 4, '2:1', 4, 4, 20, 200, 'B级线索交换规则，价值4积分', 1, 2, 1, 1),
('C级线索交换', 'RATING_C', 2, '4:1', 2, 2, 40, 400, 'C级线索交换规则，价值2积分', 1, 3, 1, 1),
('D级线索交换', 'RATING_D', 1, '8:1', 1, 1, 80, 800, 'D级线索交换规则，价值1积分', 1, 4, 1, 1),
('新用户奖励', 'REWARD_NEW_USER', 20, '1:1', 20, 20, 1, 1, '新用户注册奖励积分', 1, 10, 1, 1),
('发布线索奖励', 'REWARD_PUBLISH', 5, '1:1', 5, 5, 5, 50, '发布线索奖励积分', 1, 11, 1, 1);

-- 数据字典
INSERT INTO data_dictionaries (dict_type, dict_code, dict_name, dict_value, parent_code, sort_order, description, is_active, created_by, updated_by) VALUES
-- 用户状态
('USER_STATUS', 'ACTIVE', '正常', '1', NULL, 1, '用户状态-正常', 1, 1, 1),
('USER_STATUS', 'INACTIVE', '禁用', '0', NULL, 2, '用户状态-禁用', 1, 1, 1),
('USER_STATUS', 'PENDING', '待审核', '2', NULL, 3, '用户状态-待审核', 1, 1, 1),
-- 线索状态
('LEAD_STATUS', 'DRAFT', '草稿', 'DRAFT', NULL, 1, '线索状态-草稿', 1, 1, 1),
('LEAD_STATUS', 'PENDING', '待审核', 'PENDING', NULL, 2, '线索状态-待审核', 1, 1, 1),
('LEAD_STATUS', 'PUBLISHED', '已发布', 'PUBLISHED', NULL, 3, '线索状态-已发布', 1, 1, 1),
('LEAD_STATUS', 'EXCHANGING', '交换中', 'EXCHANGING', NULL, 4, '线索状态-交换中', 1, 1, 1),
('LEAD_STATUS', 'COMPLETED', '已成交', 'COMPLETED', NULL, 5, '线索状态-已成交', 1, 1, 1),
('LEAD_STATUS', 'OFFLINE', '已下架', 'OFFLINE', NULL, 6, '线索状态-已下架', 1, 1, 1),
-- 线索评级
('LEAD_RATING', 'A', 'A级', 'A', NULL, 1, '线索评级-A级', 1, 1, 1),
('LEAD_RATING', 'B', 'B级', 'B', NULL, 2, '线索评级-B级', 1, 1, 1),
('LEAD_RATING', 'C', 'C级', 'C', NULL, 3, '线索评级-C级', 1, 1, 1),
('LEAD_RATING', 'D', 'D级', 'D', NULL, 4, '线索评级-D级', 1, 1, 1),
-- 交换状态
('EXCHANGE_STATUS', 'PENDING', '待处理', 'PENDING', NULL, 1, '交换状态-待处理', 1, 1, 1),
('EXCHANGE_STATUS', 'APPROVED', '已同意', 'APPROVED', NULL, 2, '交换状态-已同意', 1, 1, 1),
('EXCHANGE_STATUS', 'REJECTED', '已拒绝', 'REJECTED', NULL, 3, '交换状态-已拒绝', 1, 1, 1),
('EXCHANGE_STATUS', 'COMPLETED', '已完成', 'COMPLETED', NULL, 4, '交换状态-已完成', 1, 1, 1),
('EXCHANGE_STATUS', 'CANCELLED', '已取消', 'CANCELLED', NULL, 5, '交换状态-已取消', 1, 1, 1),
-- 通知类型
('NOTIFICATION_TYPE', 'SYSTEM', '系统通知', 'SYSTEM', NULL, 1, '通知类型-系统通知', 1, 1, 1),
('NOTIFICATION_TYPE', 'EXCHANGE', '交换通知', 'EXCHANGE', NULL, 2, '通知类型-交换通知', 1, 1, 1),
('NOTIFICATION_TYPE', 'LEAD', '线索通知', 'LEAD', NULL, 3, '通知类型-线索通知', 1, 1, 1),
('NOTIFICATION_TYPE', 'CREDIT', '积分通知', 'CREDIT', NULL, 4, '通知类型-积分通知', 1, 1, 1);

-- =============================================
-- 2. 地区数据
-- =============================================

INSERT INTO lead_regions (region_code, region_name, region_type, parent_id, longitude, latitude, sort_order, created_by, updated_by) VALUES
-- 省级地区
(110000, '北京市', 'PROVINCE', NULL, 116.4074, 39.9042, 1, 1, 1),
(120000, '天津市', 'PROVINCE', NULL, 117.1901, 39.1084, 2, 1, 1),
(310000, '上海市', 'PROVINCE', NULL, 121.4737, 31.2304, 3, 1, 1),
(500000, '重庆市', 'PROVINCE', NULL, 106.5516, 29.5630, 4, 1, 1),
(440000, '广东省', 'PROVINCE', NULL, 113.2644, 23.1291, 5, 1, 1),
(320000, '江苏省', 'PROVINCE', NULL, 118.7674, 32.0415, 6, 1, 1),
(330000, '浙江省', 'PROVINCE', NULL, 120.1536, 30.2650, 7, 1, 1),
(370000, '山东省', 'PROVINCE', NULL, 117.0009, 36.6758, 8, 1, 1);

-- 获取省级地区ID用于插入市级地区
SET @beijing_id = (SELECT id FROM lead_regions WHERE region_code = 110000);
SET @guangdong_id = (SELECT id FROM lead_regions WHERE region_code = 440000);
SET @jiangsu_id = (SELECT id FROM lead_regions WHERE region_code = 320000);
SET @zhejiang_id = (SELECT id FROM lead_regions WHERE region_code = 330000);

-- 市级地区
INSERT INTO lead_regions (region_code, region_name, region_type, parent_id, longitude, latitude, sort_order, created_by, updated_by) VALUES
-- 北京市下属区
(110100, '北京市区', 'CITY', @beijing_id, 116.4074, 39.9042, 1, 1, 1),
-- 广东省下属市
(440100, '广州市', 'CITY', @guangdong_id, 113.2644, 23.1291, 1, 1, 1),
(440300, '深圳市', 'CITY', @guangdong_id, 114.0579, 22.5431, 2, 1, 1),
(440600, '佛山市', 'CITY', @guangdong_id, 113.1220, 23.0288, 3, 1, 1),
(441900, '东莞市', 'CITY', @guangdong_id, 113.7518, 23.0489, 4, 1, 1),
-- 江苏省下属市
(320100, '南京市', 'CITY', @jiangsu_id, 118.7674, 32.0415, 1, 1, 1),
(320200, '无锡市', 'CITY', @jiangsu_id, 120.3019, 31.5747, 2, 1, 1),
(320500, '苏州市', 'CITY', @jiangsu_id, 120.6197, 31.2989, 3, 1, 1),
-- 浙江省下属市
(330100, '杭州市', 'CITY', @zhejiang_id, 120.1536, 30.2650, 1, 1, 1),
(330200, '宁波市', 'CITY', @zhejiang_id, 121.5440, 29.8683, 2, 1, 1),
(330300, '温州市', 'CITY', @zhejiang_id, 120.6994, 27.9944, 3, 1, 1);

-- =============================================
-- 3. 行业分类数据
-- =============================================

INSERT INTO lead_categories (category_code, category_name, parent_id, sort_order, description, created_by, updated_by) VALUES
-- 一级分类
('01', '制造业', NULL, 1, '制造业相关行业', 1, 1),
('02', '信息技术', NULL, 2, '信息技术相关行业', 1, 1),
('03', '金融服务', NULL, 3, '金融服务相关行业', 1, 1),
('04', '房地产', NULL, 4, '房地产相关行业', 1, 1),
('05', '新能源', NULL, 5, '新能源相关行业', 1, 1),
('06', '生物医药', NULL, 6, '生物医药相关行业', 1, 1),
('07', '现代服务业', NULL, 7, '现代服务业相关行业', 1, 1),
('08', '文化创意', NULL, 8, '文化创意相关行业', 1, 1);

-- 获取一级分类ID用于插入二级分类
SET @manufacturing_id = (SELECT id FROM lead_categories WHERE category_code = '01');
SET @it_id = (SELECT id FROM lead_categories WHERE category_code = '02');
SET @finance_id = (SELECT id FROM lead_categories WHERE category_code = '03');
SET @realestate_id = (SELECT id FROM lead_categories WHERE category_code = '04');
SET @newenergy_id = (SELECT id FROM lead_categories WHERE category_code = '05');
SET @biomedical_id = (SELECT id FROM lead_categories WHERE category_code = '06');

-- 二级分类
INSERT INTO lead_categories (category_code, category_name, parent_id, sort_order, description, created_by, updated_by) VALUES
-- 制造业子分类
('0101', '汽车制造', @manufacturing_id, 1, '汽车及零部件制造', 1, 1),
('0102', '机械设备', @manufacturing_id, 2, '机械设备制造', 1, 1),
('0103', '电子制造', @manufacturing_id, 3, '电子产品制造', 1, 1),
('0104', '纺织服装', @manufacturing_id, 4, '纺织服装制造', 1, 1),
-- 信息技术子分类
('0201', '软件开发', @it_id, 1, '软件开发与服务', 1, 1),
('0202', '人工智能', @it_id, 2, '人工智能技术', 1, 1),
('0203', '大数据', @it_id, 3, '大数据技术与应用', 1, 1),
('0204', '云计算', @it_id, 4, '云计算服务', 1, 1),
('0205', '物联网', @it_id, 5, '物联网技术', 1, 1),
-- 金融服务子分类
('0301', '银行业', @finance_id, 1, '银行金融服务', 1, 1),
('0302', '保险业', @finance_id, 2, '保险服务', 1, 1),
('0303', '证券业', @finance_id, 3, '证券投资服务', 1, 1),
('0304', '互联网金融', @finance_id, 4, '互联网金融服务', 1, 1),
-- 房地产子分类
('0401', '住宅开发', @realestate_id, 1, '住宅房地产开发', 1, 1),
('0402', '商业地产', @realestate_id, 2, '商业房地产开发', 1, 1),
('0403', '工业地产', @realestate_id, 3, '工业房地产开发', 1, 1),
-- 新能源子分类
('0501', '太阳能', @newenergy_id, 1, '太阳能发电', 1, 1),
('0502', '风能', @newenergy_id, 2, '风力发电', 1, 1),
('0503', '储能技术', @newenergy_id, 3, '储能技术与设备', 1, 1),
('0504', '新能源汽车', @newenergy_id, 4, '新能源汽车制造', 1, 1),
-- 生物医药子分类
('0601', '药物研发', @biomedical_id, 1, '药物研发与生产', 1, 1),
('0602', '医疗器械', @biomedical_id, 2, '医疗器械制造', 1, 1),
('0603', '生物技术', @biomedical_id, 3, '生物技术研发', 1, 1);

-- =============================================
-- 4. 权限和角色数据
-- =============================================

-- 权限数据
INSERT INTO permissions (permission_code, permission_name, permission_type, resource_url, parent_id, sort_order, description, created_by, updated_by) VALUES
-- 系统管理权限
('SYSTEM', '系统管理', 'MENU', '/system', NULL, 1, '系统管理菜单', 1, 1),
('SYSTEM:USER', '用户管理', 'MENU', '/system/user', 1, 1, '用户管理菜单', 1, 1),
('SYSTEM:USER:LIST', '用户列表', 'BUTTON', NULL, 2, 1, '查看用户列表', 1, 1),
('SYSTEM:USER:ADD', '新增用户', 'BUTTON', NULL, 2, 2, '新增用户', 1, 1),
('SYSTEM:USER:EDIT', '编辑用户', 'BUTTON', NULL, 2, 3, '编辑用户信息', 1, 1),
('SYSTEM:USER:DELETE', '删除用户', 'BUTTON', NULL, 2, 4, '删除用户', 1, 1),
('SYSTEM:ROLE', '角色管理', 'MENU', '/system/role', 1, 2, '角色管理菜单', 1, 1),
('SYSTEM:ROLE:LIST', '角色列表', 'BUTTON', NULL, 7, 1, '查看角色列表', 1, 1),
('SYSTEM:ROLE:ADD', '新增角色', 'BUTTON', NULL, 7, 2, '新增角色', 1, 1),
('SYSTEM:ROLE:EDIT', '编辑角色', 'BUTTON', NULL, 7, 3, '编辑角色信息', 1, 1),
('SYSTEM:ROLE:DELETE', '删除角色', 'BUTTON', NULL, 7, 4, '删除角色', 1, 1),

-- 线索管理权限
('LEAD', '线索管理', 'MENU', '/lead', NULL, 2, '线索管理菜单', 1, 1),
('LEAD:LIST', '线索列表', 'MENU', '/lead/list', 12, 1, '线索列表菜单', 1, 1),
('LEAD:LIST:VIEW', '查看线索', 'BUTTON', NULL, 13, 1, '查看线索详情', 1, 1),
('LEAD:LIST:SEARCH', '搜索线索', 'BUTTON', NULL, 13, 2, '搜索线索', 1, 1),
('LEAD:LIST:FAVORITE', '收藏线索', 'BUTTON', NULL, 13, 3, '收藏线索', 1, 1),
('LEAD:PUBLISH', '发布线索', 'MENU', '/lead/publish', 12, 2, '发布线索菜单', 1, 1),
('LEAD:PUBLISH:ADD', '新增线索', 'BUTTON', NULL, 17, 1, '新增线索', 1, 1),
('LEAD:PUBLISH:EDIT', '编辑线索', 'BUTTON', NULL, 17, 2, '编辑线索', 1, 1),
('LEAD:PUBLISH:DELETE', '删除线索', 'BUTTON', NULL, 17, 3, '删除线索', 1, 1),
('LEAD:MANAGE', '线索管理', 'MENU', '/lead/manage', 12, 3, '我的线索管理', 1, 1),
('LEAD:MANAGE:LIST', '我的线索', 'BUTTON', NULL, 21, 1, '查看我的线索', 1, 1),
('LEAD:MANAGE:AUDIT', '线索审核', 'BUTTON', NULL, 21, 2, '审核线索', 1, 1),

-- 交换管理权限
('EXCHANGE', '交换管理', 'MENU', '/exchange', NULL, 3, '交换管理菜单', 1, 1),
('EXCHANGE:REQUEST', '交换申请', 'MENU', '/exchange/request', 24, 1, '交换申请菜单', 1, 1),
('EXCHANGE:REQUEST:SUBMIT', '提交申请', 'BUTTON', NULL, 25, 1, '提交交换申请', 1, 1),
('EXCHANGE:REQUEST:CANCEL', '取消申请', 'BUTTON', NULL, 25, 2, '取消交换申请', 1, 1),
('EXCHANGE:HISTORY', '交换历史', 'MENU', '/exchange/history', 24, 2, '交换历史菜单', 1, 1),
('EXCHANGE:HISTORY:VIEW', '查看历史', 'BUTTON', NULL, 28, 1, '查看交换历史', 1, 1),
('EXCHANGE:CREDITS', '积分管理', 'MENU', '/exchange/credits', 24, 3, '积分管理菜单', 1, 1),
('EXCHANGE:CREDITS:VIEW', '查看积分', 'BUTTON', NULL, 30, 1, '查看积分余额', 1, 1),

-- API权限
('API:USER:LIST', '用户列表API', 'API', '/api/users', NULL, 100, '用户列表API权限', 1, 1),
('API:USER:CREATE', '创建用户API', 'API', '/api/users', NULL, 101, '创建用户API权限', 1, 1),
('API:USER:UPDATE', '更新用户API', 'API', '/api/users/*', NULL, 102, '更新用户API权限', 1, 1),
('API:USER:DELETE', '删除用户API', 'API', '/api/users/*', NULL, 103, '删除用户API权限', 1, 1),
('API:LEAD:LIST', '线索列表API', 'API', '/api/leads', NULL, 110, '线索列表API权限', 1, 1),
('API:LEAD:CREATE', '创建线索API', 'API', '/api/leads', NULL, 111, '创建线索API权限', 1, 1),
('API:LEAD:UPDATE', '更新线索API', 'API', '/api/leads/*', NULL, 112, '更新线索API权限', 1, 1),
('API:LEAD:DELETE', '删除线索API', 'API', '/api/leads/*', NULL, 113, '删除线索API权限', 1, 1),
('API:EXCHANGE:REQUEST', '交换申请API', 'API', '/api/exchanges/request', NULL, 120, '交换申请API权限', 1, 1),
('API:EXCHANGE:APPROVE', '交换审批API', 'API', '/api/exchanges/approve', NULL, 121, '交换审批API权限', 1, 1);

-- 角色数据
INSERT INTO roles (role_code, role_name, description, is_default, sort_order, created_by, updated_by) VALUES
('SUPER_ADMIN', '超级管理员', '系统超级管理员，拥有所有权限', 0, 1, 1, 1),
('ADMIN', '系统管理员', '系统管理员，拥有系统管理权限', 0, 2, 1, 1),
('LEAD_MANAGER', '线索管理员', '线索管理员，负责线索审核和管理', 0, 3, 1, 1),
('ENTERPRISE_USER', '企业用户', '企业用户，可以发布和交换线索', 1, 4, 1, 1),
('VISITOR', '访客用户', '访客用户，只能浏览线索', 0, 5, 1, 1);

-- 角色权限关联
INSERT INTO role_permissions (role_id, permission_id, created_by) 
SELECT r.id, p.id, 1
FROM roles r, permissions p 
WHERE r.role_code = 'SUPER_ADMIN';

-- 系统管理员权限（除了超级管理员的部分权限）
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT r.id, p.id, 1
FROM roles r, permissions p
WHERE r.role_code = 'ADMIN' 
AND p.permission_code IN (
    'SYSTEM', 'SYSTEM:USER', 'SYSTEM:USER:LIST', 'SYSTEM:USER:ADD', 'SYSTEM:USER:EDIT',
    'SYSTEM:ROLE', 'SYSTEM:ROLE:LIST', 'SYSTEM:ROLE:ADD', 'SYSTEM:ROLE:EDIT',
    'LEAD', 'LEAD:LIST', 'LEAD:LIST:VIEW', 'LEAD:LIST:SEARCH', 'LEAD:MANAGE', 'LEAD:MANAGE:AUDIT',
    'API:USER:LIST', 'API:USER:CREATE', 'API:USER:UPDATE', 'API:LEAD:LIST'
);

-- 线索管理员权限
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT r.id, p.id, 1
FROM roles r, permissions p
WHERE r.role_code = 'LEAD_MANAGER'
AND p.permission_code IN (
    'LEAD', 'LEAD:LIST', 'LEAD:LIST:VIEW', 'LEAD:LIST:SEARCH', 'LEAD:MANAGE', 'LEAD:MANAGE:LIST', 'LEAD:MANAGE:AUDIT',
    'API:LEAD:LIST', 'API:LEAD:UPDATE'
);

-- 企业用户权限
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT r.id, p.id, 1
FROM roles r, permissions p
WHERE r.role_code = 'ENTERPRISE_USER'
AND p.permission_code IN (
    'LEAD', 'LEAD:LIST', 'LEAD:LIST:VIEW', 'LEAD:LIST:SEARCH', 'LEAD:LIST:FAVORITE',
    'LEAD:PUBLISH', 'LEAD:PUBLISH:ADD', 'LEAD:PUBLISH:EDIT', 'LEAD:PUBLISH:DELETE',
    'LEAD:MANAGE', 'LEAD:MANAGE:LIST',
    'EXCHANGE', 'EXCHANGE:REQUEST', 'EXCHANGE:REQUEST:SUBMIT', 'EXCHANGE:REQUEST:CANCEL',
    'EXCHANGE:HISTORY', 'EXCHANGE:HISTORY:VIEW', 'EXCHANGE:CREDITS', 'EXCHANGE:CREDITS:VIEW',
    'API:LEAD:LIST', 'API:LEAD:CREATE', 'API:LEAD:UPDATE', 'API:LEAD:DELETE',
    'API:EXCHANGE:REQUEST'
);

-- 访客用户权限
INSERT INTO role_permissions (role_id, permission_id, created_by)
SELECT r.id, p.id, 1
FROM roles r, permissions p
WHERE r.role_code = 'VISITOR'
AND p.permission_code IN (
    'LEAD', 'LEAD:LIST', 'LEAD:LIST:VIEW', 'LEAD:LIST:SEARCH',
    'API:LEAD:LIST'
);

-- =============================================
-- 5. 通知模板数据
-- =============================================

INSERT INTO notification_templates (template_code, template_name, template_type, title, content, variables, description, is_active, created_by, updated_by) VALUES
-- 系统通知模板
('WELCOME', '欢迎注册', 'SYSTEM', '欢迎加入招商线索流通平台', '尊敬的${realName}，欢迎您注册招商线索流通平台！您已获得${credits}积分奖励，可以开始浏览和交换线索了。', 'realName,credits', '用户注册欢迎通知', 1, 1, 1),
('ACCOUNT_VERIFIED', '账户认证成功', 'SYSTEM', '账户认证审核通过', '恭喜您，${companyName}的企业认证已通过审核！您现在可以发布和交换线索了。', 'companyName', '企业认证通过通知', 1, 1, 1),
('ACCOUNT_REJECTED', '账户认证失败', 'SYSTEM', '账户认证审核未通过', '很抱歉，${companyName}的企业认证未通过审核。原因：${reason}。请重新提交认证资料。', 'companyName,reason', '企业认证拒绝通知', 1, 1, 1),

-- 线索相关通知模板
('LEAD_PUBLISHED', '线索发布成功', 'LEAD', '线索发布成功', '您发布的线索「${leadTitle}」已成功发布，评级为${rating}级，价值${credits}积分。', 'leadTitle,rating,credits', '线索发布成功通知', 1, 1, 1),
('LEAD_APPROVED', '线索审核通过', 'LEAD', '线索审核通过', '您发布的线索「${leadTitle}」已通过审核，现在可以被其他用户查看和交换。', 'leadTitle', '线索审核通过通知', 1, 1, 1),
('LEAD_REJECTED', '线索审核未通过', 'LEAD', '线索审核未通过', '您发布的线索「${leadTitle}」审核未通过。原因：${reason}。请修改后重新提交。', 'leadTitle,reason', '线索审核拒绝通知', 1, 1, 1),
('LEAD_VIEWED', '线索被浏览', 'LEAD', '您的线索被浏览', '您发布的线索「${leadTitle}」被${viewerName}浏览了。', 'leadTitle,viewerName', '线索被浏览通知', 1, 1, 1),
('LEAD_FAVORITED', '线索被收藏', 'LEAD', '您的线索被收藏', '您发布的线索「${leadTitle}」被${userName}收藏了。', 'leadTitle,userName', '线索被收藏通知', 1, 1, 1),

-- 交换相关通知模板
('EXCHANGE_REQUEST', '收到交换申请', 'EXCHANGE', '收到新的交换申请', '${requesterName}申请交换您的线索「${leadTitle}」，提供${offeredCredits}积分。请及时处理。', 'requesterName,leadTitle,offeredCredits', '收到交换申请通知', 1, 1, 1),
('EXCHANGE_APPROVED', '交换申请通过', 'EXCHANGE', '交换申请已通过', '您申请交换的线索「${leadTitle}」已被同意，消耗${credits}积分。联系方式已发送至您的邮箱。', 'leadTitle,credits', '交换申请通过通知', 1, 1, 1),
('EXCHANGE_REJECTED', '交换申请被拒绝', 'EXCHANGE', '交换申请被拒绝', '很抱歉，您申请交换的线索「${leadTitle}」被拒绝。原因：${reason}。', 'leadTitle,reason', '交换申请拒绝通知', 1, 1, 1),
('EXCHANGE_COMPLETED', '交换完成', 'EXCHANGE', '交换成功完成', '您与${partnerName}的线索交换已完成。线索：「${leadTitle}」，获得${earnedCredits}积分。', 'partnerName,leadTitle,earnedCredits', '交换完成通知', 1, 1, 1),

-- 积分相关通知模板
('CREDITS_EARNED', '积分获得', 'CREDIT', '积分账户变动', '您获得了${credits}积分。原因：${reason}。当前余额：${balance}积分。', 'credits,reason,balance', '积分获得通知', 1, 1, 1),
('CREDITS_CONSUMED', '积分消费', 'CREDIT', '积分账户变动', '您消费了${credits}积分。原因：${reason}。当前余额：${balance}积分。', 'credits,reason,balance', '积分消费通知', 1, 1, 1),
('CREDITS_LOW', '积分余额不足', 'CREDIT', '积分余额不足提醒', '您的积分余额仅剩${balance}积分，建议发布更多优质线索来获得积分。', 'balance', '积分余额不足通知', 1, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;

-- 显示初始化完成信息
SELECT '测试数据初始化完成！' AS message;
SELECT 
    '系统配置' AS category, COUNT(*) AS count FROM system_configs
UNION ALL
SELECT 
    '地区数据' AS category, COUNT(*) AS count FROM lead_regions
UNION ALL
SELECT 
    '行业分类' AS category, COUNT(*) AS count FROM lead_categories
UNION ALL
SELECT 
    '权限数据' AS category, COUNT(*) AS count FROM permissions
UNION ALL
SELECT 
    '角色数据' AS category, COUNT(*) AS count FROM roles
UNION ALL
SELECT 
    '通知模板' AS category, COUNT(*) AS count FROM notification_templates;