-- =============================================
-- 招商线索流通系统 - 用户和线索测试数据脚本
-- 版本: 1.0
-- 创建时间: 2024-01-01
-- 说明: 用于开发和测试环境的用户和线索数据
-- =============================================

USE lead_exchange;

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 1. 测试用户数据
-- =============================================

-- 插入测试用户（密码统一为：123456，已加密）
INSERT INTO users (username, password, real_name, phone, email, company_name, company_address, business_license, industry, region_id, status, verified, avatar, last_login_time, login_count, created_by, updated_by) VALUES
-- 超级管理员
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '系统管理员', '13800138000', 'admin@leadexchange.com', '招商线索流通平台', '北京市朝阳区科技园区', '91110000000000000X', '信息技术', 1, 'ACTIVE', 1, '/static/avatars/admin.png', NOW(), 1, 1, 1),

-- 线索管理员
('lead_manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '线索管理员', '13800138001', 'manager@leadexchange.com', '招商线索流通平台', '北京市朝阳区科技园区', '91110000000000001X', '信息技术', 1, 'ACTIVE', 1, '/static/avatars/manager.png', NOW(), 1, 1, 1),

-- 企业用户
('tech_company', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '张科技', '13800138002', 'zhang@techcompany.com', '北京科技创新有限公司', '北京市海淀区中关村大街1号', '91110108000000002X', '信息技术', 1, 'ACTIVE', 1, '/static/avatars/user1.png', NOW(), 5, 1, 1),

('manufacturing_corp', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '李制造', '13800138003', 'li@manufacturing.com', '广东制造业集团', '广东省广州市天河区珠江新城', '91440100000000003X', '制造业', 5, 'ACTIVE', 1, '/static/avatars/user2.png', NOW(), 3, 1, 1),

('finance_group', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '王金融', '13800138004', 'wang@financegroup.com', '上海金融投资集团', '上海市浦东新区陆家嘴金融区', '91310115000000004X', '金融服务', 3, 'ACTIVE', 1, '/static/avatars/user3.png', NOW(), 8, 1, 1),

('realestate_dev', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '刘地产', '13800138005', 'liu@realestate.com', '深圳房地产开发公司', '广东省深圳市南山区科技园', '91440300000000005X', '房地产', 6, 'ACTIVE', 1, '/static/avatars/user4.png', NOW(), 2, 1, 1),

('energy_tech', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '陈新能', '13800138006', 'chen@energytech.com', '江苏新能源科技有限公司', '江苏省苏州市工业园区', '91320500000000006X', '新能源', 10, 'ACTIVE', 1, '/static/avatars/user5.png', NOW(), 4, 1, 1),

('biomedical_inc', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '赵生物', '13800138007', 'zhao@biomedical.com', '浙江生物医药股份有限公司', '浙江省杭州市滨江区医药港', '91330100000000007X', '生物医药', 12, 'ACTIVE', 1, '/static/avatars/user6.png', NOW(), 6, 1, 1),

('service_company', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '孙服务', '13800138008', 'sun@service.com', '现代服务业发展公司', '北京市朝阳区CBD商务区', '91110105000000008X', '现代服务业', 1, 'ACTIVE', 1, '/static/avatars/user7.png', NOW(), 1, 1, 1),

('culture_creative', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '周文创', '13800138009', 'zhou@culture.com', '文化创意产业园', '上海市徐汇区文化创意园区', '91310104000000009X', '文化创意', 3, 'ACTIVE', 1, '/static/avatars/user8.png', NOW(), 7, 1, 1),

('startup_company', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8imdJMReUHlHb6.Zt.WBMADE4/Z7e', '吴创业', '13800138010', 'wu@startup.com', '创业科技有限公司', '深圳市南山区创业园', '91440300000000010X', '信息技术', 6, 'ACTIVE', 1, '/static/avatars/user9.png', NOW(), 2, 1, 1);

-- 用户角色关联
INSERT INTO user_roles (user_id, role_id, created_by) VALUES
-- 超级管理员
(1, 1, 1),
-- 线索管理员
(2, 3, 1),
-- 企业用户
(3, 4, 1),
(4, 4, 1),
(5, 4, 1),
(6, 4, 1),
(7, 4, 1),
(8, 4, 1),
(9, 4, 1),
(10, 4, 1),
(11, 4, 1);

-- 用户积分初始化
INSERT INTO user_credits (user_id, total_credits, available_credits, frozen_credits, consumed_credits, earned_credits, created_by, updated_by) VALUES
(1, 1000, 1000, 0, 0, 1000, 1, 1),  -- 管理员
(2, 500, 500, 0, 0, 500, 1, 1),    -- 线索管理员
(3, 100, 80, 0, 20, 100, 1, 1),    -- 科技公司
(4, 150, 120, 10, 30, 150, 1, 1),  -- 制造业
(5, 200, 180, 0, 20, 200, 1, 1),   -- 金融集团
(6, 80, 60, 0, 20, 80, 1, 1),      -- 房地产
(7, 120, 100, 0, 20, 120, 1, 1),   -- 新能源
(8, 160, 140, 0, 20, 160, 1, 1),   -- 生物医药
(9, 60, 60, 0, 0, 60, 1, 1),       -- 服务业
(10, 90, 70, 0, 20, 90, 1, 1),     -- 文创
(11, 40, 40, 0, 0, 40, 1, 1);      -- 创业公司

-- =============================================
-- 2. 测试线索数据
-- =============================================

-- 获取分类和地区ID
SET @it_category_id = (SELECT id FROM lead_categories WHERE category_code = '02');
SET @manufacturing_category_id = (SELECT id FROM lead_categories WHERE category_code = '01');
SET @finance_category_id = (SELECT id FROM lead_categories WHERE category_code = '03');
SET @realestate_category_id = (SELECT id FROM lead_categories WHERE category_code = '04');
SET @newenergy_category_id = (SELECT id FROM lead_categories WHERE category_code = '05');
SET @biomedical_category_id = (SELECT id FROM lead_categories WHERE category_code = '06');

SET @beijing_region_id = (SELECT id FROM lead_regions WHERE region_code = 110000);
SET @guangdong_region_id = (SELECT id FROM lead_regions WHERE region_code = 440000);
SET @shanghai_region_id = (SELECT id FROM lead_regions WHERE region_code = 310000);
SET @jiangsu_region_id = (SELECT id FROM lead_regions WHERE region_code = 320000);
SET @zhejiang_region_id = (SELECT id FROM lead_regions WHERE region_code = 330000);

-- 插入测试线索数据
INSERT INTO leads (title, description, company_name, contact_person, contact_phone, contact_email, investment_amount, category_id, region_id, address, longitude, latitude, rating, status, view_count, favorite_count, exchange_count, metadata, publisher_id, published_time, created_by, updated_by) VALUES

-- A级线索（高质量）
('人工智能医疗诊断系统项目', 
 '基于深度学习的医疗影像诊断系统，已完成核心算法开发，拥有多项发明专利。项目团队由清华大学博士团队组成，已获得天使轮融资500万元，现寻求A轮融资3000万元用于产品商业化和市场推广。目标客户为三甲医院，预计3年内实现营收突破1亿元。', 
 '北京智慧医疗科技有限公司', '张博士', 'AES_ENCRYPT("13901234567", "leadex_secret")', 'AES_ENCRYPT("zhang.dr@aimedical.com", "leadex_secret")', 
 30000000, @it_category_id, @beijing_region_id, '北京市海淀区中关村软件园', 116.3008, 39.9775, 'A', 'PUBLISHED', 156, 23, 5, 
 '{"patents": 8, "team_size": 15, "funding_stage": "A轮", "revenue_projection": "1亿元", "target_market": "医疗机构"}', 
 3, NOW() - INTERVAL 5 DAY, 3, 3),

('新能源汽车电池回收项目', 
 '专注于新能源汽车动力电池回收利用的环保科技项目。拥有完整的电池拆解、材料提取、再生利用技术链条，已建成日处理100吨的示范生产线。与比亚迪、宁德时代等头部企业建立合作关系，现寻求战略投资5000万元扩大产能。', 
 '江苏绿色循环科技股份有限公司', '李总工', 'AES_ENCRYPT("13802345678", "leadex_secret")', 'AES_ENCRYPT("li.engineer@greencycle.com", "leadex_secret")', 
 50000000, @newenergy_category_id, @jiangsu_region_id, '江苏省苏州市工业园区环保产业园', 120.6197, 31.2989, 'A', 'PUBLISHED', 198, 31, 8, 
 '{"processing_capacity": "100吨/日", "partnerships": ["比亚迪", "宁德时代"], "technology_level": "国际先进", "market_share": "15%"}', 
 7, NOW() - INTERVAL 3 DAY, 7, 7),

('生物制药创新药物研发', 
 '针对阿尔茨海默病的创新药物研发项目，已完成临床前研究，即将进入临床I期试验。核心技术团队来自中科院上海药物所，拥有国际领先的药物设计平台。现寻求B轮融资8000万元用于临床试验和新药申报。', 
 '上海创新生物医药有限公司', '王研究员', 'AES_ENCRYPT("13903456789", "leadex_secret")', 'AES_ENCRYPT("wang.researcher@innovbio.com", "leadex_secret")', 
 80000000, @biomedical_category_id, @shanghai_region_id, '上海市浦东新区张江生物医药基地', 121.6024, 31.2066, 'A', 'PUBLISHED', 142, 28, 6, 
 '{"clinical_stage": "I期准备", "patent_count": 12, "team_background": "中科院", "market_potential": "100亿美元"}', 
 8, NOW() - INTERVAL 7 DAY, 8, 8),

-- B级线索（中等质量）
('智能制造产业园建设项目', 
 '规划建设占地500亩的智能制造产业园，重点引进高端装备制造、工业机器人、智能传感器等企业。园区配套完善，享受当地政府多项优惠政策。现寻求产业投资基金合作，总投资规模15亿元。', 
 '广东智造产业发展有限公司', '刘园长', 'AES_ENCRYPT("13804567890", "leadex_secret")', 'AES_ENCRYPT("liu.director@smartpark.com", "leadex_secret")', 
 150000000, @manufacturing_category_id, @guangdong_region_id, '广东省佛山市南海区智能制造园区', 113.1220, 23.0288, 'B', 'PUBLISHED', 89, 15, 3, 
 '{"land_area": "500亩", "investment_scale": "15亿元", "policy_support": "税收减免", "target_industries": ["高端装备", "工业机器人"]}', 
 4, NOW() - INTERVAL 2 DAY, 4, 4),

('金融科技支付平台项目', 
 '面向中小企业的一站式金融科技服务平台，提供支付、信贷、理财等综合金融服务。已获得支付牌照，注册用户超过10万，月交易额突破5000万元。现寻求C轮融资2亿元用于全国市场拓展。', 
 '深圳金融科技创新公司', '陈CEO', 'AES_ENCRYPT("13905678901", "leadex_secret")', 'AES_ENCRYPT("chen.ceo@fintech.com", "leadex_secret")', 
 20000000, @finance_category_id, @guangdong_region_id, '深圳市福田区金融中心区', 114.0579, 22.5431, 'B', 'PUBLISHED', 76, 12, 2, 
 '{"user_count": "10万+", "monthly_volume": "5000万元", "license": "支付牌照", "expansion_plan": "全国市场"}', 
 6, NOW() - INTERVAL 4 DAY, 6, 6),

('文化创意产业综合体', 
 '集影视制作、动漫设计、数字媒体、文化展示于一体的文创产业综合体项目。位于城市核心区域，总建筑面积8万平方米，预计引入100家文创企业。现寻求文化产业投资基金合作。', 
 '杭州文创产业投资有限公司', '周总监', 'AES_ENCRYPT("13906789012", "leadex_secret")', 'AES_ENCRYPT("zhou.director@culture.com", "leadex_secret")', 
 60000000, (SELECT id FROM lead_categories WHERE category_code = '08'), @zhejiang_region_id, '浙江省杭州市西湖区文创园区', 120.1536, 30.2650, 'B', 'PUBLISHED', 63, 9, 1, 
 '{"building_area": "8万平方米", "target_companies": 100, "location": "城市核心区", "industry_focus": ["影视", "动漫", "数字媒体"]}', 
 10, NOW() - INTERVAL 6 DAY, 10, 10),

-- C级线索（一般质量）
('传统制造业数字化改造', 
 '传统纺织企业数字化转型项目，通过引入工业互联网、智能设备等技术手段，提升生产效率和产品质量。项目总投资3000万元，预计改造周期18个月，寻求技术合作伙伴。', 
 '江苏传统纺织有限公司', '马厂长', 'AES_ENCRYPT("13907890123", "leadex_secret")', 'AES_ENCRYPT("ma.manager@textile.com", "leadex_secret")', 
 30000000, @manufacturing_category_id, @jiangsu_region_id, '江苏省无锡市纺织工业园', 120.3019, 31.5747, 'C', 'PUBLISHED', 45, 6, 1, 
 '{"transformation_period": "18个月", "efficiency_improvement": "30%", "technology_needs": ["工业互联网", "智能设备"]}', 
 4, NOW() - INTERVAL 1 DAY, 4, 4),

('社区商业综合体项目', 
 '位于新兴住宅区的社区商业综合体，规划商业面积3万平方米，包含超市、餐饮、娱乐、教育等业态。周边已入住居民2万户，消费潜力较大。现寻求商业地产投资合作。', 
 '本地房地产开发公司', '钱经理', 'AES_ENCRYPT("13908901234", "leadex_secret")', 'AES_ENCRYPT("qian.manager@localdev.com", "leadex_secret")', 
 80000000, @realestate_category_id, @guangdong_region_id, '广东省东莞市新城区', 113.7518, 23.0489, 'C', 'PUBLISHED', 38, 4, 0, 
 '{"commercial_area": "3万平方米", "resident_households": "2万户", "business_types": ["超市", "餐饮", "娱乐", "教育"]}', 
 6, NOW() - INTERVAL 8 DAY, 6, 6),

-- D级线索（基础质量）
('农业科技种植项目', 
 '现代农业科技种植项目，采用无土栽培、智能温控等技术种植有机蔬菜。项目占地50亩，年产值预计500万元。寻求农业产业基金投资合作，投资回收期3-5年。', 
 '绿色农业科技合作社', '田社长', 'AES_ENCRYPT("13909012345", "leadex_secret")', 'AES_ENCRYPT("tian.president@greenagri.com", "leadex_secret")', 
 8000000, (SELECT id FROM lead_categories WHERE category_code = '07'), @zhejiang_region_id, '浙江省温州市农业科技园', 120.6994, 27.9944, 'D', 'PUBLISHED', 28, 2, 0, 
 '{"land_area": "50亩", "annual_output": "500万元", "payback_period": "3-5年", "technology": ["无土栽培", "智能温控"]}', 
 11, NOW() - INTERVAL 10 DAY, 11, 11),

('本地餐饮连锁扩张项目', 
 '本地知名餐饮品牌连锁扩张项目，现有门店15家，年营收2000万元。计划3年内扩张至50家门店，覆盖周边城市。寻求餐饮行业投资者合作，投资金额1000万元。', 
 '美食连锁餐饮有限公司', '孙老板', 'AES_ENCRYPT("13900123456", "leadex_secret")', 'AES_ENCRYPT("sun.boss@foodchain.com", "leadex_secret")', 
 10000000, (SELECT id FROM lead_categories WHERE category_code = '07'), @guangdong_region_id, '广东省广州市天河区美食街', 113.2644, 23.1291, 'D', 'PUBLISHED', 22, 1, 0, 
 '{"current_stores": 15, "annual_revenue": "2000万元", "expansion_target": "50家门店", "timeline": "3年"}', 
 9, NOW() - INTERVAL 12 DAY, 9, 9);

-- =============================================
-- 3. 线索相关数据
-- =============================================

-- 线索收藏记录
INSERT INTO lead_favorites (user_id, lead_id, created_by) VALUES
(3, 1, 3),  -- 科技公司收藏AI医疗项目
(3, 2, 3),  -- 科技公司收藏电池回收项目
(4, 1, 4),  -- 制造业收藏AI医疗项目
(4, 4, 4),  -- 制造业收藏智能制造园区
(5, 3, 5),  -- 金融集团收藏生物制药项目
(5, 5, 5),  -- 金融集团收藏金融科技项目
(6, 8, 6),  -- 房地产收藏社区综合体
(7, 2, 7),  -- 新能源收藏电池回收项目
(8, 3, 8),  -- 生物医药收藏创新药物项目
(10, 6, 10); -- 文创收藏文创综合体

-- 线索浏览记录
INSERT INTO lead_views (lead_id, user_id, view_time, ip_address, user_agent, created_by) VALUES
(1, 3, NOW() - INTERVAL 1 HOUR, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 3),
(1, 4, NOW() - INTERVAL 2 HOUR, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 4),
(1, 5, NOW() - INTERVAL 3 HOUR, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 5),
(2, 3, NOW() - INTERVAL 4 HOUR, '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 3),
(2, 7, NOW() - INTERVAL 5 HOUR, '192.168.1.106', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 7),
(3, 5, NOW() - INTERVAL 6 HOUR, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 5),
(3, 8, NOW() - INTERVAL 7 HOUR, '192.168.1.107', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 8),
(4, 4, NOW() - INTERVAL 8 HOUR, '192.168.1.101', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 4),
(5, 5, NOW() - INTERVAL 9 HOUR, '192.168.1.102', 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36', 5),
(6, 10, NOW() - INTERVAL 10 HOUR, '192.168.1.109', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36', 10);

-- 线索审核日志
INSERT INTO lead_audit_logs (lead_id, auditor_id, audit_action, audit_result, audit_comment, audit_time, created_by) VALUES
(1, 2, 'REVIEW', 'APPROVED', '项目技术先进，团队实力强，市场前景广阔，评定为A级线索', NOW() - INTERVAL 5 DAY, 2),
(2, 2, 'REVIEW', 'APPROVED', '环保项目符合国家政策导向，技术成熟，合作伙伴优质，评定为A级线索', NOW() - INTERVAL 3 DAY, 2),
(3, 2, 'REVIEW', 'APPROVED', '创新药物研发项目，临床前数据良好，团队专业，评定为A级线索', NOW() - INTERVAL 7 DAY, 2),
(4, 2, 'REVIEW', 'APPROVED', '产业园区项目规模较大，政策支持力度强，评定为B级线索', NOW() - INTERVAL 2 DAY, 2),
(5, 2, 'REVIEW', 'APPROVED', '金融科技项目用户基础良好，商业模式清晰，评定为B级线索', NOW() - INTERVAL 4 DAY, 2),
(6, 2, 'REVIEW', 'APPROVED', '文创项目位置优越，产业聚集效应明显，评定为B级线索', NOW() - INTERVAL 6 DAY, 2),
(7, 2, 'REVIEW', 'APPROVED', '传统制造业转型项目，技术需求明确，评定为C级线索', NOW() - INTERVAL 1 DAY, 2),
(8, 2, 'REVIEW', 'APPROVED', '社区商业项目位置尚可，消费群体稳定，评定为C级线索', NOW() - INTERVAL 8 DAY, 2),
(9, 2, 'REVIEW', 'APPROVED', '农业科技项目规模较小，技术应用有限，评定为D级线索', NOW() - INTERVAL 10 DAY, 2),
(10, 2, 'REVIEW', 'APPROVED', '餐饮连锁项目本地化特色明显，扩张计划合理，评定为D级线索', NOW() - INTERVAL 12 DAY, 2);

-- =============================================
-- 4. 交换相关测试数据
-- =============================================

-- 交换申请记录
INSERT INTO exchange_requests (request_no, requester_id, target_lead_id, offered_leads, required_credits, offered_credits, message, status, expire_time, created_by, updated_by) VALUES
('EXR202401001', 3, 2, '[{"leadId": 1, "credits": 8}]', 8, 8, '我们对新能源电池回收项目非常感兴趣，希望能够交换联系方式进行深入合作洽谈。', 'APPROVED', NOW() + INTERVAL 7 DAY, 3, 3),
('EXR202401002', 5, 1, '[{"leadId": 3, "credits": 8}]', 8, 8, '我们金融集团对AI医疗诊断系统项目有投资意向，希望获取详细联系方式。', 'COMPLETED', NOW() + INTERVAL 7 DAY, 5, 5),
('EXR202401003', 4, 4, '[{"leadId": 7, "credits": 2}]', 4, 2, '我们制造业集团希望了解智能制造产业园的入驻政策和优惠条件。', 'PENDING', NOW() + INTERVAL 7 DAY, 4, 4),
('EXR202401004', 8, 3, '[{"leadId": 1, "credits": 8}]', 8, 8, '作为生物医药企业，我们对创新药物研发项目非常关注，希望探讨合作可能。', 'APPROVED', NOW() + INTERVAL 7 DAY, 8, 8),
('EXR202401005', 6, 5, '[{"leadId": 8, "credits": 2}]', 4, 2, '我们房地产公司对金融科技支付平台项目感兴趣，希望了解更多信息。', 'REJECTED', NOW() + INTERVAL 7 DAY, 6, 6);

-- 交换事务记录
INSERT INTO exchange_transactions (transaction_no, exchange_request_id, requester_id, provider_id, lead_id, credits_amount, transaction_type, status, completed_time, created_by, updated_by) VALUES
('TXN202401001', 1, 3, 7, 2, 8, 'EXCHANGE', 'COMPLETED', NOW() - INTERVAL 1 DAY, 3, 3),
('TXN202401002', 2, 5, 3, 1, 8, 'EXCHANGE', 'COMPLETED', NOW() - INTERVAL 2 DAY, 5, 5),
('TXN202401003', 4, 8, 3, 1, 8, 'EXCHANGE', 'COMPLETED', NOW() - INTERVAL 3 DAY, 8, 8);

-- 交换历史记录
INSERT INTO exchange_histories (exchange_request_id, requester_id, provider_id, lead_id, credits_amount, exchange_time, contact_info, notes, created_by, updated_by) VALUES
(1, 3, 7, 2, 8, NOW() - INTERVAL 1 DAY, '{"phone": "13800138006", "email": "chen@energytech.com", "contact_person": "陈新能"}', '交换成功，双方已建立联系', 3, 3),
(2, 5, 3, 1, 8, NOW() - INTERVAL 2 DAY, '{"phone": "13800138002", "email": "zhang@techcompany.com", "contact_person": "张科技"}', '交换成功，金融集团已联系项目方', 5, 5),
(4, 8, 3, 1, 8, NOW() - INTERVAL 3 DAY, '{"phone": "13800138002", "email": "zhang@techcompany.com", "contact_person": "张科技"}', '交换成功，生物医药公司已获取联系方式', 8, 8);

-- 积分交易记录
INSERT INTO credit_transactions (user_id, transaction_type, amount, balance_before, balance_after, related_id, description, created_by, updated_by) VALUES
-- 用户注册奖励
(3, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(4, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(5, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(6, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(7, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(8, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(9, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(10, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),
(11, 'REWARD', 20, 0, 20, NULL, '新用户注册奖励', 1, 1),

-- 发布线索奖励
(3, 'REWARD', 8, 20, 28, 1, '发布A级线索奖励', 1, 1),
(7, 'REWARD', 8, 20, 28, 2, '发布A级线索奖励', 1, 1),
(8, 'REWARD', 8, 20, 28, 3, '发布A级线索奖励', 1, 1),
(4, 'REWARD', 4, 20, 24, 4, '发布B级线索奖励', 1, 1),
(6, 'REWARD', 4, 20, 24, 5, '发布B级线索奖励', 1, 1),
(10, 'REWARD', 4, 20, 24, 6, '发布B级线索奖励', 1, 1),
(4, 'REWARD', 2, 24, 26, 7, '发布C级线索奖励', 1, 1),
(6, 'REWARD', 2, 24, 26, 8, '发布C级线索奖励', 1, 1),
(11, 'REWARD', 1, 20, 21, 9, '发布D级线索奖励', 1, 1),
(9, 'REWARD', 1, 20, 21, 10, '发布D级线索奖励', 1, 1),

-- 交换消费
(3, 'EXCHANGE', -8, 100, 92, 1, '交换线索消费积分', 3, 3),
(5, 'EXCHANGE', -8, 200, 192, 2, '交换线索消费积分', 5, 5),
(8, 'EXCHANGE', -8, 160, 152, 4, '交换线索消费积分', 8, 8),

-- 交换收入
(7, 'EXCHANGE', 8, 120, 128, 1, '线索被交换获得积分', 7, 7),
(3, 'EXCHANGE', 8, 92, 100, 2, '线索被交换获得积分', 3, 3),
(3, 'EXCHANGE', 8, 100, 108, 4, '线索被交换获得积分', 3, 3);

-- =============================================
-- 5. 通知相关测试数据
-- =============================================

-- 用户通知设置
INSERT INTO notification_settings (user_id, notification_type, is_enabled, quiet_start_time, quiet_end_time, created_by, updated_by) VALUES
(3, 'SYSTEM', 1, '22:00:00', '08:00:00', 3, 3),
(3, 'SMS', 1, '22:00:00', '08:00:00', 3, 3),
(3, 'EMAIL', 1, NULL, NULL, 3, 3),
(3, 'WECHAT', 0, NULL, NULL, 3, 3),
(4, 'SYSTEM', 1, '23:00:00', '07:00:00', 4, 4),
(4, 'SMS', 0, NULL, NULL, 4, 4),
(4, 'EMAIL', 1, NULL, NULL, 4, 4),
(4, 'WECHAT', 1, '23:00:00', '07:00:00', 4, 4),
(5, 'SYSTEM', 1, '22:30:00', '07:30:00', 5, 5),
(5, 'SMS', 1, '22:30:00', '07:30:00', 5, 5),
(5, 'EMAIL', 1, NULL, NULL, 5, 5),
(5, 'WECHAT', 1, '22:30:00', '07:30:00', 5, 5);

-- 通知记录
INSERT INTO notifications (user_id, title, content, notification_type, template_code, is_read, read_time, created_by, updated_by) VALUES
(3, '欢迎加入招商线索流通平台', '尊敬的张科技，欢迎您注册招商线索流通平台！您已获得20积分奖励，可以开始浏览和交换线索了。', 'SYSTEM', 'WELCOME', 1, NOW() - INTERVAL 5 DAY, 1, 1),
(3, '线索发布成功', '您发布的线索「人工智能医疗诊断系统项目」已成功发布，评级为A级，价值8积分。', 'LEAD', 'LEAD_PUBLISHED', 1, NOW() - INTERVAL 4 DAY, 3, 3),
(3, '收到新的交换申请', '王金融申请交换您的线索「人工智能医疗诊断系统项目」，提供8积分。请及时处理。', 'EXCHANGE', 'EXCHANGE_REQUEST', 1, NOW() - INTERVAL 2 DAY, 5, 5),
(3, '交换申请已通过', '您申请交换的线索「新能源汽车电池回收项目」已被同意，消耗8积分。联系方式已发送至您的邮箱。', 'EXCHANGE', 'EXCHANGE_APPROVED', 1, NOW() - INTERVAL 1 DAY, 3, 3),
(5, '欢迎加入招商线索流通平台', '尊敬的王金融，欢迎您注册招商线索流通平台！您已获得20积分奖励，可以开始浏览和交换线索了。', 'SYSTEM', 'WELCOME', 1, NOW() - INTERVAL 6 DAY, 1, 1),
(5, '交换申请已通过', '您申请交换的线索「人工智能医疗诊断系统项目」已被同意，消耗8积分。联系方式已发送至您的邮箱。', 'EXCHANGE', 'EXCHANGE_APPROVED', 1, NOW() - INTERVAL 2 DAY, 5, 5),
(7, '欢迎加入招商线索流通平台', '尊敬的陈新能，欢迎您注册招商线索流通平台！您已获得20积分奖励，可以开始浏览和交换线索了。', 'SYSTEM', 'WELCOME', 1, NOW() - INTERVAL 7 DAY, 1, 1),
(7, '线索发布成功', '您发布的线索「新能源汽车电池回收项目」已成功发布，评级为A级，价值8积分。', 'LEAD', 'LEAD_PUBLISHED', 1, NOW() - INTERVAL 3 DAY, 7, 7),
(7, '收到新的交换申请', '张科技申请交换您的线索「新能源汽车电池回收项目」，提供8积分。请及时处理。', 'EXCHANGE', 'EXCHANGE_REQUEST', 1, NOW() - INTERVAL 1 DAY, 3, 3),
(8, '欢迎加入招商线索流通平台', '尊敬的赵生物，欢迎您注册招商线索流通平台！您已获得20积分奖励，可以开始浏览和交换线索了。', 'SYSTEM', 'WELCOME', 0, NULL, 1, 1);

SET FOREIGN_KEY_CHECKS = 1;

-- 显示测试数据统计
SELECT '用户和线索测试数据初始化完成！' AS message;
SELECT 
    '用户数据' AS category, COUNT(*) AS count FROM users WHERE id > 1
UNION ALL
SELECT 
    '线索数据' AS category, COUNT(*) AS count FROM leads
UNION ALL
SELECT 
    '收藏记录' AS category, COUNT(*) AS count FROM lead_favorites
UNION ALL
SELECT 
    '浏览记录' AS category, COUNT(*) AS count FROM lead_views
UNION ALL
SELECT 
    '交换申请' AS category, COUNT(*) AS count FROM exchange_requests
UNION ALL
SELECT 
    '积分交易' AS category, COUNT(*) AS count FROM credit_transactions
UNION ALL
SELECT 
    '通知记录' AS category, COUNT(*) AS count FROM notifications;