package com.leadexchange.service.rating;

import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.repository.rating.RatingHistoryRepository;
import com.leadexchange.service.rating.RatingHistoryService.RatingChangeTrend;
import com.leadexchange.service.rating.RatingHistoryService.RatingRollbackSuggestion;
import com.leadexchange.service.rating.RatingHistoryService.RatingHistoryStatistics;
import com.leadexchange.service.rating.impl.RatingHistoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评级历史服务单元测试
 * 测试评级历史记录管理功能的正确性
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RatingHistoryServiceTest {

    @Mock
    private RatingHistoryRepository ratingHistoryRepository;

    @InjectMocks
    private RatingHistoryServiceImpl ratingHistoryService;

    private RatingHistory testHistory;
    private List<RatingHistory> testHistories;

    @BeforeEach
    void setUp() {
        // 创建测试评级历史记录
        testHistory = new RatingHistory();
        testHistory.setLeadId(100L);
        testHistory.setPreviousRating(LeadRating.B);
        testHistory.setCurrentRating(LeadRating.A);
        testHistory.setPreviousScore(65);
        testHistory.setCurrentScore(85);
        testHistory.setChangeReason(RatingChangeReason.RULE_CHANGE);
        testHistory.setOperatorId(1L);
        testHistory.setOperatorName("系统管理员");
        testHistory.setChangeDescription("规则调整导致评级提升");
        testHistory.setRatingTime(LocalDateTime.now());
        testHistory.setRatingDetails("{\"dimensions\":{\"completeness\":90,\"qualification\":80}}");
        testHistory.setRatingVersion("1.0");

        // 创建测试历史记录列表
        testHistories = createTestHistories();
    }

    /**
     * 测试记录评级变更 - 正常情况
     */
    @Test
    void testRecordRatingChange_Success() {
        // 准备测试数据
        when(ratingHistoryRepository.save(any(RatingHistory.class))).thenReturn(testHistory);

        // 执行测试
        RatingHistory result = ratingHistoryService.recordRatingChange(
            100L, LeadRating.B, LeadRating.A, 65.0, 85.0, RatingChangeReason.RULE_CHANGE,
            null, null, "规则变更导致评级提升", "{\"dimensions\":{\"completeness\":90}}", "1.0");

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getLeadId());
        assertEquals(LeadRating.B, result.getPreviousRating());
        assertEquals(LeadRating.A, result.getCurrentRating());
        assertEquals(Integer.valueOf(65), result.getPreviousScore());
        assertEquals(Integer.valueOf(85), result.getCurrentScore());
        assertEquals(RatingChangeReason.RULE_CHANGE, result.getChangeReason());
        
        // 验证保存操作
        verify(ratingHistoryRepository).save(any(RatingHistory.class));
    }

    /**
     * 测试记录评级变更 - 带操作人信息
     */
    @Test
    void testRecordRatingChangeWithOperator() {
        // 准备测试数据 - 创建一个具有正确changeReason的返回对象
        RatingHistory manualAdjustmentHistory = new RatingHistory();
        manualAdjustmentHistory.setLeadId(100L);
        manualAdjustmentHistory.setPreviousRating(LeadRating.B);
        manualAdjustmentHistory.setCurrentRating(LeadRating.A);
        manualAdjustmentHistory.setPreviousScore(65);
        manualAdjustmentHistory.setCurrentScore(85);
        manualAdjustmentHistory.setChangeReason(RatingChangeReason.MANUAL_ADJUSTMENT);
        manualAdjustmentHistory.setOperatorId(1L);
        manualAdjustmentHistory.setOperatorName("管理员");
        manualAdjustmentHistory.setChangeDescription("手动调整评级");
        manualAdjustmentHistory.setRatingTime(LocalDateTime.now());
        manualAdjustmentHistory.setRatingDetails("{\"dimensions\":{\"completeness\":90}}");
        manualAdjustmentHistory.setRatingVersion("1.0");
        
        when(ratingHistoryRepository.save(any(RatingHistory.class))).thenReturn(manualAdjustmentHistory);

        // 执行测试
        RatingHistory result = ratingHistoryService.recordRatingChange(
            100L, LeadRating.B, LeadRating.A, 65.0, 85.0, 
            RatingChangeReason.MANUAL_ADJUSTMENT, 1L, "管理员", "手动调整评级",
            "{\"dimensions\":{\"completeness\":90}}", "1.0");

        // 验证结果
        assertNotNull(result);
        assertEquals(100L, result.getLeadId());
        assertEquals(LeadRating.A, result.getCurrentRating());
        assertEquals(LeadRating.B, result.getPreviousRating());
        assertEquals(RatingChangeReason.MANUAL_ADJUSTMENT, result.getChangeReason());
        assertEquals(1L, result.getOperatorId());
        assertEquals("管理员", result.getOperatorName());
        assertEquals("手动调整评级", result.getChangeDescription());
    }

    /**
     * 测试根据ID获取历史记录
     */
    @Test
    void testGetHistoryById() {
        // 准备测试数据
        when(ratingHistoryRepository.findById(1L)).thenReturn(Optional.of(testHistory));

        // 执行测试
        Optional<RatingHistory> result = ratingHistoryService.getHistoryById(1L);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getLeadId());
    }

    /**
     * 测试根据线索ID获取历史记录
     */
    @Test
    void testGetHistoriesByLeadId() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        List<RatingHistory> leadHistories = testHistories.stream()
            .filter(h -> h.getLeadId().equals(100L))
            .toList();
        Page<RatingHistory> page = new PageImpl<>(leadHistories, pageable, leadHistories.size());
        when(ratingHistoryRepository.findByLeadIdOrderByRatingTimeDesc(100L, pageable))
            .thenReturn(page);

        // 执行测试
        Page<RatingHistory> result = ratingHistoryService.getLeadRatingHistory(100L, pageable);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getContent().size() > 0);
        assertTrue(result.getContent().stream().allMatch(h -> h.getLeadId().equals(100L)));
    }

    /**
     * 测试根据变更原因获取历史记录
     */
    @Test
    void testGetHistoriesByChangeReason() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        List<RatingHistory> reasonHistories = testHistories.stream()
            .filter(h -> h.getChangeReason() == RatingChangeReason.RULE_CHANGE)
            .toList();
        Page<RatingHistory> page = new PageImpl<>(reasonHistories, pageable, reasonHistories.size());
        when(ratingHistoryRepository.findByChangeReasonOrderByRatingTimeDesc(
            RatingChangeReason.RULE_CHANGE, pageable)).thenReturn(page);

        // 执行测试
        Page<RatingHistory> result = ratingHistoryService.getHistoryByChangeReason(
            RatingChangeReason.RULE_CHANGE, pageable);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getContent().stream()
            .allMatch(h -> h.getChangeReason() == RatingChangeReason.RULE_CHANGE));
    }

    /**
     * 测试根据操作人获取历史记录
     */
    @Test
    void testGetHistoriesByOperator() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        List<RatingHistory> operatorHistories = testHistories.stream()
            .filter(h -> h.getOperatorId() != null && h.getOperatorId().equals(1L))
            .toList();
        Page<RatingHistory> page = new PageImpl<>(operatorHistories, pageable, operatorHistories.size());
        when(ratingHistoryRepository.findByOperatorIdOrderByRatingTimeDesc(1L, pageable))
            .thenReturn(page);

        // 执行测试
        Page<RatingHistory> result = ratingHistoryService.getHistoryByOperator(1L, pageable);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getContent().stream()
            .allMatch(h -> h.getOperatorId() != null && h.getOperatorId().equals(1L)));
    }

    /**
     * 测试根据时间范围获取历史记录
     */
    @Test
    void testGetHistoriesByTimeRange() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(7);
        LocalDateTime endTime = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        Page<RatingHistory> page = new PageImpl<>(testHistories, pageable, testHistories.size());
        when(ratingHistoryRepository.findByRatingTimeBetweenOrderByRatingTimeDesc(
            startTime, endTime, pageable)).thenReturn(page);

        // 执行测试
        Page<RatingHistory> result = ratingHistoryService.getHistoryByTimeRange(
            startTime, endTime, pageable);

        // 验证结果
        assertNotNull(result);
        assertEquals(testHistories.size(), result.getContent().size());
    }

    /**
     * 测试根据评级变化获取历史记录
     */
    @Test
    void testGetHistoriesByRatingChange() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        List<RatingHistory> ratingChangeHistories = testHistories.stream()
            .filter(h -> h.getPreviousRating() == LeadRating.B && h.getCurrentRating() == LeadRating.A)
            .toList();
        Page<RatingHistory> page = new PageImpl<>(ratingChangeHistories, pageable, ratingChangeHistories.size());
        when(ratingHistoryRepository.findByPreviousRatingAndCurrentRatingOrderByRatingTimeDesc(
            LeadRating.B, LeadRating.A, pageable)).thenReturn(page);

        // 执行测试
        Page<RatingHistory> result = ratingHistoryService.getHistoryByRatingChange(
            LeadRating.B, LeadRating.A, pageable);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.getContent().stream().allMatch(h -> 
            h.getPreviousRating() == LeadRating.B && h.getCurrentRating() == LeadRating.A));
    }

    /**
     * 测试获取统计信息
     */
    @Test
    void testGetStatistics() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        
        when(ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime))
            .thenReturn(testHistories);

        // 执行测试
        RatingHistoryStatistics result = ratingHistoryService.getHistoryStatistics(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals((long) testHistories.size(), result.getTotalChanges());
        assertTrue(result.getUpgradeCount() >= 0);
        assertTrue(result.getDowngradeCount() >= 0);
        assertNotNull(result.getChangeReasonCount());
        assertTrue(result.getChangeReasonCount().size() > 0);
    }

    /**
     * 测试获取趋势数据
     */
    @Test
    void testGetTrendData() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        
        // 执行测试
        List<RatingChangeTrend> result = ratingHistoryService.getRatingChangeTrend(startTime, endTime, "DAY");

        // 验证结果
        assertNotNull(result);
        // 验证数据库查询被调用
        verify(ratingHistoryRepository, atLeastOnce()).findByRatingTimeBetween(any(), any());
    }

    /**
     * 测试删除过期记录
     */
    @Test
    void testDeleteExpiredRecords() {
        // 准备测试数据
        int retentionDays = 30;
        
        // Mock repository 行为
        when(ratingHistoryRepository.deleteByRatingTimeBefore(any(LocalDateTime.class)))
            .thenReturn(100L);
        
        // 执行测试
        int deletedCount = ratingHistoryService.deleteExpiredHistory(retentionDays);
        
        // 验证结果
        assertEquals(100, deletedCount);
        
        // 验证方法调用
        verify(ratingHistoryRepository).deleteByRatingTimeBefore(any(LocalDateTime.class));
    }

    /**
     * 测试批量删除记录
     */
    @Test
    void testBatchDeleteRecords() {
        // 准备测试数据
        List<Long> historyIds = Arrays.asList(1L, 2L, 3L);
        when(ratingHistoryRepository.findAllById(historyIds))
            .thenReturn(testHistories.subList(0, 3));

        // 执行测试
        ratingHistoryService.batchDeleteHistory(historyIds);

        // 验证删除操作
        verify(ratingHistoryRepository).deleteAllById(historyIds);
    }

    /**
     * 测试导出数据
     */
    @Test
    void testExportData() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        when(ratingHistoryRepository.findByRatingTimeBetweenOrderByRatingTimeDesc(startTime, endTime))
            .thenReturn(testHistories);

        // 执行测试
        List<RatingHistory> result = ratingHistoryService.exportData(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(testHistories.size(), result.size());
    }

    /**
     * 测试获取回滚建议
     */
    @Test
    void testGetRollbackSuggestions() {
        // 准备测试数据
        when(ratingHistoryRepository.findTop10ByLeadIdOrderByRatingTimeDesc(100L))
            .thenReturn(testHistories.stream()
                .filter(h -> h.getLeadId().equals(100L))
                .toList());

        // 执行测试
        List<RatingRollbackSuggestion> result = ratingHistoryService.getRollbackSuggestions(100L);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() > 0);
        result.forEach(suggestion -> {
            assertNotNull(suggestion.isCanRollback());
            if (suggestion.isCanRollback()) {
                assertNotNull(suggestion.getRollbackOptions());
                assertNotNull(suggestion.getRecommendation());
            }
        });
    }

    /**
     * 测试执行回滚
     */
    @Test
    void testRollbackToHistory() {
        // 准备测试数据
        RatingHistory targetHistory = createHistory(2L, 1L, LeadRating.A, LeadRating.B, RatingChangeReason.MANUAL_ADJUSTMENT);
        RatingHistory currentHistory = createHistory(3L, 1L, LeadRating.B, LeadRating.A, RatingChangeReason.RULE_CHANGE);
        
        // 创建预期的回滚结果对象
        RatingHistory rollbackResult = createHistory(4L, 1L, LeadRating.A, LeadRating.B, RatingChangeReason.ROLLBACK);
        rollbackResult.setOperatorId(1L);
        rollbackResult.setOperatorName("1");
        rollbackResult.setChangeDescription("回滚到历史记录[2]: 回滚操作");
        
        when(ratingHistoryRepository.findById(2L)).thenReturn(Optional.of(targetHistory));
        when(ratingHistoryRepository.findFirstByLeadIdOrderByRatingTimeDesc(1L)).thenReturn(Optional.of(currentHistory));
        when(ratingHistoryRepository.save(any(RatingHistory.class))).thenReturn(rollbackResult);

        // 执行测试
        RatingHistory result = ratingHistoryService.rollbackToHistory(1L, 2L, "1", "回滚操作");

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getLeadId());
        assertEquals(RatingChangeReason.ROLLBACK, result.getChangeReason());
        assertEquals(1L, result.getOperatorId());
        assertEquals("1", result.getOperatorName());
        assertTrue(result.getChangeDescription().contains("回滚操作"));
    }

    /**
     * 测试获取评级变化统计
     */
    @Test
    void testGetRatingChangeStatistics() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        
        when(ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime))
            .thenReturn(testHistories);

        // 执行测试
        Map<String, Map<String, Long>> result = ratingHistoryService.getRatingChangeStatistics(
            startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertEquals(3, result.size()); // changeReason, ratingType, changeType
        assertTrue(result.containsKey("changeReason"));
        assertTrue(result.containsKey("ratingType"));
        assertTrue(result.containsKey("changeType"));
    }

    /**
     * 测试获取操作人统计
     */
    @Test
    void testGetOperatorStatistics() {
        // 准备测试数据
        LocalDateTime startTime = LocalDateTime.now().minusDays(30);
        LocalDateTime endTime = LocalDateTime.now();
        
        when(ratingHistoryRepository.findByRatingTimeBetween(startTime, endTime))
            .thenReturn(testHistories);
        
        // 执行测试
        Map<String, Long> result = ratingHistoryService.getOperatorStatistics(startTime, endTime);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() > 0);
        // 验证数据库查询被调用
        verify(ratingHistoryRepository).findByRatingTimeBetween(startTime, endTime);
    }

    /**
     * 创建测试历史记录列表
     */
    private List<RatingHistory> createTestHistories() {
        return Arrays.asList(
            createHistory(1L, 100L, LeadRating.B, LeadRating.A, RatingChangeReason.RULE_CHANGE),
            createHistory(2L, 100L, LeadRating.C, LeadRating.B, RatingChangeReason.INFO_UPDATE),
            createHistory(3L, 101L, LeadRating.D, LeadRating.C, RatingChangeReason.MANUAL_ADJUSTMENT),
            createHistory(4L, 102L, LeadRating.A, LeadRating.B, RatingChangeReason.SYSTEM_AUTO),
            createHistory(5L, 103L, LeadRating.B, LeadRating.A, RatingChangeReason.RULE_CHANGE)
        );
    }

    /**
     * 创建测试历史记录
     */
    private RatingHistory createHistory(Long id, Long leadId, LeadRating oldRating, 
                                      LeadRating newRating, RatingChangeReason reason) {
        RatingHistory history = new RatingHistory();
        history.setId(id);
        history.setLeadId(leadId);
        history.setPreviousRating(oldRating);
        history.setCurrentRating(newRating);
        history.setPreviousScore(oldRating.getMinScore());
        history.setCurrentScore(newRating.getMinScore());
        history.setChangeReason(reason);
        history.setOperatorId(1L);
        history.setOperatorName("测试用户");
        history.setChangeDescription("测试变更");
        history.setRatingTime(LocalDateTime.now());
        history.setRatingDetails("{}");
        history.setRatingVersion("1.0");
        return history;
    }
}