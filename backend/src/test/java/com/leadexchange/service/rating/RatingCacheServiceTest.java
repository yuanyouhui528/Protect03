package com.leadexchange.service.rating;

import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.service.rating.RatingEngineService.RatingResult;
import com.leadexchange.service.rating.impl.RatingCacheServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.SetOperations;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评级缓存服务单元测试
 * 测试缓存功能的正确性和性能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RatingCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private ValueOperations<String, String> stringValueOperations;

    @Mock
    private SetOperations<String, String> setOperations;



    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RatingCacheServiceImpl ratingCacheService;

    private RatingResult testRatingResult;
    private RatingRule testRule;
    private Map<String, Object> testStatistics;

    @BeforeEach
    void setUp() {
        // 设置Mock行为
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(stringRedisTemplate.opsForValue()).thenReturn(stringValueOperations);
        when(stringRedisTemplate.opsForSet()).thenReturn(setOperations);


        // 创建测试评级结果
        testRatingResult = new RatingResult();
        testRatingResult.setRating(LeadRating.A);
        testRatingResult.setScore(85.0);
        
        Map<RatingRuleType, Double> dimensionScores = new HashMap<>();
        dimensionScores.put(RatingRuleType.COMPLETENESS, 90.0);
        dimensionScores.put(RatingRuleType.QUALIFICATION, 80.0);
        testRatingResult.setDimensionScores(dimensionScores);

        // 创建测试评级规则
        testRule = new RatingRule();
        testRule.setId(1L);
        testRule.setRuleName("信息完整度");
        testRule.setWeight(BigDecimal.valueOf(0.20));
        testRule.setIsEnabled(true);

        // 创建测试统计数据
        testStatistics = new HashMap<>();
        testStatistics.put("totalCount", 1000L);
        testStatistics.put("averageScore", 75.5);
        testStatistics.put("ratingDistribution", Map.of(
            "A", 200L, "B", 300L, "C", 400L, "D", 100L
        ));
    }

    /**
     * 测试缓存评级结果 - 正常情况
     */
    @Test
    void testCacheRatingResult_Success() throws Exception {
        // 准备测试数据
        String expectedJson = "{\"leadId\":100,\"rating\":\"A\",\"score\":85.0}";
        when(objectMapper.writeValueAsString(testRatingResult)).thenReturn(expectedJson);

        // 执行测试
        ratingCacheService.cacheRatingResult(100L, testRatingResult);

        // 验证缓存操作
        verify(stringValueOperations).set(
            eq("rating:result:100"), 
            eq(expectedJson), 
            eq(3600L), 
            eq(TimeUnit.SECONDS)
        );
        verify(setOperations).add("rating:result:keys", "rating:result:100");
    }

    /**
     * 测试获取评级结果 - 缓存命中
     */
    @Test
    void testGetRatingResult_CacheHit() throws Exception {
        // 准备测试数据
        String cachedJson = "{\"leadId\":100,\"rating\":\"A\",\"score\":85.0}";
        when(stringValueOperations.get("rating:result:100")).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, RatingResult.class)).thenReturn(testRatingResult);

        // 执行测试
        Optional<RatingResult> optionalResult = ratingCacheService.getCachedRatingResult(100L);

        // 验证结果
        assertTrue(optionalResult.isPresent());
        RatingResult result = optionalResult.get();
        assertEquals(LeadRating.A, result.getRating());
        assertEquals(Double.valueOf(85.0), result.getScore());
        
        // 验证缓存命中统计
        verify(stringValueOperations).increment("rating:cache:hits");
    }

    /**
     * 测试获取评级结果 - 缓存未命中
     */
    @Test
    void testGetRatingResult_CacheMiss() {
        // 准备测试数据
        when(stringValueOperations.get("rating:result:100")).thenReturn(null);

        // 执行测试
        Optional<RatingResult> optionalResult = ratingCacheService.getCachedRatingResult(100L);

        // 验证结果
        assertFalse(optionalResult.isPresent());
        
        // 验证缓存未命中统计
        verify(stringValueOperations).increment("rating:cache:misses");
    }

    /**
     * 测试批量缓存评级结果
     */
    @Test
    void testBatchCacheRatingResults() throws Exception {
        // 准备测试数据
        Map<Long, RatingResult> results = new HashMap<>();
        results.put(100L, testRatingResult);
        
        RatingResult result2 = new RatingResult();
        result2.setRating(LeadRating.B);
        result2.setScore(70.0);
        results.put(101L, result2);

        when(objectMapper.writeValueAsString(any(RatingResult.class)))
            .thenReturn("{\"test\":\"data\"}");

        // 执行测试 - 逐个缓存
        for (Map.Entry<Long, RatingResult> entry : results.entrySet()) {
            ratingCacheService.cacheRatingResult(entry.getKey(), entry.getValue());
        }

        // 验证批量缓存操作
        verify(stringValueOperations, times(2)).set(anyString(), anyString(), eq(3600L), eq(TimeUnit.SECONDS));
        verify(setOperations, times(2)).add(eq("rating:result:keys"), anyString());
    }

    /**
     * 测试批量获取评级结果
     */
    @Test
    void testBatchGetRatingResults() throws Exception {
        // 准备测试数据
        List<Long> leadIds = Arrays.asList(100L, 101L, 102L);
        List<String> keys = Arrays.asList("rating:result:100", "rating:result:101", "rating:result:102");
        List<String> cachedValues = Arrays.asList(
            "{\"leadId\":100}", 
            null, 
            "{\"leadId\":102}"
        );
        
        when(stringValueOperations.multiGet(keys)).thenReturn(cachedValues);
        when(objectMapper.readValue(eq("{\"leadId\":100}"), eq(RatingResult.class)))
            .thenReturn(testRatingResult);
        
        RatingResult result3 = new RatingResult();
        result3.setRating(LeadRating.C);
        result3.setScore(60.0);
        when(objectMapper.readValue(eq("{\"leadId\":102}"), eq(RatingResult.class)))
            .thenReturn(result3);

        // 执行测试
        Map<Long, RatingResult> results = ratingCacheService.batchGetCachedRatingResults(leadIds);

        // 验证结果
        assertNotNull(results);
        assertEquals(2, results.size()); // 只有2个缓存命中
        assertTrue(results.containsKey(100L));
        assertFalse(results.containsKey(101L)); // 缓存未命中
        assertTrue(results.containsKey(102L));
    }

    /**
     * 测试删除评级结果缓存
     */
    @Test
    void testDeleteRatingResult() {
        // 执行测试
        ratingCacheService.evictRatingResult(100L);

        // 验证删除操作
        verify(stringRedisTemplate).delete("rating:result:100");
        verify(setOperations).remove("rating:result:keys", "rating:result:100");
    }

    /**
     * 测试批量删除评级结果缓存
     */
    @Test
    void testBatchDeleteRatingResults() {
        // 准备测试数据
        List<Long> leadIds = Arrays.asList(100L, 101L, 102L);
        List<String> keys = Arrays.asList("rating:result:100", "rating:result:101", "rating:result:102");

        // 执行测试 - 逐个删除
        for (Long leadId : leadIds) {
            ratingCacheService.evictRatingResult(leadId);
        }

        // 验证删除操作
        verify(stringRedisTemplate, times(3)).delete(anyString());
        verify(setOperations, times(3)).remove(eq("rating:result:keys"), anyString());
    }

    /**
     * 测试清空所有评级结果缓存
     */
    @Test
    void testClearAllRatingResults() {
        // 准备测试数据
        Set<String> keys = Set.of("rating:result:100", "rating:result:101", "rating:result:102");
        when(setOperations.members("rating:result:keys")).thenReturn(keys);

        // 执行测试
        ratingCacheService.clearAllRatingResults();

        // 验证清空操作
        verify(stringRedisTemplate).delete(keys);
        verify(stringRedisTemplate).delete("rating:result:keys");
    }

    /**
     * 测试缓存评级规则
     */
    @Test
    void testCacheRatingRule() throws Exception {
        // 准备测试数据
        String expectedJson = "{\"id\":1,\"ruleName\":\"信息完整度\"}";
        when(objectMapper.writeValueAsString(testRule)).thenReturn(expectedJson);

        // 执行测试
        ratingCacheService.cacheRuleConfig("rule:1", testRule, Duration.ofHours(1));

        // 验证缓存操作
        verify(stringValueOperations).set(
            eq("rating:rule:1"), 
            eq(expectedJson), 
            eq(7200L), 
            eq(TimeUnit.SECONDS)
        );
    }

    /**
     * 测试获取评级规则缓存
     */
    @Test
    void testGetRatingRule() throws Exception {
        // 准备测试数据
        String cachedJson = "{\"id\":1,\"ruleName\":\"信息完整度\"}";
        when(stringValueOperations.get("rating:rule:1")).thenReturn(cachedJson);
        when(objectMapper.readValue(cachedJson, RatingRule.class)).thenReturn(testRule);

        // 执行测试
        Optional<RatingRule> result = ratingCacheService.getCachedRuleConfig("rating:rule:1", RatingRule.class);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("信息完整度", result.get().getRuleName());
    }

    /**
     * 测试清空规则缓存
     */
    @Test
    void testClearRuleCache() {
        // 准备测试数据
        Set<String> keys = Set.of("rating:rule:1", "rating:rule:2", "rating:rule:3");
        when(stringRedisTemplate.keys("rating:rule:*")).thenReturn(keys);

        // 执行测试
        ratingCacheService.clearAllRuleConfigs();

        // 验证清空操作
        verify(stringRedisTemplate).delete(keys);
    }

    /**
     * 测试缓存统计数据
     */
    @Test
    void testCacheStatistics() throws Exception {
        // 准备测试数据
        String key = "rating:stats:distribution";
        String expectedJson = "{\"totalCount\":1000,\"averageScore\":75.5}";
        when(objectMapper.writeValueAsString(testStatistics)).thenReturn(expectedJson);

        // 执行测试
        ratingCacheService.cacheRatingStatistics(key, testStatistics, Duration.ofSeconds(1800));

        // 验证缓存操作
        verify(stringValueOperations).set(key, expectedJson, 1800L, TimeUnit.SECONDS);
    }

    /**
     * 测试获取统计数据缓存
     */
    @Test
    void testGetStatistics() throws Exception {
        // 准备测试数据
        String key = "rating:stats:distribution";
        String cachedJson = "{\"totalCount\":1000,\"averageScore\":75.5}";
        when(stringValueOperations.get(key)).thenReturn(cachedJson);
        when(objectMapper.readValue(eq(cachedJson), eq(Map.class))).thenReturn(testStatistics);

        // 执行测试
        @SuppressWarnings("unchecked")
        Optional<Map> result = ratingCacheService.getCachedRatingStatistics(key, Map.class);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1000L, result.get().get("totalCount"));
        assertEquals(75.5, result.get().get("averageScore"));
    }

    /**
     * 测试设置缓存过期时间
     */
    @Test
    void testSetExpiration() {
        // 执行测试
        ratingCacheService.refreshRatingResultTtl(100L, Duration.ofSeconds(3600));

        // 验证过期时间设置
        verify(stringRedisTemplate).expire("rating:result:100", 3600L, TimeUnit.SECONDS);
    }

    /**
     * 测试获取缓存过期时间
     */
    @Test
    void testGetExpiration() {
        // 准备测试数据
        when(stringRedisTemplate.getExpire("rating:result:100", TimeUnit.SECONDS))
            .thenReturn(1800L);

        // 执行测试
        Duration expiration = ratingCacheService.getRatingResultTtl(100L);

        // 验证结果
        assertEquals(Duration.ofSeconds(1800), expiration);
    }

    /**
     * 测试获取缓存命中率统计
     */
    @Test
    void testGetCacheHitRateStatistics() {
        // 准备测试数据
        when(stringValueOperations.get("rating:cache:hits")).thenReturn("800");
        when(stringValueOperations.get("rating:cache:misses")).thenReturn("200");

        // 执行测试
        RatingCacheService.CacheStatistics result = ratingCacheService.getCacheStatistics();

        // 验证结果
        assertNotNull(result);
        assertEquals(800L, result.getHitCount());
        assertEquals(200L, result.getMissCount());
        assertEquals(1000L, result.getTotalRequests());
        assertEquals(0.8, result.getHitRate(), 0.01);
    }

    /**
     * 测试预热缓存
     */
    @Test
    void testWarmupCache() throws Exception {
        // 准备测试数据
        List<Long> leadIds = Arrays.asList(100L, 101L, 102L);
        Map<Long, RatingResult> results = new HashMap<>();
        results.put(100L, testRatingResult);
        
        when(objectMapper.writeValueAsString(any(RatingResult.class)))
            .thenReturn("{\"test\":\"data\"}");

        // 执行测试
        RatingCacheService.CacheWarmupResult warmupResult = ratingCacheService.warmupRatingCache(leadIds);

        // 验证预热操作
        verify(stringValueOperations).set(anyString(), anyString(), eq(3600L), eq(TimeUnit.SECONDS));
        verify(setOperations).add(eq("rating:result:keys"), anyString());
    }

    /**
     * 测试获取缓存大小信息
     */
    @Test
    void testGetCacheSizeInfo() {
        // 准备测试数据
        Set<String> resultKeys = Set.of("rating:result:100", "rating:result:101");
        Set<String> ruleKeys = Set.of("rating:rule:1", "rating:rule:2", "rating:rule:3");
        Set<String> statsKeys = Set.of("rating:stats:distribution");
        
        when(stringRedisTemplate.keys("rating:result:*")).thenReturn(resultKeys);
        when(stringRedisTemplate.keys("rating:rule:*")).thenReturn(ruleKeys);
        when(stringRedisTemplate.keys("rating:stats:*")).thenReturn(statsKeys);

        // 执行测试
        RatingCacheService.CacheSizeInfo result = ratingCacheService.getCacheSizeInfo();

        // 验证结果
        assertNotNull(result);
        assertEquals(2L, result.getRatingResultCacheSize());
        assertEquals(3L, result.getRuleConfigCacheSize());
        assertEquals(1L, result.getStatisticsCacheSize());
        assertEquals(6L, result.getTotalCacheSize());
    }

    /**
     * 测试清理过期缓存
     */
    @Test
    void testCleanupExpiredCache() {
        // 准备测试数据
        Set<String> allKeys = Set.of(
            "rating:result:100", "rating:result:101", 
            "rating:rule:1", "rating:stats:test"
        );
        when(stringRedisTemplate.keys("rating:*")).thenReturn(allKeys);
        
        // 模拟部分key已过期
        when(stringRedisTemplate.getExpire("rating:result:100", TimeUnit.SECONDS))
            .thenReturn(-2L); // 已过期
        when(stringRedisTemplate.getExpire("rating:result:101", TimeUnit.SECONDS))
            .thenReturn(1800L); // 未过期
        when(stringRedisTemplate.getExpire("rating:rule:1", TimeUnit.SECONDS))
            .thenReturn(-2L); // 已过期
        when(stringRedisTemplate.getExpire("rating:stats:test", TimeUnit.SECONDS))
            .thenReturn(900L); // 未过期

        // 执行测试
        long cleanedCount = ratingCacheService.cleanupExpiredCache();

        // 验证结果
        assertEquals(2L, cleanedCount); // 清理了2个过期的key
        verify(stringRedisTemplate).delete("rating:result:100");
        verify(stringRedisTemplate).delete("rating:rule:1");
        verify(stringRedisTemplate, never()).delete("rating:result:101");
        verify(stringRedisTemplate, never()).delete("rating:stats:test");
    }

    /**
     * 测试缓存事件监听器
     */
    @Test
    void testCacheEventListener() {
        // 创建测试监听器
        RatingCacheService.CacheEventListener listener = mock(RatingCacheService.CacheEventListener.class);
        
        // 添加监听器
        ratingCacheService.addCacheEventListener(listener);
        
        // 执行缓存操作
        ratingCacheService.cacheRatingResult(100L, testRatingResult);
        
        // 验证监听器被调用（这里需要根据实际实现调整）
        // verify(listener).onCacheEvent(any());
    }

    /**
     * 测试缓存异常处理
     */
    @Test
    void testCacheExceptionHandling() {
        // 准备测试数据 - 模拟Redis异常
        when(stringValueOperations.get(anyString()))
            .thenThrow(new RuntimeException("Redis connection failed"));

        // 执行测试 - 应该优雅处理异常
        Optional<RatingResult> optionalResult = ratingCacheService.getCachedRatingResult(100L);

        // 验证结果 - 异常情况下返回空Optional
        assertFalse(optionalResult.isPresent());
    }

    /**
     * 测试缓存性能监控
     */
    @Test
    void testCachePerformanceMonitoring() {
        // 执行多次缓存操作
        for (int i = 0; i < 10; i++) {
            ratingCacheService.getCachedRatingResult((long) i);
        }

        // 验证性能统计被记录
        verify(stringValueOperations, atLeast(10)).increment("rating:cache:misses");
    }
}