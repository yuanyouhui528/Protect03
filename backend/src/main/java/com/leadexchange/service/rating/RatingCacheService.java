package com.leadexchange.service.rating;

import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.service.rating.RatingEngineService.RatingResult;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 评级缓存服务接口
 * 提供评级结果的缓存管理功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface RatingCacheService {

    /**
     * 缓存评级结果
     * 
     * @param leadId 线索ID
     * @param ratingResult 评级结果
     * @param ttl 缓存过期时间
     */
    void cacheRatingResult(Long leadId, RatingResult ratingResult, Duration ttl);

    /**
     * 缓存评级结果（使用默认过期时间）
     * 
     * @param leadId 线索ID
     * @param ratingResult 评级结果
     */
    void cacheRatingResult(Long leadId, RatingResult ratingResult);

    /**
     * 获取缓存的评级结果
     * 
     * @param leadId 线索ID
     * @return 评级结果
     */
    Optional<RatingResult> getCachedRatingResult(Long leadId);

    /**
     * 批量获取缓存的评级结果
     * 
     * @param leadIds 线索ID列表
     * @return 线索ID和评级结果的映射
     */
    Map<Long, RatingResult> batchGetCachedRatingResults(List<Long> leadIds);

    /**
     * 删除评级结果缓存
     * 
     * @param leadId 线索ID
     */
    void evictRatingResult(Long leadId);

    /**
     * 批量删除评级结果缓存
     * 
     * @param leadIds 线索ID列表
     */
    void batchEvictRatingResults(List<Long> leadIds);

    /**
     * 清空所有评级结果缓存
     */
    void clearAllRatingResults();

    /**
     * 检查评级结果是否已缓存
     * 
     * @param leadId 线索ID
     * @return 是否已缓存
     */
    boolean isRatingResultCached(Long leadId);

    /**
     * 获取缓存的评级结果剩余过期时间
     * 
     * @param leadId 线索ID
     * @return 剩余过期时间
     */
    Duration getRatingResultTtl(Long leadId);

    /**
     * 刷新评级结果缓存过期时间
     * 
     * @param leadId 线索ID
     * @param ttl 新的过期时间
     */
    void refreshRatingResultTtl(Long leadId, Duration ttl);

    /**
     * 缓存评级规则配置
     * 
     * @param ruleConfigKey 规则配置键
     * @param ruleConfig 规则配置
     * @param ttl 缓存过期时间
     */
    void cacheRuleConfig(String ruleConfigKey, Object ruleConfig, Duration ttl);

    /**
     * 获取缓存的评级规则配置
     * 
     * @param ruleConfigKey 规则配置键
     * @param clazz 配置类型
     * @return 规则配置
     */
    <T> Optional<T> getCachedRuleConfig(String ruleConfigKey, Class<T> clazz);

    /**
     * 删除评级规则配置缓存
     * 
     * @param ruleConfigKey 规则配置键
     */
    void evictRuleConfig(String ruleConfigKey);

    /**
     * 清空所有评级规则配置缓存
     */
    void clearAllRuleConfigs();

    /**
     * 缓存评级统计数据
     * 
     * @param statisticsKey 统计数据键
     * @param statistics 统计数据
     * @param ttl 缓存过期时间
     */
    void cacheRatingStatistics(String statisticsKey, Object statistics, Duration ttl);

    /**
     * 缓存评级统计数据（使用默认过期时间）
     * 
     * @param statisticsKey 统计数据键
     * @param statistics 统计数据
     */
    void cacheRatingStatistics(String statisticsKey, Object statistics);

    /**
     * 获取缓存的评级统计数据
     * 
     * @param statisticsKey 统计数据键
     * @param clazz 统计数据类型
     * @return 统计数据
     */
    <T> Optional<T> getCachedRatingStatistics(String statisticsKey, Class<T> clazz);

    /**
     * 删除评级统计数据缓存
     * 
     * @param statisticsKey 统计数据键
     */
    void evictRatingStatistics(String statisticsKey);

    /**
     * 清空所有评级统计数据缓存
     */
    void clearAllRatingStatistics();

    /**
     * 获取缓存命中率统计
     * 
     * @return 缓存统计信息
     */
    CacheStatistics getCacheStatistics();

    /**
     * 预热评级结果缓存
     * 为指定的线索ID列表预先计算并缓存评级结果
     * 
     * @param leadIds 线索ID列表
     * @return 预热结果
     */
    CacheWarmupResult warmupRatingCache(List<Long> leadIds);

    /**
     * 预热评级规则配置缓存
     * 预先加载所有评级规则配置到缓存
     * 
     * @return 预热结果
     */
    CacheWarmupResult warmupRuleConfigCache();

    /**
     * 获取所有缓存的线索ID
     * 
     * @return 线索ID集合
     */
    Set<Long> getAllCachedLeadIds();

    /**
     * 获取缓存大小信息
     * 
     * @return 缓存大小信息
     */
    CacheSizeInfo getCacheSizeInfo();

    /**
     * 清理过期的缓存
     * 
     * @return 清理的缓存数量
     */
    int cleanupExpiredCache();

    /**
     * 设置缓存监听器
     * 
     * @param listener 缓存事件监听器
     */
    void setCacheEventListener(CacheEventListener listener);

    /**
     * 添加缓存事件监听器
     * 
     * @param listener 缓存事件监听器
     */
    void addCacheEventListener(CacheEventListener listener);

    /**
     * 移除缓存事件监听器
     * 
     * @param listener 缓存事件监听器
     */
    void removeCacheEventListener(CacheEventListener listener);

    /**
     * 导出缓存数据
     * 
     * @return 缓存数据快照
     */
    CacheSnapshot exportCacheData();

    /**
     * 导入缓存数据
     * 
     * @param snapshot 缓存数据快照
     * @return 导入结果
     */
    CacheImportResult importCacheData(CacheSnapshot snapshot);

    /**
     * 获取缓存命中率统计
     * 
     * @return 缓存命中率统计信息
     */
    CacheHitRateStatistics getCacheHitRateStatistics();

    /**
     * 预热缓存
     * 
     * @return 预热结果
     */
    CacheWarmupResult warmupCache();

    /**
     * 预热评级结果缓存
     * 
     * @param leadIds 线索ID列表
     * @return 预热结果
     */
    CacheWarmupResult warmupRatingResults(List<Long> leadIds);

    /**
     * 预热评级规则缓存
     * 
     * @return 预热结果
     */
    CacheWarmupResult warmupRatingRules();



    /**
     * 缓存命中率统计信息内部类
     */
    class CacheHitRateStatistics {
        private CacheTypeStatistics ratingResultStats;
        private CacheTypeStatistics ratingRulesStats;
        private CacheTypeStatistics ratingStatsStats;
        private double overallHitRate;
        private long totalHits;
        private long totalMisses;
        private long totalRequests;
        private java.time.LocalDateTime statisticsTime;

        public CacheHitRateStatistics() {}

        public CacheTypeStatistics getRatingResultStats() { return ratingResultStats; }
        public void setRatingResultStats(CacheTypeStatistics ratingResultStats) { this.ratingResultStats = ratingResultStats; }
        public CacheTypeStatistics getRatingRulesStats() { return ratingRulesStats; }
        public void setRatingRulesStats(CacheTypeStatistics ratingRulesStats) { this.ratingRulesStats = ratingRulesStats; }
        public CacheTypeStatistics getRatingStatsStats() { return ratingStatsStats; }
        public void setRatingStatsStats(CacheTypeStatistics ratingStatsStats) { this.ratingStatsStats = ratingStatsStats; }
        public double getOverallHitRate() { return overallHitRate; }
        public void setOverallHitRate(double overallHitRate) { this.overallHitRate = overallHitRate; }
        public long getTotalHits() { return totalHits; }
        public void setTotalHits(long totalHits) { this.totalHits = totalHits; }
        public long getTotalMisses() { return totalMisses; }
        public void setTotalMisses(long totalMisses) { this.totalMisses = totalMisses; }
        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        public java.time.LocalDateTime getStatisticsTime() { return statisticsTime; }
        public void setStatisticsTime(java.time.LocalDateTime statisticsTime) { this.statisticsTime = statisticsTime; }
    }

    /**
     * 缓存类型统计信息内部类
     */
    class CacheTypeStatistics {
        private String type;
        private long hits;
        private long misses;
        private long total;
        private double hitRate;

        public CacheTypeStatistics() {}

        public CacheTypeStatistics(String type, long hits, long misses, long total, double hitRate) {
            this.type = type;
            this.hits = hits;
            this.misses = misses;
            this.total = total;
            this.hitRate = hitRate;
        }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public long getHits() { return hits; }
        public void setHits(long hits) { this.hits = hits; }
        public long getMisses() { return misses; }
        public void setMisses(long misses) { this.misses = misses; }
        public long getTotal() { return total; }
        public void setTotal(long total) { this.total = total; }
        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }
    }

    /**
     * 缓存统计信息内部类
     */
    class CacheStatistics {
        private long totalRequests;
        private long hitCount;
        private long missCount;
        private double hitRate;
        private long evictionCount;
        private long loadCount;
        private double averageLoadTime;
        private long cacheSize;
        private java.time.LocalDateTime lastResetTime;

        public CacheStatistics() {}

        public long getTotalRequests() { return totalRequests; }
        public void setTotalRequests(long totalRequests) { this.totalRequests = totalRequests; }
        public long getHitCount() { return hitCount; }
        public void setHitCount(long hitCount) { this.hitCount = hitCount; }
        public long getMissCount() { return missCount; }
        public void setMissCount(long missCount) { this.missCount = missCount; }
        public double getHitRate() { return hitRate; }
        public void setHitRate(double hitRate) { this.hitRate = hitRate; }
        public long getEvictionCount() { return evictionCount; }
        public void setEvictionCount(long evictionCount) { this.evictionCount = evictionCount; }
        public long getLoadCount() { return loadCount; }
        public void setLoadCount(long loadCount) { this.loadCount = loadCount; }
        public double getAverageLoadTime() { return averageLoadTime; }
        public void setAverageLoadTime(double averageLoadTime) { this.averageLoadTime = averageLoadTime; }
        public long getCacheSize() { return cacheSize; }
        public void setCacheSize(long cacheSize) { this.cacheSize = cacheSize; }
        public java.time.LocalDateTime getLastResetTime() { return lastResetTime; }
        public void setLastResetTime(java.time.LocalDateTime lastResetTime) { this.lastResetTime = lastResetTime; }
    }

    /**
     * 缓存预热结果内部类
     */
    class CacheWarmupResult {
        private int totalCount;
        private int successCount;
        private int failureCount;
        private long executionTime;
        private List<String> errors;

        public CacheWarmupResult() {}

        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
    }

    /**
     * 缓存大小信息内部类
     */
    class CacheSizeInfo {
        private long ratingResultCacheSize;
        private long ruleConfigCacheSize;
        private long statisticsCacheSize;
        private long totalCacheSize;
        private String memorySizeFormatted;

        public CacheSizeInfo() {}

        public long getRatingResultCacheSize() { return ratingResultCacheSize; }
        public void setRatingResultCacheSize(long ratingResultCacheSize) { this.ratingResultCacheSize = ratingResultCacheSize; }
        public long getRuleConfigCacheSize() { return ruleConfigCacheSize; }
        public void setRuleConfigCacheSize(long ruleConfigCacheSize) { this.ruleConfigCacheSize = ruleConfigCacheSize; }
        public long getStatisticsCacheSize() { return statisticsCacheSize; }
        public void setStatisticsCacheSize(long statisticsCacheSize) { this.statisticsCacheSize = statisticsCacheSize; }
        public long getTotalCacheSize() { return totalCacheSize; }
        public void setTotalCacheSize(long totalCacheSize) { this.totalCacheSize = totalCacheSize; }
        public String getMemorySizeFormatted() { return memorySizeFormatted; }
        public void setMemorySizeFormatted(String memorySizeFormatted) { this.memorySizeFormatted = memorySizeFormatted; }
    }

    /**
     * 缓存事件监听器接口
     */
    interface CacheEventListener {
        /**
         * 缓存命中事件
         * 
         * @param key 缓存键
         * @param value 缓存值
         */
        void onCacheHit(String key, Object value);

        /**
         * 缓存未命中事件
         * 
         * @param key 缓存键
         */
        void onCacheMiss(String key);

        /**
         * 缓存加载事件
         * 
         * @param key 缓存键
         * @param value 加载的值
         * @param loadTime 加载时间（毫秒）
         */
        void onCacheLoad(String key, Object value, long loadTime);

        /**
         * 缓存驱逐事件
         * 
         * @param key 缓存键
         * @param value 被驱逐的值
         * @param cause 驱逐原因
         */
        void onCacheEviction(String key, Object value, String cause);
    }

    /**
     * 缓存数据快照内部类
     */
    class CacheSnapshot {
        private Map<Long, RatingEngineService.RatingResult> ratingResults;
        private Map<String, Object> ruleConfigs;
        private Map<String, Object> statistics;
        private java.time.LocalDateTime snapshotTime;
        private String version;

        public CacheSnapshot() {}

        public Map<Long, RatingEngineService.RatingResult> getRatingResults() { return ratingResults; }
        public void setRatingResults(Map<Long, RatingEngineService.RatingResult> ratingResults) { this.ratingResults = ratingResults; }
        public Map<String, Object> getRuleConfigs() { return ruleConfigs; }
        public void setRuleConfigs(Map<String, Object> ruleConfigs) { this.ruleConfigs = ruleConfigs; }
        public Map<String, Object> getStatistics() { return statistics; }
        public void setStatistics(Map<String, Object> statistics) { this.statistics = statistics; }
        public java.time.LocalDateTime getSnapshotTime() { return snapshotTime; }
        public void setSnapshotTime(java.time.LocalDateTime snapshotTime) { this.snapshotTime = snapshotTime; }
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
    }

    /**
     * 缓存导入结果内部类
     */
    class CacheImportResult {
        private int totalItems;
        private int successItems;
        private int failureItems;
        private int skippedItems;
        private List<String> errors;
        private long executionTime;

        public CacheImportResult() {}

        public int getTotalItems() { return totalItems; }
        public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
        public int getSuccessItems() { return successItems; }
        public void setSuccessItems(int successItems) { this.successItems = successItems; }
        public int getFailureItems() { return failureItems; }
        public void setFailureItems(int failureItems) { this.failureItems = failureItems; }
        public int getSkippedItems() { return skippedItems; }
        public void setSkippedItems(int skippedItems) { this.skippedItems = skippedItems; }
        public List<String> getErrors() { return errors; }
        public void setErrors(List<String> errors) { this.errors = errors; }
        public long getExecutionTime() { return executionTime; }
        public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
    }
}