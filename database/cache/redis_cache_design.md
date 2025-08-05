# Redis缓存键值结构设计方案

## 1. 缓存策略概述

### 1.1 缓存原则
- **热点数据优先**：缓存高频访问的数据
- **分层缓存**：L1（本地缓存）+ L2（Redis缓存）
- **过期策略**：合理设置TTL，避免缓存雪崩
- **一致性保证**：写操作时及时更新或删除缓存
- **容量控制**：设置最大内存限制和淘汰策略

### 1.2 命名规范
- **格式**：`{项目前缀}:{模块}:{业务类型}:{具体标识}`
- **项目前缀**：`leadex`
- **分隔符**：使用冒号`:`分隔
- **大小写**：全部小写
- **特殊字符**：避免使用空格和特殊字符

## 2. 用户管理模块缓存

### 2.1 用户会话缓存
```redis
# 用户会话信息
KEY: leadex:user:session:{userId}
TYPE: Hash
TTL: 86400 (24小时)
FIELDS:
  - token: JWT令牌
  - username: 用户名
  - realName: 真实姓名
  - avatar: 头像URL
  - companyName: 企业名称
  - roles: 角色列表(JSON)
  - permissions: 权限列表(JSON)
  - lastLoginTime: 最后登录时间
  - loginCount: 登录次数
```

### 2.2 用户权限缓存
```redis
# 用户权限列表
KEY: leadex:user:permissions:{userId}
TYPE: Set
TTL: 3600 (1小时)
VALUE: 权限编码集合

# 用户角色缓存
KEY: leadex:user:roles:{userId}
TYPE: Set
TTL: 3600 (1小时)
VALUE: 角色编码集合
```

### 2.3 用户基本信息缓存
```redis
# 用户基本信息
KEY: leadex:user:info:{userId}
TYPE: Hash
TTL: 1800 (30分钟)
FIELDS:
  - id: 用户ID
  - username: 用户名
  - realName: 真实姓名
  - phone: 手机号(脱敏)
  - email: 邮箱(脱敏)
  - companyName: 企业名称
  - industry: 行业
  - regionName: 地区
  - status: 状态
  - verified: 认证状态
```

## 3. 线索管理模块缓存

### 3.1 热点线索缓存
```redis
# 线索详情缓存
KEY: leadex:lead:detail:{leadId}
TYPE: Hash
TTL: 1800 (30分钟)
FIELDS:
  - id: 线索ID
  - title: 标题
  - description: 描述
  - companyName: 企业名称
  - contactPerson: 联系人
  - investmentAmount: 投资金额
  - categoryName: 行业分类
  - regionName: 地区
  - rating: 评级
  - status: 状态
  - viewCount: 浏览次数
  - favoriteCount: 收藏次数
  - publishedTime: 发布时间
  - publisherName: 发布者姓名
```

### 3.2 线索列表缓存
```redis
# 线索列表缓存（分页）
KEY: leadex:lead:list:{categoryId}:{regionId}:{rating}:{page}:{size}
TYPE: String (JSON)
TTL: 300 (5分钟)
VALUE: 线索列表JSON数据

# 热门线索排行
KEY: leadex:lead:hot:ranking
TYPE: Sorted Set
TTL: 600 (10分钟)
SCORE: 热度分数(浏览量*0.3 + 收藏量*0.5 + 交换量*0.2)
MEMBER: 线索ID
```

### 3.3 线索统计缓存
```redis
# 线索浏览计数
KEY: leadex:lead:view:count:{leadId}
TYPE: String
TTL: 3600 (1小时)
VALUE: 浏览次数

# 线索收藏计数
KEY: leadex:lead:favorite:count:{leadId}
TYPE: String
TTL: 3600 (1小时)
VALUE: 收藏次数

# 用户收藏的线索列表
KEY: leadex:user:favorites:{userId}
TYPE: Set
TTL: 1800 (30分钟)
VALUE: 线索ID集合
```

### 3.4 搜索结果缓存
```redis
# 搜索结果缓存
KEY: leadex:search:result:{queryHash}
TYPE: String (JSON)
TTL: 300 (5分钟)
VALUE: 搜索结果JSON数据

# 搜索热词统计
KEY: leadex:search:hotwords
TYPE: Sorted Set
TTL: 86400 (24小时)
SCORE: 搜索次数
MEMBER: 搜索关键词
```

## 4. 交换引擎模块缓存

### 4.1 用户积分缓存
```redis
# 用户积分信息
KEY: leadex:user:credits:{userId}
TYPE: Hash
TTL: 1800 (30分钟)
FIELDS:
  - totalCredits: 总积分
  - availableCredits: 可用积分
  - frozenCredits: 冻结积分
  - consumedCredits: 已消费积分
  - earnedCredits: 累计获得积分
  - lastUpdateTime: 最后更新时间
```

### 4.2 交换申请缓存
```redis
# 待处理的交换申请
KEY: leadex:exchange:pending:{userId}
TYPE: List
TTL: 3600 (1小时)
VALUE: 交换申请ID列表

# 交换申请详情
KEY: leadex:exchange:request:{requestId}
TYPE: Hash
TTL: 1800 (30分钟)
FIELDS:
  - id: 申请ID
  - requestNo: 申请编号
  - requesterId: 申请人ID
  - targetLeadId: 目标线索ID
  - offeredCredits: 提供积分
  - requiredCredits: 需要积分
  - status: 状态
  - expireTime: 过期时间
```

### 4.3 交换统计缓存
```redis
# 用户交换统计
KEY: leadex:user:exchange:stats:{userId}
TYPE: Hash
TTL: 3600 (1小时)
FIELDS:
  - totalExchanges: 总交换次数
  - successfulExchanges: 成功交换次数
  - pendingRequests: 待处理申请数
  - todayExchanges: 今日交换次数
  - monthlyExchanges: 本月交换次数
```

## 5. 通知服务模块缓存

### 5.1 用户通知缓存
```redis
# 用户未读通知数量
KEY: leadex:notification:unread:count:{userId}
TYPE: String
TTL: 300 (5分钟)
VALUE: 未读通知数量

# 用户通知列表
KEY: leadex:notification:list:{userId}:{page}
TYPE: String (JSON)
TTL: 300 (5分钟)
VALUE: 通知列表JSON数据
```

### 5.2 通知模板缓存
```redis
# 通知模板缓存
KEY: leadex:notification:template:{templateCode}
TYPE: Hash
TTL: 3600 (1小时)
FIELDS:
  - id: 模板ID
  - templateName: 模板名称
  - templateType: 模板类型
  - title: 标题模板
  - content: 内容模板
  - variables: 变量说明
```

### 5.3 通知设置缓存
```redis
# 用户通知设置
KEY: leadex:notification:settings:{userId}
TYPE: Hash
TTL: 3600 (1小时)
FIELDS:
  - systemEnabled: 系统通知开关
  - smsEnabled: 短信通知开关
  - emailEnabled: 邮件通知开关
  - wechatEnabled: 微信通知开关
  - quietStartTime: 免打扰开始时间
  - quietEndTime: 免打扰结束时间
```

## 6. 系统配置模块缓存

### 6.1 系统配置缓存
```redis
# 系统配置缓存
KEY: leadex:config:{configKey}
TYPE: String
TTL: 3600 (1小时)
VALUE: 配置值

# 配置分组缓存
KEY: leadex:config:group:{groupName}
TYPE: Hash
TTL: 3600 (1小时)
FIELDS: 该分组下的所有配置项
```

### 6.2 评级规则缓存
```redis
# 评级规则缓存
KEY: leadex:rating:rules
TYPE: String (JSON)
TTL: 3600 (1小时)
VALUE: 评级规则JSON数据

# 交换规则缓存
KEY: leadex:exchange:rules
TYPE: String (JSON)
TTL: 3600 (1小时)
VALUE: 交换规则JSON数据
```

### 6.3 数据字典缓存
```redis
# 数据字典缓存
KEY: leadex:dict:{dictType}
TYPE: String (JSON)
TTL: 7200 (2小时)
VALUE: 字典数据JSON

# 行业分类缓存
KEY: leadex:categories:tree
TYPE: String (JSON)
TTL: 7200 (2小时)
VALUE: 行业分类树JSON

# 地区数据缓存
KEY: leadex:regions:tree
TYPE: String (JSON)
TTL: 7200 (2小时)
VALUE: 地区数据树JSON
```

## 7. 分布式锁

### 7.1 业务锁
```redis
# 用户操作锁
KEY: leadex:lock:user:{userId}
TYPE: String
TTL: 30 (30秒)
VALUE: 锁标识符

# 线索操作锁
KEY: leadex:lock:lead:{leadId}
TYPE: String
TTL: 30 (30秒)
VALUE: 锁标识符

# 交换操作锁
KEY: leadex:lock:exchange:{requestId}
TYPE: String
TTL: 60 (60秒)
VALUE: 锁标识符

# 积分操作锁
KEY: leadex:lock:credits:{userId}
TYPE: String
TTL: 30 (30秒)
VALUE: 锁标识符
```

## 8. 统计分析缓存

### 8.1 实时统计
```redis
# 在线用户统计
KEY: leadex:stats:online:users
TYPE: Set
TTL: 1800 (30分钟)
VALUE: 在线用户ID集合

# 今日统计数据
KEY: leadex:stats:daily:{date}
TYPE: Hash
TTL: 86400 (24小时)
FIELDS:
  - newUsers: 新增用户数
  - newLeads: 新增线索数
  - exchanges: 交换次数
  - pageViews: 页面浏览量
  - uniqueVisitors: 独立访客数
```

### 8.2 排行榜缓存
```redis
# 用户活跃度排行
KEY: leadex:ranking:user:activity
TYPE: Sorted Set
TTL: 3600 (1小时)
SCORE: 活跃度分数
MEMBER: 用户ID

# 企业线索发布排行
KEY: leadex:ranking:company:leads
TYPE: Sorted Set
TTL: 3600 (1小时)
SCORE: 线索发布数量
MEMBER: 企业名称
```

## 9. 缓存管理策略

### 9.1 缓存更新策略
- **Cache Aside**：读取时检查缓存，缓存未命中时查询数据库并更新缓存
- **Write Through**：写入时同时更新数据库和缓存
- **Write Behind**：写入时先更新缓存，异步更新数据库

### 9.2 缓存失效策略
- **主动失效**：数据更新时主动删除相关缓存
- **被动失效**：设置合理的TTL，让缓存自动过期
- **版本控制**：使用版本号控制缓存一致性

### 9.3 缓存预热
```redis
# 系统启动时预热的缓存
1. 系统配置数据
2. 数据字典
3. 行业分类和地区数据
4. 通知模板
5. 评级和交换规则
6. 热门线索数据
```

### 9.4 缓存监控
```redis
# 缓存命中率监控
KEY: leadex:monitor:cache:hit:{module}
TYPE: Hash
FIELDS:
  - hits: 命中次数
  - misses: 未命中次数
  - hitRate: 命中率

# 缓存性能监控
KEY: leadex:monitor:cache:performance
TYPE: Hash
FIELDS:
  - avgResponseTime: 平均响应时间
  - maxResponseTime: 最大响应时间
  - totalRequests: 总请求数
```

## 10. Redis配置建议

### 10.1 内存配置
```redis
# 最大内存限制
maxmemory 2gb

# 内存淘汰策略
maxmemory-policy allkeys-lru

# 内存使用报告
memory-usage-threshold 80
```

### 10.2 持久化配置
```redis
# RDB持久化
save 900 1
save 300 10
save 60 10000

# AOF持久化
appendonly yes
appendfsync everysec
```

### 10.3 集群配置
```redis
# 主从复制
replicaof <master-ip> <master-port>

# 哨兵模式
sentinel monitor mymaster 127.0.0.1 6379 2
sentinel down-after-milliseconds mymaster 30000
sentinel failover-timeout mymaster 180000
```