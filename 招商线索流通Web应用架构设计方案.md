# 招商线索流通Web应用架构设计方案

## 1. 架构概述

### 1.1 架构目标

基于需求文档分析，本架构设计旨在构建一个高可用、高性能、安全可靠的招商线索流通平台，支持以下核心目标：

- **业务目标**：实现线索发布、评级、交换全流程线上化，提高流转效率
- **技术目标**：支持5000并发用户，50000用户规模，99.9%可用性
- **质量目标**：确保数据安全、系统稳定、用户体验优良

### 1.2 架构原则

| 原则 | 说明 | 应用场景 |
|------|------|----------|
| **模块化设计** | 按业务领域划分模块，降低耦合度 | 6个核心业务模块独立设计 |
| **分层架构** | 清晰的分层结构，职责分离 | 表现层、应用层、数据层分离 |
| **事件驱动** | 通过事件实现模块间异步通信 | 线索状态变更、通知推送 |
| **安全优先** | 数据加密、权限控制、审计日志 | 敏感信息保护、操作追踪 |
| **可扩展性** | 支持水平扩展和功能扩展 | 容器化部署、模块化设计 |

### 1.3 架构风格选择

**选择：模块化单体架构**

#### 架构决策记录

- **上下文**：中等规模系统（50000用户），复杂业务逻辑，团队规模适中
- **方案对比**：
  - 微服务架构：优势是独立部署、技术栈灵活；劣势是复杂度高、运维成本大
  - 单体架构：优势是简单易维护、事务一致性好；劣势是扩展性受限
  - 模块化单体：平衡了两者优劣，适合当前规模
- **决策理由**：在保证代码组织清晰的同时，降低部署运维复杂度，便于团队开发维护
- **影响**：简化了分布式事务处理，提高了开发效率
- **约束**：需要在代码层面严格控制模块边界，避免耦合

## 2. 系统架构设计

### 2.1 整体架构图

```
┌─────────────────────────────────────────────────────────────┐
│                        用户层                                │
├─────────────────────────────────────────────────────────────┤
│  PC端浏览器          │         移动端浏览器                  │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      CDN + 负载均衡                          │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       API网关层                              │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   路由管理   │ │   认证授权   │ │   限流熔断   │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      应用服务层                              │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│ │用户管理模块│ │线索管理模块│ │评级引擎  │ │交换引擎  │       │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
│ ┌──────────┐ ┌──────────┐                                 │
│ │数据分析模块│ │通知服务  │                                 │
│ └──────────┘ └──────────┘                                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                       数据服务层                             │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│ │MySQL集群 │ │Redis集群 │ │Elasticsearch│ │消息队列  │       │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      基础设施层                              │
│ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐       │
│ │容器编排  │ │监控告警  │ │日志分析  │ │文件存储  │       │
│ └──────────┘ └──────────┘ └──────────┘ └──────────┘       │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 技术栈选择

#### 前端技术栈

| 技术 | 版本 | 用途 | 选择理由 |
|------|------|------|----------|
| **Vue.js** | 3.x | 前端框架 | 生态成熟、学习成本低、响应式支持好 |
| **Element Plus** | 最新版 | UI组件库 | 组件丰富、设计规范、企业级应用 |
| **Vue Router** | 4.x | 路由管理 | 官方路由解决方案 |
| **Pinia** | 最新版 | 状态管理 | Vue3官方推荐状态管理库 |
| **Axios** | 最新版 | HTTP客户端 | 请求拦截、响应处理便捷 |
| **ECharts** | 5.x | 数据可视化 | 图表功能强大、性能优秀 |

#### 后端技术栈

| 技术 | 版本 | 用途 | 选择理由 |
|------|------|------|----------|
| **Spring Boot** | 2.7.x | 应用框架 | 生态完善、开发效率高、企业级应用首选 |
| **Spring Security** | 5.7.x | 安全框架 | 认证授权功能完善、与Spring集成好 |
| **Spring Data JPA** | 2.7.x | 数据访问 | ORM映射便捷、查询功能强大 |
| **MyBatis Plus** | 3.5.x | 数据访问 | 复杂查询支持、代码生成便捷 |
| **Redis** | 6.x | 缓存/会话 | 性能优秀、数据结构丰富 |
| **RabbitMQ** | 3.9.x | 消息队列 | 可靠性高、功能完善 |
| **Elasticsearch** | 7.x | 搜索引擎 | 全文检索、聚合分析能力强 |

#### 数据库技术栈

| 技术 | 版本 | 用途 | 选择理由 |
|------|------|------|----------|
| **MySQL** | 8.0.x | 主数据库 | 事务支持好、生态成熟、运维经验丰富 |
| **Redis** | 6.x | 缓存数据库 | 高性能、支持多种数据结构 |
| **Elasticsearch** | 7.x | 搜索数据库 | 全文检索、实时分析能力强 |

### 2.3 分层架构设计

#### 表现层（Presentation Layer）

**职责**：用户界面展示、用户交互处理、前端路由管理

**组件设计**：
- **页面组件**：线索管理页面、交换中心页面、数据看板页面等
- **业务组件**：线索卡片、评级显示、交换申请等
- **通用组件**：表格、表单、图表、通知等
- **布局组件**：头部导航、侧边栏、面包屑等

#### 网关层（Gateway Layer）

**职责**：API路由、认证授权、限流熔断、请求日志

**核心功能**：
- **路由管理**：根据请求路径分发到对应服务
- **认证授权**：JWT令牌验证、权限检查
- **限流保护**：防止恶意请求、保护后端服务
- **监控日志**：记录API调用情况、性能指标

#### 应用层（Application Layer）

**职责**：业务逻辑处理、服务编排、事务管理

**模块划分**：

```
应用层
├── 用户管理模块 (User Management)
│   ├── 用户注册服务
│   ├── 用户认证服务
│   ├── 权限管理服务
│   └── 用户信息服务
├── 线索管理模块 (Lead Management)
│   ├── 线索CRUD服务
│   ├── 线索重复检测服务
│   ├── 线索状态管理服务
│   └── 线索收藏服务
├── 评级引擎模块 (Rating Engine)
│   ├── 评级算法服务
│   ├── 评级规则管理服务
│   └── 评级调整服务
├── 交换引擎模块 (Exchange Engine)
│   ├── 交换规则服务
│   ├── 交换申请服务
│   ├── 交换审核服务
│   └── 权限转移服务
├── 数据分析模块 (Analytics)
│   ├── 实时统计服务
│   ├── 报表生成服务
│   └── 数据导出服务
└── 通知服务模块 (Notification)
    ├── 消息推送服务
    ├── 通知模板服务
    └── 消息状态服务
```

#### 数据层（Data Layer）

**职责**：数据持久化、数据访问、缓存管理

**存储策略**：
- **MySQL**：存储核心业务数据（用户、线索、交换记录等）
- **Redis**：缓存热点数据、会话信息、分布式锁
- **Elasticsearch**：线索搜索、全文检索、数据分析

## 3. 核心模块设计

### 3.1 用户管理模块

#### 模块架构

```
用户管理模块
├── Controller层
│   ├── UserController (用户基本操作)
│   ├── AuthController (认证相关)
│   └── PermissionController (权限管理)
├── Service层
│   ├── UserService (用户业务逻辑)
│   ├── AuthService (认证业务逻辑)
│   ├── PermissionService (权限业务逻辑)
│   └── SmsService (短信验证服务)
├── Repository层
│   ├── UserRepository (用户数据访问)
│   ├── RoleRepository (角色数据访问)
│   └── PermissionRepository (权限数据访问)
└── Domain层
    ├── User (用户实体)
    ├── Role (角色实体)
    └── Permission (权限实体)
```

#### 核心功能设计

**用户注册流程**：
1. 手机号验证码发送
2. 验证码校验
3. 用户信息填写（用户名、密码、所在城市）
4. 账户创建
5. 默认角色分配

**认证授权机制**：
- **JWT令牌**：无状态认证，支持分布式部署
- **RBAC权限模型**：基于角色的访问控制
- **权限粒度**：功能级权限控制

### 3.2 线索管理模块

#### 模块架构

```
线索管理模块
├── Controller层
│   ├── LeadController (线索CRUD)
│   ├── LeadSearchController (线索搜索)
│   └── LeadCollectionController (线索收藏)
├── Service层
│   ├── LeadService (线索业务逻辑)
│   ├── LeadDuplicateService (重复检测)
│   ├── LeadStatusService (状态管理)
│   └── LeadCollectionService (收藏管理)
├── Repository层
│   ├── LeadRepository (线索数据访问)
│   ├── LeadCollectionRepository (收藏数据访问)
│   └── LeadAuditRepository (审核数据访问)
└── Domain层
    ├── Lead (线索实体)
    ├── LeadCollection (收藏实体)
    └── LeadAudit (审核实体)
```

#### 核心功能设计

**线索重复检测算法**：

```java
/**
 * 线索重复检测服务
 */
@Service
public class LeadDuplicateService {
    
    /**
     * 检测线索是否重复
     * @param leadName 企业/项目名称
     * @return 检测结果
     */
    public DuplicateCheckResult checkDuplicate(String leadName) {
        // 1. 完全匹配检测
        if (exactMatch(leadName)) {
            return DuplicateCheckResult.EXACT_DUPLICATE;
        }
        
        // 2. 相似度检测
        double similarity = calculateSimilarity(leadName);
        if (similarity > SIMILARITY_THRESHOLD) {
            return DuplicateCheckResult.SIMILAR_DUPLICATE;
        }
        
        return DuplicateCheckResult.NO_DUPLICATE;
    }
    
    /**
     * 计算名称相似度
     */
    private double calculateSimilarity(String leadName) {
        // 使用编辑距离算法计算相似度
        // 结合中文分词和语义分析
        return LevenshteinDistance.calculate(leadName, existingNames);
    }
}
```

**线索状态机设计**：

| 状态 | 描述 | 可转换状态 | 触发条件 |
|------|------|------------|----------|
| **新建** | 线索创建后的初始状态 | 交换中、已成交、已下架 | 用户操作 |
| **审核中** | 等待管理员审核 | 新建、已拒绝 | 系统检测到相似名称 |
| **交换中** | 正在进行交换申请 | 新建、已成交 | 交换申请处理 |
| **已成交** | 交换完成 | 无 | 交换成功 |
| **已下架** | 线索被删除 | 无 | 用户删除 |

### 3.3 评级引擎模块

#### 评级算法设计

**多维度评级模型**：

```java
/**
 * 线索评级引擎
 */
@Service
public class LeadRatingEngine {
    
    /**
     * 计算线索评级
     */
    public LeadRating calculateRating(Lead lead) {
        int totalScore = 0;
        
        // 1. 信息完整度评分 (40%权重)
        int completenessScore = calculateCompletenessScore(lead);
        totalScore += completenessScore * 0.4;
        
        // 2. 企业资质评分 (30%权重)
        int qualificationScore = calculateQualificationScore(lead);
        totalScore += qualificationScore * 0.3;
        
        // 3. 企业规模评分 (20%权重)
        int scaleScore = calculateScaleScore(lead);
        totalScore += scaleScore * 0.2;
        
        // 4. 产业价值评分 (10%权重)
        int industryScore = calculateIndustryScore(lead);
        totalScore += industryScore * 0.1;
        
        return mapScoreToRating(totalScore);
    }
    
    /**
     * 信息完整度评分
     */
    private int calculateCompletenessScore(Lead lead) {
        int score = 0;
        int maxScore = 100;
        
        // 必填项检查
        if (StringUtils.isNotBlank(lead.getCompanyName())) score += 20;
        if (StringUtils.isNotBlank(lead.getContactPerson())) score += 20;
        if (StringUtils.isNotBlank(lead.getContactPhone())) score += 20;
        if (lead.getCompanyType() != null) score += 20;
        if (lead.getIntendedArea() != null) score += 20;
        
        // 选填项加分
        if (lead.getInvestmentAmount() != null) score += 10;
        if (lead.getRegisteredCapital() != null) score += 10;
        if (StringUtils.isNotBlank(lead.getIntendedRegion())) score += 10;
        if (CollectionUtils.isNotEmpty(lead.getQualifications())) score += 10;
        if (lead.getIndustryDirection() != null) score += 10;
        
        return Math.min(score, maxScore);
    }
    
    /**
     * 分数映射到评级
     */
    private LeadRating mapScoreToRating(int score) {
        if (score >= 90) return LeadRating.A;
        if (score >= 75) return LeadRating.B;
        if (score >= 60) return LeadRating.C;
        return LeadRating.D;
    }
}
```

### 3.4 交换引擎模块

#### 交换规则引擎

**交换价值计算**：

| 评级 | 价值系数 | 交换规则 |
|------|----------|----------|
| **A级** | 8 | 1个A级 = 2个B级 = 4个C级 = 8个D级 |
| **B级** | 4 | 1个B级 = 2个C级 = 4个D级 |
| **C级** | 2 | 1个C级 = 2个D级 |
| **D级** | 1 | 基础价值单位 |

**交换流程设计**：

```java
/**
 * 线索交换引擎
 */
@Service
@Transactional
public class LeadExchangeEngine {
    
    /**
     * 发起交换申请
     */
    public ExchangeApplication createExchangeApplication(
            Long applicantId, Long targetLeadId, List<Long> offerLeadIds) {
        
        // 1. 验证交换条件
        validateExchangeConditions(targetLeadId, offerLeadIds);
        
        // 2. 计算交换价值
        int targetValue = calculateLeadValue(targetLeadId);
        int offerValue = calculateTotalValue(offerLeadIds);
        
        if (offerValue < targetValue) {
            throw new InsufficientValueException("提供的线索价值不足");
        }
        
        // 3. 创建交换申请
        ExchangeApplication application = new ExchangeApplication();
        application.setApplicantId(applicantId);
        application.setTargetLeadId(targetLeadId);
        application.setOfferLeadIds(offerLeadIds);
        application.setStatus(ExchangeStatus.PENDING);
        
        // 4. 更新线索状态为交换中
        updateLeadStatus(offerLeadIds, LeadStatus.EXCHANGING);
        
        return exchangeApplicationRepository.save(application);
    }
    
    /**
     * 处理交换申请
     */
    public void processExchangeApplication(Long applicationId, boolean approved) {
        ExchangeApplication application = getApplication(applicationId);
        
        if (approved) {
            // 执行交换
            executeExchange(application);
            application.setStatus(ExchangeStatus.APPROVED);
        } else {
            // 拒绝交换，恢复线索状态
            restoreLeadStatus(application.getOfferLeadIds());
            application.setStatus(ExchangeStatus.REJECTED);
        }
        
        exchangeApplicationRepository.save(application);
    }
    
    /**
     * 执行交换逻辑
     */
    private void executeExchange(ExchangeApplication application) {
        // 1. 转移线索所有权
        transferLeadOwnership(application.getTargetLeadId(), 
                            application.getApplicantId());
        
        for (Long offerLeadId : application.getOfferLeadIds()) {
            transferLeadOwnership(offerLeadId, 
                                application.getTargetLead().getOwnerId());
        }
        
        // 2. 更新线索状态
        updateLeadStatus(Arrays.asList(application.getTargetLeadId()), 
                        LeadStatus.EXCHANGED);
        updateLeadStatus(application.getOfferLeadIds(), 
                        LeadStatus.EXCHANGED);
        
        // 3. 记录交换历史
        recordExchangeHistory(application);
        
        // 4. 发送通知
        sendExchangeNotification(application);
    }
}
```

### 3.5 通知服务模块

#### 事件驱动架构

**事件定义**：

```java
/**
 * 线索相关事件
 */
public abstract class LeadEvent {
    private Long leadId;
    private Long userId;
    private LocalDateTime timestamp;
}

/**
 * 线索创建事件
 */
public class LeadCreatedEvent extends LeadEvent {
    private Lead lead;
}

/**
 * 交换申请事件
 */
public class ExchangeApplicationEvent extends LeadEvent {
    private ExchangeApplication application;
}

/**
 * 线索删除事件
 */
public class LeadDeletedEvent extends LeadEvent {
    private List<Long> affectedUserIds; // 收藏该线索的用户
}
```

**事件处理器**：

```java
/**
 * 通知事件处理器
 */
@Component
public class NotificationEventHandler {
    
    @EventListener
    @Async
    public void handleExchangeApplication(ExchangeApplicationEvent event) {
        // 向线索持有者发送交换申请通知
        NotificationMessage message = NotificationMessage.builder()
            .recipientId(event.getApplication().getTargetLead().getOwnerId())
            .type(NotificationType.EXCHANGE_APPLICATION)
            .title("收到线索交换申请")
            .content("用户申请交换您的线索：" + event.getApplication().getTargetLead().getCompanyName())
            .build();
            
        notificationService.sendNotification(message);
    }
    
    @EventListener
    @Async
    public void handleLeadDeleted(LeadDeletedEvent event) {
        // 向收藏该线索的用户发送下架通知
        for (Long userId : event.getAffectedUserIds()) {
            NotificationMessage message = NotificationMessage.builder()
                .recipientId(userId)
                .type(NotificationType.LEAD_OFFLINE)
                .title("收藏的线索已下架")
                .content("您收藏的线索已被删除")
                .build();
                
            notificationService.sendNotification(message);
        }
    }
}
```

## 4. 数据架构设计

### 4.1 数据库设计

#### 核心数据表结构

**用户表 (users)**

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    status TINYINT DEFAULT 1 COMMENT '状态：1-正常，0-禁用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone),
    INDEX idx_city (province, city)
) COMMENT '用户表';
```

**线索表 (leads)**

```sql
CREATE TABLE leads (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '线索ID',
    owner_id BIGINT NOT NULL COMMENT '所有者ID',
    company_name VARCHAR(200) NOT NULL COMMENT '企业/项目名称',
    contact_person VARCHAR(100) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系方式',
    company_type VARCHAR(50) NOT NULL COMMENT '企业类型',
    intended_area DECIMAL(10,2) COMMENT '意向面积',
    office_type VARCHAR(50) NOT NULL COMMENT '办公类型',
    investment_amount DECIMAL(15,2) COMMENT '意向投资额',
    registered_capital DECIMAL(15,2) COMMENT '注册资本',
    intended_region VARCHAR(100) COMMENT '意向区域',
    qualifications JSON COMMENT '企业资质',
    industry_direction VARCHAR(50) COMMENT '产业方向',
    remarks TEXT COMMENT '备注',
    rating CHAR(1) DEFAULT 'D' COMMENT '评级：A/B/C/D',
    rating_score INT DEFAULT 0 COMMENT '评级分数',
    status VARCHAR(20) DEFAULT 'NEW' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (owner_id) REFERENCES users(id),
    INDEX idx_owner (owner_id),
    INDEX idx_company_name (company_name),
    INDEX idx_rating (rating),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT '线索表';
```

**交换申请表 (exchange_applications)**

```sql
CREATE TABLE exchange_applications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '申请ID',
    applicant_id BIGINT NOT NULL COMMENT '申请人ID',
    target_lead_id BIGINT NOT NULL COMMENT '目标线索ID',
    offer_lead_ids JSON NOT NULL COMMENT '提供的线索ID列表',
    target_value INT NOT NULL COMMENT '目标线索价值',
    offer_value INT NOT NULL COMMENT '提供线索总价值',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING/APPROVED/REJECTED',
    processed_at TIMESTAMP NULL COMMENT '处理时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (applicant_id) REFERENCES users(id),
    FOREIGN KEY (target_lead_id) REFERENCES leads(id),
    INDEX idx_applicant (applicant_id),
    INDEX idx_target_lead (target_lead_id),
    INDEX idx_status (status)
) COMMENT '交换申请表';
```

### 4.2 缓存策略设计

#### Redis缓存架构

**缓存分层策略**：

| 缓存层级 | 数据类型 | TTL | 用途 |
|----------|----------|-----|------|
| **L1缓存** | 用户会话 | 30分钟 | JWT令牌、用户权限 |
| **L2缓存** | 热点数据 | 1小时 | 线索列表、评级统计 |
| **L3缓存** | 静态数据 | 24小时 | 字典数据、配置信息 |

**缓存键设计规范**：

```
# 用户相关
user:profile:{userId}           # 用户基本信息
user:permissions:{userId}       # 用户权限列表
user:session:{sessionId}        # 用户会话信息

# 线索相关
lead:detail:{leadId}           # 线索详细信息
lead:list:user:{userId}        # 用户线索列表
lead:list:public:page:{page}   # 公开线索分页列表
lead:rating:stats              # 评级统计信息

# 交换相关
exchange:applications:{userId}  # 用户交换申请列表
exchange:pending:{leadOwnerId} # 待处理交换申请

# 系统相关
system:config                  # 系统配置
system:dict:{type}            # 字典数据
```

**缓存更新策略**：

```java
/**
 * 缓存管理服务
 */
@Service
public class CacheService {
    
    /**
     * 线索缓存更新策略
     */
    @CacheEvict(value = "leads", allEntries = true)
    public void evictLeadCache(Long leadId) {
        // 删除相关缓存
        redisTemplate.delete("lead:detail:" + leadId);
        redisTemplate.delete("lead:list:user:*");
        redisTemplate.delete("lead:list:public:*");
    }
    
    /**
     * 用户缓存更新策略
     */
    @CacheEvict(value = "users", key = "#userId")
    public void evictUserCache(Long userId) {
        redisTemplate.delete("user:profile:" + userId);
        redisTemplate.delete("user:permissions:" + userId);
    }
}
```

### 4.3 搜索架构设计

#### Elasticsearch索引设计

**线索搜索索引**：

```json
{
  "mappings": {
    "properties": {
      "id": { "type": "long" },
      "companyName": {
        "type": "text",
        "analyzer": "ik_max_word",
        "search_analyzer": "ik_smart",
        "fields": {
          "keyword": { "type": "keyword" }
        }
      },
      "companyType": { "type": "keyword" },
      "qualifications": { "type": "keyword" },
      "industryDirection": { "type": "keyword" },
      "officeType": { "type": "keyword" },
      "intendedRegion": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "rating": { "type": "keyword" },
      "ratingScore": { "type": "integer" },
      "investmentAmount": { "type": "double" },
      "registeredCapital": { "type": "double" },
      "intendedArea": { "type": "double" },
      "status": { "type": "keyword" },
      "ownerId": { "type": "long" },
      "createdAt": { "type": "date" },
      "updatedAt": { "type": "date" }
    }
  },
  "settings": {
    "number_of_shards": 3,
    "number_of_replicas": 1,
    "analysis": {
      "analyzer": {
        "ik_max_word": {
          "type": "ik_max_word"
        },
        "ik_smart": {
          "type": "ik_smart"
        }
      }
    }
  }
}
```

**搜索查询示例**：

```java
/**
 * 线索搜索服务
 */
@Service
public class LeadSearchService {
    
    /**
     * 多条件搜索线索
     */
    public SearchResult<Lead> searchLeads(LeadSearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        
        // 关键词搜索
        if (StringUtils.isNotBlank(request.getKeyword())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(request.getKeyword())
                .field("companyName", 2.0f)  // 企业名称权重更高
                .field("industryDirection", 1.5f)
                .field("intendedRegion", 1.0f));
        }
        
        // 筛选条件
        if (CollectionUtils.isNotEmpty(request.getCompanyTypes())) {
            boolQuery.filter(QueryBuilders.termsQuery("companyType", request.getCompanyTypes()));
        }
        
        if (CollectionUtils.isNotEmpty(request.getRatings())) {
            boolQuery.filter(QueryBuilders.termsQuery("rating", request.getRatings()));
        }
        
        // 投资额范围
        if (request.getMinInvestment() != null || request.getMaxInvestment() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("investmentAmount");
            if (request.getMinInvestment() != null) {
                rangeQuery.gte(request.getMinInvestment());
            }
            if (request.getMaxInvestment() != null) {
                rangeQuery.lte(request.getMaxInvestment());
            }
            boolQuery.filter(rangeQuery);
        }
        
        // 排序
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
            .query(boolQuery)
            .from(request.getPage() * request.getSize())
            .size(request.getSize());
            
        if ("rating".equals(request.getSortBy())) {
            sourceBuilder.sort("ratingScore", SortOrder.DESC);
        } else if ("time".equals(request.getSortBy())) {
            sourceBuilder.sort("createdAt", SortOrder.DESC);
        }
        
        return executeSearch(sourceBuilder);
    }
}
```

## 5. 安全架构设计

### 5.1 认证授权架构

#### JWT认证机制

**令牌结构设计**：

```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "用户ID",
    "username": "用户名",
    "roles": ["ROLE_USER"],
    "permissions": ["lead:read", "lead:write"],
    "iat": 1640995200,
    "exp": 1641081600
  }
}
```

**认证流程设计**：

```java
/**
 * JWT认证服务
 */
@Service
public class JwtAuthenticationService {
    
    /**
     * 用户登录认证
     */
    public AuthenticationResult authenticate(LoginRequest request) {
        // 1. 验证用户凭据
        User user = validateCredentials(request.getPhone(), request.getPassword());
        
        // 2. 生成访问令牌
        String accessToken = generateAccessToken(user);
        
        // 3. 生成刷新令牌
        String refreshToken = generateRefreshToken(user);
        
        // 4. 缓存用户会话
        cacheUserSession(user.getId(), accessToken);
        
        return AuthenticationResult.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtProperties.getAccessTokenExpiration())
            .user(UserDto.from(user))
            .build();
    }
    
    /**
     * 令牌刷新
     */
    public AuthenticationResult refreshToken(String refreshToken) {
        // 1. 验证刷新令牌
        Claims claims = validateRefreshToken(refreshToken);
        
        // 2. 获取用户信息
        Long userId = Long.valueOf(claims.getSubject());
        User user = userService.findById(userId);
        
        // 3. 生成新的访问令牌
        String newAccessToken = generateAccessToken(user);
        
        return AuthenticationResult.builder()
            .accessToken(newAccessToken)
            .refreshToken(refreshToken)  // 刷新令牌保持不变
            .expiresIn(jwtProperties.getAccessTokenExpiration())
            .build();
    }
}
```

#### RBAC权限模型

**权限设计**：

| 权限类别 | 权限代码 | 权限描述 |
|----------|----------|----------|
| **线索管理** | lead:read | 查看线索 |
| | lead:write | 创建/编辑线索 |
| | lead:delete | 删除线索 |
| | lead:audit | 审核线索 |
| **线索交换** | exchange:apply | 发起交换申请 |
| | exchange:approve | 审批交换申请 |
| | exchange:view | 查看交换记录 |
| **数据分析** | analytics:view | 查看数据看板 |
| | analytics:export | 导出数据报表 |
| **系统管理** | system:user | 用户管理 |
| | system:config | 系统配置 |

**权限检查拦截器**：

```java
/**
 * 权限检查拦截器
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        // 1. 获取方法上的权限注解
        RequirePermission annotation = getPermissionAnnotation(handler);
        if (annotation == null) {
            return true; // 无需权限检查
        }
        
        // 2. 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("用户未认证");
        }
        
        // 3. 检查权限
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String[] requiredPermissions = annotation.value();
        
        for (String permission : requiredPermissions) {
            if (!userPrincipal.hasPermission(permission)) {
                throw new ForbiddenException("权限不足：" + permission);
            }
        }
        
        return true;
    }
}
```

### 5.2 数据安全设计

#### 敏感数据加密

**加密策略**：

| 数据类型 | 加密方式 | 密钥管理 |
|----------|----------|----------|
| **密码** | BCrypt哈希 | 系统内置盐值 |
| **手机号** | AES-256对称加密 | 配置文件密钥 |
| **联系方式** | AES-256对称加密 | 数据库密钥表 |
| **投资金额** | 格式化脱敏 | 无需密钥 |

**加密工具类**：

```java
/**
 * 数据加密工具
 */
@Component
public class DataEncryptionUtil {
    
    @Value("${app.encryption.secret-key}")
    private String secretKey;
    
    /**
     * 加密敏感信息
     */
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(generateIV());
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new EncryptionException("数据加密失败", e);
        }
    }
    
    /**
     * 解密敏感信息
     */
    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(extractIV(encryptedText));
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionException("数据解密失败", e);
        }
    }
}
```

#### 数据脱敏处理

**脱敏规则**：

```java
/**
 * 数据脱敏服务
 */
@Service
public class DataMaskingService {
    
    /**
     * 企业名称脱敏
     */
    public String maskCompanyName(String companyName) {
        if (StringUtils.isBlank(companyName)) {
            return companyName;
        }
        
        if (companyName.length() <= 2) {
            return companyName.charAt(0) + "*";
        }
        
        return companyName.charAt(0) + "*".repeat(companyName.length() - 1);
    }
    
    /**
     * 联系人姓名脱敏
     */
    public String maskContactPerson(String contactPerson) {
        if (StringUtils.isBlank(contactPerson)) {
            return contactPerson;
        }
        
        if (contactPerson.length() <= 1) {
            return "*";
        }
        
        return contactPerson.charAt(0) + "*".repeat(contactPerson.length() - 1);
    }
    
    /**
     * 手机号脱敏
     */
    public String maskPhoneNumber(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber) || phoneNumber.length() < 7) {
            return "***";
        }
        
        return phoneNumber.substring(0, 3) + "****" + 
               phoneNumber.substring(phoneNumber.length() - 4);
    }
}
```

### 5.3 API安全设计

#### 接口防护策略

**限流保护**：

```java
/**
 * API限流配置
 */
@Configuration
public class RateLimitConfig {
    
    /**
     * 用户级别限流
     */
    @Bean
    public RedisRateLimiter userRateLimiter() {
        return new RedisRateLimiter(
            100,  // 每秒允许100个请求
            200   // 突发流量最多200个请求
        );
    }
    
    /**
     * IP级别限流
     */
    @Bean
    public RedisRateLimiter ipRateLimiter() {
        return new RedisRateLimiter(
            1000, // 每秒允许1000个请求
            2000  // 突发流量最多2000个请求
        );
    }
}

/**
 * 限流拦截器
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                           HttpServletResponse response, 
                           Object handler) throws Exception {
        
        String clientIp = getClientIp(request);
        String userId = getCurrentUserId(request);
        
        // IP级别限流检查
        if (!ipRateLimiter.isAllowed("ip:" + clientIp)) {
            throw new RateLimitExceededException("IP访问频率过高");
        }
        
        // 用户级别限流检查
        if (userId != null && !userRateLimiter.isAllowed("user:" + userId)) {
            throw new RateLimitExceededException("用户访问频率过高");
        }
        
        return true;
    }
}
```

**输入验证**：

```java
/**
 * 输入验证注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SafeStringValidator.class)
public @interface SafeString {
    String message() default "输入包含非法字符";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    boolean allowHtml() default false;
    int maxLength() default 255;
}

/**
 * 安全字符串验证器
 */
public class SafeStringValidator implements ConstraintValidator<SafeString, String> {
    
    private static final Pattern DANGEROUS_PATTERN = 
        Pattern.compile(".*[<>\"'%;()&+]");
    
    private boolean allowHtml;
    private int maxLength;
    
    @Override
    public void initialize(SafeString constraintAnnotation) {
        this.allowHtml = constraintAnnotation.allowHtml();
        this.maxLength = constraintAnnotation.maxLength();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        
        // 长度检查
        if (value.length() > maxLength) {
            return false;
        }
        
        // 危险字符检查
        if (!allowHtml && DANGEROUS_PATTERN.matcher(value).matches()) {
            return false;
        }
        
        return true;
    }
}
```

## 6. 部署架构设计

### 6.1 容器化部署

#### Docker镜像构建

**后端应用Dockerfile**：

```dockerfile
# 多阶段构建
FROM maven:3.8.4-openjdk-11 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 运行时镜像
FROM openjdk:11-jre-slim

WORKDIR /app

# 创建非root用户
RUN groupadd -r appuser && useradd -r -g appuser appuser

# 复制应用文件
COPY --from=builder /app/target/*.jar app.jar

# 设置文件权限
RUN chown -R appuser:appuser /app
USER appuser

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "-Xmx1g", "-Xms512m", "app.jar"]
```

**前端应用Dockerfile**：

```dockerfile
# 构建阶段
FROM node:16-alpine AS builder

WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# 运行阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/nginx.conf

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### Kubernetes部署配置

**应用部署配置**：

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: lead-exchange-app
  namespace: production
spec:
  replicas: 3
  selector:
    matchLabels:
      app: lead-exchange-app
  template:
    metadata:
      labels:
        app: lead-exchange-app
    spec:
      containers:
      - name: app
        image: lead-exchange-app:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: DB_HOST
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: host
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: db-secret
              key: password
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: lead-exchange-service
  namespace: production
spec:
  selector:
    app: lead-exchange-app
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: ClusterIP
```

### 6.2 高可用架构

#### 负载均衡配置

**Nginx配置**：

```nginx
upstream backend {
    least_conn;
    server app1:8080 max_fails=3 fail_timeout=30s;
    server app2:8080 max_fails=3 fail_timeout=30s;
    server app3:8080 max_fails=3 fail_timeout=30s;
}

server {
    listen 80;
    server_name lead-exchange.example.com;
    
    # 重定向到HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name lead-exchange.example.com;
    
    # SSL配置
    ssl_certificate /etc/ssl/certs/lead-exchange.crt;
    ssl_certificate_key /etc/ssl/private/lead-exchange.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-RSA-AES256-GCM-SHA512:DHE-RSA-AES256-GCM-SHA512;
    
    # 静态资源
    location /static/ {
        alias /var/www/static/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
    
    # API请求
    location /api/ {
        proxy_pass http://backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # 超时配置
        proxy_connect_timeout 5s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # 缓冲配置
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
    }
    
    # 前端应用
    location / {
        try_files $uri $uri/ /index.html;
        add_header Cache-Control "no-cache";
    }
}
```

#### 数据库高可用

**MySQL主从配置**：

```yaml
# MySQL主库配置
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
        - name: MYSQL_REPLICATION_USER
          value: "replicator"
        - name: MYSQL_REPLICATION_PASSWORD
          valueFrom:
            secretKeyRef:
              name: mysql-secret
              key: replication-password
        ports:
        - containerPort: 3306
        volumeMounts:
        - name: mysql-data
          mountPath: /var/lib/mysql
        - name: mysql-config
          mountPath: /etc/mysql/conf.d
  volumeClaimTemplates:
  - metadata:
      name: mysql-data
    spec:
      accessModes: ["ReadWriteOnce"]
      resources:
        requests:
          storage: 100Gi
```

### 6.3 监控与运维

#### 监控架构设计

**Prometheus监控配置**：

```yaml
# Prometheus配置
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert_rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  # 应用监控
  - job_name: 'lead-exchange-app'
    static_configs:
      - targets: ['app1:8080', 'app2:8080', 'app3:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 30s
    
  # 数据库监控
  - job_name: 'mysql'
    static_configs:
      - targets: ['mysql-exporter:9104']
    
  # Redis监控
  - job_name: 'redis'
    static_configs:
      - targets: ['redis-exporter:9121']
    
  # 系统监控
  - job_name: 'node'
    static_configs:
      - targets: ['node-exporter:9100']
```

**关键指标监控**：

| 监控类别 | 指标名称 | 阈值 | 告警级别 |
|----------|----------|------|----------|
| **应用性能** | 响应时间 | >3秒 | Warning |
| | 错误率 | >5% | Critical |
| | QPS | >1000 | Info |
| **系统资源** | CPU使用率 | >80% | Warning |
| | 内存使用率 | >85% | Warning |
| | 磁盘使用率 | >90% | Critical |
| **数据库** | 连接数 | >80% | Warning |
| | 慢查询 | >100ms | Warning |
| | 主从延迟 | >5秒 | Critical |

#### 日志管理架构

**ELK日志收集配置**：

```yaml
# Filebeat配置
filebeat.inputs:
- type: log
  enabled: true
  paths:
    - /var/log/app/*.log
  fields:
    service: lead-exchange
    environment: production
  multiline.pattern: '^\d{4}-\d{2}-\d{2}'
  multiline.negate: true
  multiline.match: after

output.elasticsearch:
  hosts: ["elasticsearch:9200"]
  index: "lead-exchange-logs-%{+yyyy.MM.dd}"
  
logging.level: info
logging.to_files: true
logging.files:
  path: /var/log/filebeat
  name: filebeat
  keepfiles: 7
  permissions: 0644
```

## 7. 性能优化策略

### 7.1 数据库优化

#### 索引优化策略

**核心查询索引设计**：

```sql
-- 线索查询优化索引
CREATE INDEX idx_leads_search ON leads(status, rating, company_type, created_at);
CREATE INDEX idx_leads_owner_status ON leads(owner_id, status);
CREATE INDEX idx_leads_rating_created ON leads(rating, created_at DESC);

-- 交换申请查询索引
CREATE INDEX idx_exchange_applicant_status ON exchange_applications(applicant_id, status);
CREATE INDEX idx_exchange_target_status ON exchange_applications(target_lead_id, status);

-- 用户相关索引
CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_city ON users(province, city);

-- 复合索引优化
CREATE INDEX idx_leads_composite ON leads(owner_id, status, rating, created_at DESC);
```

#### 分库分表策略

**分表规则设计**：

```java
/**
 * 分表策略配置
 */
@Configuration
public class ShardingConfig {
    
    /**
     * 线索表分表策略
     */
    @Bean
    public ShardingRuleConfiguration shardingRuleConfig() {
        ShardingRuleConfiguration config = new ShardingRuleConfiguration();
        
        // 线索表分表配置
        TableRuleConfiguration leadTableRule = new TableRuleConfiguration("leads", 
            "ds0.leads_${0..11}");
        
        // 按创建时间月份分表
        leadTableRule.setTableShardingStrategyConfig(
            new StandardShardingStrategyConfiguration("created_at", 
                new MonthShardingAlgorithm()));
        
        config.getTableRuleConfigs().add(leadTableRule);
        
        return config;
    }
}

/**
 * 按月分表算法
 */
public class MonthShardingAlgorithm implements PreciseShardingAlgorithm<Date> {
    
    @Override
    public String doSharding(Collection<String> availableTargetNames, 
                           PreciseShardingValue<Date> shardingValue) {
        Date createTime = shardingValue.getValue();
        int month = createTime.getMonth();
        
        String tableName = "leads_" + month;
        
        for (String targetName : availableTargetNames) {
            if (targetName.endsWith(tableName)) {
                return targetName;
            }
        }
        
        throw new IllegalArgumentException("无法找到对应的分表");
    }
}
```

### 7.2 缓存优化

#### 多级缓存架构

```java
/**
 * 多级缓存管理器
 */
@Service
public class MultiLevelCacheManager {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private CaffeineCache localCache;
    
    /**
     * 获取缓存数据
     */
    public <T> T get(String key, Class<T> type, Supplier<T> dataLoader) {
        // 1. 本地缓存查询
        T result = localCache.get(key, type);
        if (result != null) {
            return result;
        }
        
        // 2. Redis缓存查询
        result = (T) redisTemplate.opsForValue().get(key);
        if (result != null) {
            // 回写本地缓存
            localCache.put(key, result, Duration.ofMinutes(5));
            return result;
        }
        
        // 3. 数据库查询
        result = dataLoader.get();
        if (result != null) {
            // 写入Redis缓存
            redisTemplate.opsForValue().set(key, result, Duration.ofHours(1));
            // 写入本地缓存
            localCache.put(key, result, Duration.ofMinutes(5));
        }
        
        return result;
    }
    
    /**
     * 缓存预热
     */
    @EventListener(ApplicationReadyEvent.class)
    public void warmUpCache() {
        // 预热热点数据
        CompletableFuture.runAsync(() -> {
            // 预热用户权限数据
            warmUpUserPermissions();
            
            // 预热线索评级统计
            warmUpRatingStats();
            
            // 预热系统配置
            warmUpSystemConfig();
        });
    }
}
```

### 7.3 前端性能优化

#### 代码分割与懒加载

```javascript
// 路由懒加载配置
const routes = [
  {
    path: '/leads',
    name: 'LeadManagement',
    component: () => import(/* webpackChunkName: "leads" */ '@/views/LeadManagement.vue')
  },
  {
    path: '/exchange',
    name: 'ExchangeCenter',
    component: () => import(/* webpackChunkName: "exchange" */ '@/views/ExchangeCenter.vue')
  },
  {
    path: '/analytics',
    name: 'Analytics',
    component: () => import(/* webpackChunkName: "analytics" */ '@/views/Analytics.vue')
  }
];

// 组件懒加载
export default {
  components: {
    LeadCard: () => import('@/components/LeadCard.vue'),
    ExchangeDialog: () => import('@/components/ExchangeDialog.vue')
  }
};
```

#### 数据虚拟化

```vue
<template>
  <div class="virtual-list-container">
    <!-- 虚拟滚动列表 -->
    <RecycleScroller
      class="scroller"
      :items="leads"
      :item-size="120"
      key-field="id"
      v-slot="{ item }"
    >
      <LeadCard :lead="item" />
    </RecycleScroller>
  </div>
</template>

<script>
import { RecycleScroller } from 'vue-virtual-scroller';
import LeadCard from '@/components/LeadCard.vue';

export default {
  components: {
    RecycleScroller,
    LeadCard
  },
  data() {
    return {
      leads: [] // 大量线索数据
    };
  }
};
</script>
```

## 8. 风险评估与缓解

### 8.1 技术风险

| 风险类别 | 风险描述 | 影响程度 | 缓解策略 |
|----------|----------|----------|----------|
| **性能风险** | 大量并发访问导致系统响应缓慢 | 高 | 负载均衡、缓存优化、数据库优化 |
| **数据风险** | 数据丢失或损坏 | 高 | 定期备份、主从复制、事务保护 |
| **安全风险** | 数据泄露或恶意攻击 | 高 | 加密存储、权限控制、安全审计 |
| **可用性风险** | 系统故障导致服务中断 | 中 | 高可用部署、监控告警、快速恢复 |
| **扩展性风险** | 业务增长超出系统承载能力 | 中 | 微服务改造、分库分表、云原生 |

### 8.2 业务风险

| 风险类别 | 风险描述 | 影响程度 | 缓解策略 |
|----------|----------|----------|----------|
| **数据质量** | 线索信息不准确或重复 | 中 | 重复检测算法、数据验证规则 |
| **用户体验** | 系统操作复杂影响用户使用 | 中 | 用户体验测试、界面优化 |
| **业务规则** | 交换规则不合理影响公平性 | 中 | 规则引擎、动态调整机制 |

### 8.3 运维风险

| 风险类别 | 风险描述 | 影响程度 | 缓解策略 |
|----------|----------|----------|----------|
| **部署风险** | 部署过程中出现故障 | 中 | 蓝绿部署、回滚机制 |
| **监控盲区** | 关键指标监控不到位 | 中 | 全面监控、告警机制 |
| **人员风险** | 关键人员离职影响运维 | 低 | 文档完善、知识传承 |

## 9. 实施计划

### 9.1 开发阶段规划

#### 第一阶段：基础功能开发（4周）

**目标**：完成核心业务功能

- **用户管理模块**（1周）
  - 用户注册登录
  - 权限管理
  - 个人信息管理

- **线索管理模块**（2周）
  - 线索CRUD操作
  - 线索重复检测
  - 线索状态管理
  - 线索收藏功能

- **评级引擎模块**（1周）
  - 评级算法实现
  - 评级规则配置
  - 评级调整功能

#### 第二阶段：交换功能开发（3周）

**目标**：实现线索交换核心流程

- **交换引擎模块**（2周）
  - 交换申请流程
  - 交换审核机制
  - 权限转移逻辑

- **通知服务模块**（1周）
  - 消息推送功能
  - 通知模板管理
  - 事件驱动架构

#### 第三阶段：数据分析与优化（2周）

**目标**：完善数据分析和性能优化

- **数据分析模块**（1周）
  - 个人看板
  - 系统看板
  - 数据导出

- **性能优化**（1周）
  - 缓存优化
  - 数据库优化
  - 前端优化

### 9.2 测试阶段规划

#### 单元测试（1周）
- 核心业务逻辑测试
- 工具类方法测试
- 覆盖率要求：>80%

#### 集成测试（1周）
- API接口测试
- 数据库集成测试
- 第三方服务集成测试

#### 性能测试（1周）
- 压力测试：5000并发用户
- 负载测试：持续高负载运行
- 稳定性测试：长时间运行测试

#### 安全测试（1周）
- 权限控制测试
- 数据加密测试
- 安全漏洞扫描

### 9.3 部署上线规划

#### 环境准备（1周）
- 生产环境搭建
- 监控系统部署
- 备份策略实施

#### 灰度发布（1周）
- 小范围用户测试
- 问题修复和优化
- 逐步扩大用户范围

#### 全量上线（1周）
- 全用户开放
- 监控和运维
- 问题快速响应

## 10. 总结

### 10.1 架构优势

1. **模块化设计**：清晰的模块划分，便于开发维护和功能扩展
2. **技术成熟**：采用主流技术栈，降低技术风险和学习成本
3. **安全可靠**：完善的安全机制和数据保护措施
4. **高性能**：多级缓存、数据库优化等性能优化策略
5. **可扩展**：支持水平扩展和微服务演进

### 10.2 关键决策

1. **架构风格**：选择模块化单体架构，平衡复杂度和可维护性
2. **技术栈**：Spring Boot + Vue.js，成熟稳定的企业级技术栈
3. **数据存储**：MySQL + Redis + Elasticsearch，满足不同数据需求
4. **部署方式**：容器化部署，支持弹性扩缩容
5. **安全策略**：JWT认证 + RBAC权限 + 数据加密，全方位安全保护

### 10.3 演进路径

1. **短期目标**：完成基础功能，满足核心业务需求
2. **中期目标**：性能优化，支持更大用户规模
3. **长期目标**：微服务改造，支持更复杂的业务场景

本架构设计方案基于需求文档的深入分析，结合行业最佳实践，为招商线索流通Web应用提供了一个安全、高效、可扩展的技术架构。通过合理的技术选型、清晰的模块设计和完善的安全机制，能够有效支撑业务目标的实现，并为未来的发展提供良好的技术基础。