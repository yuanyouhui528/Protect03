# 招商线索流通系统 - 数据库设计文档

## 1. 文档概述

### 1.1 文档信息
- **项目名称**：招商线索流通系统
- **文档版本**：v1.0
- **创建日期**：2024-01-01
- **最后更新**：2024-01-01
- **文档作者**：系统架构师
- **审核状态**：已审核

### 1.2 项目背景
招商线索流通系统是一个面向企业的B2B线索交换平台，旨在通过积分制度实现优质招商线索的有效流通，提高招商引资效率。系统采用模块化单体架构，支持高并发访问和大数据量处理。

### 1.3 设计目标
- **高性能**：支持5000并发用户，50000用户规模
- **高可用**：99.9%系统可用性
- **可扩展**：支持水平扩展和功能扩展
- **安全性**：敏感数据加密，完整的权限控制
- **一致性**：事务完整性和数据一致性保证

## 2. 技术架构

### 2.1 技术栈
- **数据库**：MySQL 8.0.x
- **缓存**：Redis 6.x
- **搜索引擎**：Elasticsearch 7.x
- **应用框架**：Spring Boot 2.7.x
- **数据访问**：Spring Data JPA + MyBatis Plus

### 2.2 数据存储策略
- **MySQL**：核心业务数据存储
- **Redis**：缓存热点数据、会话信息、分布式锁
- **Elasticsearch**：全文检索、数据分析

### 2.3 数据库设计原则
- **规范化设计**：遵循第三范式，避免数据冗余
- **性能优化**：合理设计索引，优化查询性能
- **扩展性**：预留扩展字段，支持业务发展
- **安全性**：敏感数据加密存储
- **审计性**：完整的操作日志记录

## 3. 数据库结构设计

### 3.1 模块划分
系统按照业务功能划分为6个核心模块：

1. **用户管理模块**：用户、角色、权限管理
2. **线索管理模块**：线索发布、审核、分类管理
3. **评级引擎模块**：线索评级规则和算法
4. **交换引擎模块**：线索交换、积分管理
5. **通知服务模块**：消息通知、模板管理
6. **系统配置模块**：系统参数、数据字典

### 3.2 数据库表结构

#### 3.2.1 用户管理模块（5张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| users | 用户基本信息 | id, username, password, real_name, phone, email, company_name |
| roles | 角色定义 | id, role_code, role_name, description |
| permissions | 权限定义 | id, permission_code, permission_name, permission_type |
| user_roles | 用户角色关联 | user_id, role_id |
| role_permissions | 角色权限关联 | role_id, permission_id |

**设计特点**：
- 采用RBAC权限模型，支持灵活的权限控制
- 用户密码加密存储，敏感信息AES加密
- 支持企业认证和多级权限管理

#### 3.2.2 线索管理模块（6张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| lead_categories | 行业分类 | id, category_code, category_name, parent_id |
| lead_regions | 地区信息 | id, region_code, region_name, longitude, latitude |
| leads | 线索主表 | id, title, description, company_name, investment_amount, rating |
| lead_favorites | 线索收藏 | id, user_id, lead_id |
| lead_views | 浏览记录 | id, lead_id, user_id, view_time, ip_address |
| lead_audit_logs | 审核日志 | id, lead_id, auditor_id, audit_action, audit_result |

**设计特点**：
- 支持层级化的行业分类和地区管理
- 线索信息包含地理位置，支持地理搜索
- 完整的浏览和收藏记录，支持用户行为分析
- 详细的审核流程和日志记录

#### 3.2.3 交换引擎模块（5张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| user_credits | 用户积分 | id, user_id, total_credits, available_credits, frozen_credits |
| credit_transactions | 积分交易 | id, user_id, transaction_type, amount, balance_before, balance_after |
| exchange_requests | 交换申请 | id, request_no, requester_id, target_lead_id, required_credits |
| exchange_transactions | 交换事务 | id, transaction_no, requester_id, provider_id, credits_amount |
| exchange_histories | 交换历史 | id, exchange_request_id, requester_id, provider_id, contact_info |

**设计特点**：
- 完整的积分管理体系，支持冻结和解冻
- 交换流程状态机管理，保证事务一致性
- 详细的交换历史记录，支持数据分析

#### 3.2.4 通知服务模块（4张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| notification_templates | 通知模板 | id, template_code, template_name, title, content |
| notifications | 通知记录 | id, user_id, title, content, notification_type, is_read |
| notification_settings | 通知设置 | id, user_id, notification_type, is_enabled, quiet_start_time |
| notification_send_logs | 发送日志 | id, notification_id, channel, send_status, response_time |

**设计特点**：
- 支持多渠道通知（系统、短信、邮件、微信）
- 模板化消息管理，支持变量替换
- 用户个性化通知设置，包含免打扰时间
- 完整的发送日志和性能监控

#### 3.2.5 系统配置模块（5张表）

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| system_configs | 系统配置 | id, config_key, config_value, config_type, config_group |
| rating_rules | 评级规则 | id, rule_name, rule_type, weight, calculation_method |
| exchange_rules | 交换规则 | id, rule_name, rule_type, credit_value, exchange_ratio |
| audit_logs | 操作日志 | id, user_id, operation, resource_type, request_data |
| data_dictionaries | 数据字典 | id, dict_type, dict_code, dict_name, dict_value |

**设计特点**：
- 灵活的系统配置管理，支持不同数据类型
- 可配置的评级和交换规则
- 完整的操作审计日志
- 统一的数据字典管理

### 3.3 表关系设计

#### 3.3.1 核心实体关系
```
Users (1:N) Leads           # 用户发布线索
Users (1:N) ExchangeRequests # 用户发起交换申请
Leads (1:N) ExchangeRequests # 线索被申请交换
Users (N:M) Roles           # 用户角色多对多
Roles (N:M) Permissions     # 角色权限多对多
Users (1:N) Notifications   # 用户接收通知
Leads (1:N) LeadViews       # 线索浏览记录
Users (N:M) Leads (Favorites) # 用户收藏线索
```

#### 3.3.2 外键约束
- 所有外键都设置了适当的约束和级联规则
- 重要的业务数据采用软删除，避免数据丢失
- 关联表设置了复合主键和唯一索引

## 4. 索引设计策略

### 4.1 索引分类

#### 4.1.1 主键索引
- 所有表都使用自增长的bigint类型主键
- 主键索引自动创建，无需额外配置

#### 4.1.2 唯一索引
```sql
-- 用户表唯一索引
CREATE UNIQUE INDEX uk_users_username ON users(username);
CREATE UNIQUE INDEX uk_users_phone ON users(phone);
CREATE UNIQUE INDEX uk_users_email ON users(email);

-- 角色权限唯一索引
CREATE UNIQUE INDEX uk_roles_code ON roles(role_code);
CREATE UNIQUE INDEX uk_permissions_code ON permissions(permission_code);
```

#### 4.1.3 普通索引
```sql
-- 线索表查询索引
CREATE INDEX idx_leads_category_id ON leads(category_id);
CREATE INDEX idx_leads_region_id ON leads(region_id);
CREATE INDEX idx_leads_status ON leads(status);
CREATE INDEX idx_leads_rating ON leads(rating);
CREATE INDEX idx_leads_publisher_id ON leads(publisher_id);
CREATE INDEX idx_leads_published_time ON leads(published_time);

-- 交换相关索引
CREATE INDEX idx_exchange_requests_requester_id ON exchange_requests(requester_id);
CREATE INDEX idx_exchange_requests_target_lead_id ON exchange_requests(target_lead_id);
CREATE INDEX idx_exchange_requests_status ON exchange_requests(status);
```

#### 4.1.4 复合索引
```sql
-- 线索搜索复合索引
CREATE INDEX idx_leads_search ON leads(status, rating, category_id, region_id);
CREATE INDEX idx_leads_hot ON leads(view_count, favorite_count, exchange_count);

-- 用户行为复合索引
CREATE INDEX idx_lead_views_user_time ON lead_views(user_id, view_time);
CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read, created_time);
```

#### 4.1.5 全文索引
```sql
-- 线索全文搜索索引
CREATE FULLTEXT INDEX ft_leads_search ON leads(title, description, company_name);
```

#### 4.1.6 地理空间索引
```sql
-- 地理位置索引
CREATE SPATIAL INDEX idx_leads_location ON leads(location);
CREATE SPATIAL INDEX idx_regions_location ON lead_regions(location);
```

### 4.2 索引优化策略

#### 4.2.1 查询优化
- 根据业务查询模式设计复合索引
- 避免在索引列上使用函数和表达式
- 合理使用覆盖索引减少回表查询

#### 4.2.2 写入优化
- 控制单表索引数量，避免过多索引影响写入性能
- 对于高频写入的表，考虑延迟索引更新
- 批量操作时临时禁用非关键索引

#### 4.2.3 维护策略
- 定期分析索引使用情况，删除无用索引
- 监控索引碎片率，及时进行索引重建
- 根据数据增长调整索引策略

## 5. 数据分区策略

### 5.1 分区表设计

#### 5.1.1 按时间分区
```sql
-- 线索浏览记录按月分区
ALTER TABLE lead_views PARTITION BY RANGE (YEAR(view_time) * 100 + MONTH(view_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    -- 继续添加分区...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 操作日志按月分区
ALTER TABLE audit_logs PARTITION BY RANGE (YEAR(created_time) * 100 + MONTH(created_time)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    -- 继续添加分区...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

#### 5.1.2 分区维护
- 自动创建新分区的存储过程
- 定期删除历史分区数据
- 分区统计信息更新策略

### 5.2 分库分表策略

#### 5.2.1 垂直分库
- 用户管理库：users, roles, permissions相关表
- 线索管理库：leads, categories, regions相关表
- 交换管理库：exchange, credits相关表
- 日志分析库：logs, analytics相关表

#### 5.2.2 水平分表
```sql
-- 大表分表策略
-- 线索表按用户ID分表（如果数据量巨大）
leads_0, leads_1, leads_2, ... leads_15  # 16张表

-- 分表路由算法
SELECT table_name FROM (
    SELECT CONCAT('leads_', publisher_id % 16) as table_name
) routing;
```

## 6. 缓存设计

### 6.1 Redis缓存策略

#### 6.1.1 缓存分层
- **L1缓存**：应用本地缓存（Caffeine）
- **L2缓存**：Redis分布式缓存
- **L3缓存**：数据库查询结果缓存

#### 6.1.2 缓存键设计
```redis
# 命名规范：leadex:{模块}:{业务类型}:{具体标识}
leadex:user:info:{userId}           # 用户基本信息
leadex:lead:detail:{leadId}         # 线索详情
leadex:lead:list:{category}:{page}  # 线索列表
leadex:user:credits:{userId}        # 用户积分
leadex:search:result:{queryHash}    # 搜索结果
```

#### 6.1.3 缓存更新策略
- **Cache Aside**：读取时检查缓存，未命中时查询数据库
- **Write Through**：写入时同时更新数据库和缓存
- **Write Behind**：写入时先更新缓存，异步更新数据库

#### 6.1.4 缓存过期策略
```redis
# TTL设置策略
用户会话信息：24小时
用户基本信息：30分钟
线索详情：30分钟
线索列表：5分钟
搜索结果：5分钟
系统配置：1小时
```

### 6.2 Elasticsearch设计

#### 6.2.1 索引映射
```json
{
  "mappings": {
    "properties": {
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart"
      },
      "description": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "company_name": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fields": {
          "keyword": {
            "type": "keyword"
          }
        }
      },
      "location": {
        "type": "geo_point"
      },
      "investment_amount": {
        "type": "long"
      },
      "rating": {
        "type": "keyword"
      },
      "published_time": {
        "type": "date"
      }
    }
  }
}
```

#### 6.2.2 搜索功能
- **全文搜索**：支持中文分词的标题、描述搜索
- **过滤搜索**：按行业、地区、评级、投资金额过滤
- **地理搜索**：基于地理位置的附近线索搜索
- **聚合分析**：行业分布、地区分布、投资金额分布
- **搜索建议**：自动补全和搜索建议

## 7. 安全设计

### 7.1 数据加密

#### 7.1.1 敏感数据加密
```sql
-- 手机号加密存储
phone = AES_ENCRYPT(phone_plain, 'leadex_secret')

-- 邮箱加密存储
email = AES_ENCRYPT(email_plain, 'leadex_secret')

-- 联系方式加密存储
contact_phone = AES_ENCRYPT(contact_phone_plain, 'leadex_secret')
contact_email = AES_ENCRYPT(contact_email_plain, 'leadex_secret')
```

#### 7.1.2 密码安全
```java
// 使用BCrypt加密用户密码
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(10));
```

### 7.2 权限控制

#### 7.2.1 RBAC权限模型
- **用户（User）**：系统使用者
- **角色（Role）**：权限的集合
- **权限（Permission）**：具体的操作权限
- **资源（Resource）**：被保护的系统资源

#### 7.2.2 权限粒度
- **菜单权限**：控制页面访问
- **按钮权限**：控制操作按钮显示
- **API权限**：控制接口访问
- **数据权限**：控制数据范围

### 7.3 审计日志

#### 7.3.1 操作日志记录
```sql
INSERT INTO audit_logs (
    user_id, operation, resource_type, resource_id,
    request_data, response_data, ip_address, user_agent,
    execution_time, created_time
) VALUES (
    #{userId}, #{operation}, #{resourceType}, #{resourceId},
    #{requestData}, #{responseData}, #{ipAddress}, #{userAgent},
    #{executionTime}, NOW()
);
```

#### 7.3.2 审计内容
- 用户登录登出记录
- 关键业务操作记录
- 数据修改记录
- 权限变更记录
- 异常操作记录

## 8. 性能优化

### 8.1 查询优化

#### 8.1.1 SQL优化原则
- 避免SELECT *，只查询需要的字段
- 合理使用索引，避免全表扫描
- 优化JOIN查询，控制关联表数量
- 使用LIMIT限制结果集大小
- 避免在WHERE子句中使用函数

#### 8.1.2 分页优化
```sql
-- 传统分页（性能较差）
SELECT * FROM leads ORDER BY id LIMIT 10000, 20;

-- 优化后的分页（使用游标）
SELECT * FROM leads WHERE id > 10000 ORDER BY id LIMIT 20;

-- 复合条件分页
SELECT * FROM leads 
WHERE (published_time < '2024-01-01' OR (published_time = '2024-01-01' AND id < 10000))
ORDER BY published_time DESC, id DESC 
LIMIT 20;
```

### 8.2 连接池优化

#### 8.2.1 HikariCP配置
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1800000
      connection-timeout: 30000
      validation-timeout: 5000
      leak-detection-threshold: 60000
```

### 8.3 读写分离

#### 8.3.1 主从配置
```yaml
# 主库配置
master:
  url: jdbc:mysql://master-db:3306/lead_exchange
  username: master_user
  password: master_password

# 从库配置
slave:
  url: jdbc:mysql://slave-db:3306/lead_exchange
  username: slave_user
  password: slave_password
```

#### 8.3.2 读写路由
- 写操作路由到主库
- 读操作路由到从库
- 事务内操作路由到主库
- 支持强制主库读取

## 9. 监控与维护

### 9.1 性能监控

#### 9.1.1 数据库监控指标
- **连接数监控**：当前连接数、最大连接数
- **查询性能**：慢查询、查询响应时间
- **资源使用**：CPU使用率、内存使用率、磁盘IO
- **锁等待**：锁等待时间、死锁检测

#### 9.1.2 缓存监控指标
- **命中率**：缓存命中率、未命中率
- **内存使用**：已用内存、可用内存
- **连接状态**：连接数、连接池状态
- **操作性能**：读写响应时间、吞吐量

### 9.2 备份策略

#### 9.2.1 数据备份
```bash
# 全量备份（每日）
mysqldump --single-transaction --routines --triggers \
  --master-data=2 lead_exchange > backup_$(date +%Y%m%d).sql

# 增量备份（每小时）
mysqlbinlog --start-datetime="2024-01-01 00:00:00" \
  --stop-datetime="2024-01-01 01:00:00" \
  mysql-bin.000001 > incremental_backup.sql
```

#### 9.2.2 备份验证
- 定期恢复测试
- 备份文件完整性检查
- 恢复时间测试

### 9.3 维护计划

#### 9.3.1 日常维护
- 监控系统状态
- 检查慢查询日志
- 清理过期数据
- 更新统计信息

#### 9.3.2 定期维护
- 索引重建和优化
- 表空间整理
- 分区维护
- 性能调优

## 10. 部署方案

### 10.1 环境规划

#### 10.1.1 开发环境
- **数据库**：单机MySQL实例
- **缓存**：单机Redis实例
- **搜索**：单节点Elasticsearch

#### 10.1.2 测试环境
- **数据库**：主从MySQL集群
- **缓存**：Redis哨兵模式
- **搜索**：3节点Elasticsearch集群

#### 10.1.3 生产环境
- **数据库**：MySQL主从集群 + 读写分离
- **缓存**：Redis集群模式
- **搜索**：多节点Elasticsearch集群

### 10.2 容器化部署

#### 10.2.1 Docker配置
```dockerfile
# MySQL容器
FROM mysql:8.0
COPY my.cnf /etc/mysql/conf.d/
COPY init.sql /docker-entrypoint-initdb.d/

# Redis容器
FROM redis:6-alpine
COPY redis.conf /usr/local/etc/redis/redis.conf
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
```

#### 10.2.2 Kubernetes部署
```yaml
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: mysql-master
spec:
  serviceName: mysql-master
  replicas: 1
  selector:
    matchLabels:
      app: mysql-master
  template:
    metadata:
      labels:
        app: mysql-master
    spec:
      containers:
      - name: mysql
        image: mysql:8.0
        env:
        - name: MYSQL_ROOT_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: root-password
```

## 11. 总结

### 11.1 设计亮点
1. **模块化设计**：清晰的模块划分，便于维护和扩展
2. **性能优化**：完善的索引策略和缓存设计
3. **安全保障**：敏感数据加密和完整的权限控制
4. **高可用性**：主从复制和集群部署
5. **可扩展性**：分库分表和水平扩展支持

### 11.2 技术特色
1. **多存储引擎**：MySQL + Redis + Elasticsearch组合
2. **智能缓存**：多级缓存和智能失效策略
3. **全文搜索**：中文分词和地理位置搜索
4. **实时监控**：完善的监控和告警体系
5. **自动化运维**：容器化部署和自动化维护

### 11.3 后续优化方向
1. **数据分析**：增加更多的数据分析和报表功能
2. **机器学习**：引入推荐算法和智能匹配
3. **微服务化**：根据业务发展考虑微服务拆分
4. **国际化**：支持多语言和多地区部署
5. **移动端**：优化移动端数据访问性能

---

**文档版本历史**

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| v1.0 | 2024-01-01 | 初始版本 | 系统架构师 |

**审核记录**

| 审核人 | 审核日期 | 审核结果 | 备注 |
|--------|----------|----------|------|
| 技术总监 | 2024-01-01 | 通过 | 设计方案合理，可以实施 |
| DBA | 2024-01-01 | 通过 | 数据库设计规范，性能考虑充分 |