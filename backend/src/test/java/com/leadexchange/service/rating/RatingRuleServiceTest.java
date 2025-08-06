package com.leadexchange.service.rating;

import com.leadexchange.common.result.PageResult;
import com.leadexchange.domain.rating.CalculationMethod;
import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.repository.rating.RatingRuleRepository;
import com.leadexchange.service.rating.impl.RatingRuleServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 评级规则服务单元测试
 * 测试评级规则管理功能的正确性
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
class RatingRuleServiceTest {

    @Mock
    private RatingRuleRepository ratingRuleRepository;

    @Mock
    private RatingCacheService ratingCacheService;

    @InjectMocks
    private RatingRuleServiceImpl ratingRuleService;

    private RatingRule testRule;
    private List<RatingRule> testRules;

    @BeforeEach
    void setUp() {
        // 创建测试评级规则
        testRule = new RatingRule();
        testRule.setId(1L);
        testRule.setRuleName("信息完整度");
        testRule.setRuleType(RatingRuleType.COMPLETENESS);
        testRule.setWeight(BigDecimal.valueOf(0.20));
        testRule.setIsEnabled(true);
        testRule.setSortOrder(1);
        testRule.setDescription("评估线索信息的完整程度");
        testRule.setCreateTime(LocalDateTime.now());
        testRule.setUpdateTime(LocalDateTime.now());

        // 创建测试规则列表
        testRules = createTestRules();
    }

    /**
     * 测试创建评级规则 - 正常情况
     */
    @Test
    void testCreateRule_Success() {
        // 准备测试数据
        when(ratingRuleRepository.existsByRuleNameIgnoreCase("信息完整度")).thenReturn(false);
        when(ratingRuleRepository.existsBySortOrder(1)).thenReturn(false);
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(testRule);

        // 执行测试
        RatingRule result = ratingRuleService.createRule(testRule);

        // 验证结果
        assertNotNull(result);
        assertEquals("信息完整度", result.getRuleName());
        assertEquals(RatingRuleType.COMPLETENESS, result.getRuleType());
        assertTrue(result.getIsEnabled());
        
        // 验证缓存清除
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试创建评级规则 - 规则名称重复
     */
    @Test
    void testCreateRule_DuplicateName() {
        // 准备测试数据
        when(ratingRuleRepository.existsByRuleNameIgnoreCase("信息完整度")).thenReturn(true);

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            ratingRuleService.createRule(testRule);
        });
    }

    /**
     * 测试更新评级规则 - 正常情况
     */
    @Test
    void testUpdateRule_Success() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(ratingRuleRepository.existsByRuleNameIgnoreCaseAndIdNot("信息完整度", 1L)).thenReturn(false);
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(testRule);

        // 修改规则
        testRule.setWeight(BigDecimal.valueOf(0.25));
        testRule.setDescription("更新后的描述");

        // 执行测试
        RatingRule result = ratingRuleService.updateRule(1L, testRule);

        // 验证结果
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(0.25), result.getWeight());
        assertEquals("更新后的描述", result.getDescription());
        
        // 验证缓存清除
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试更新评级规则 - 规则不存在
     */
    @Test
    void testUpdateRule_NotFound() {
        // 准备测试数据
        when(ratingRuleRepository.findById(999L)).thenReturn(Optional.empty());
        testRule.setId(999L);

        // 执行测试并验证异常
        assertThrows(RuntimeException.class, () -> {
            ratingRuleService.updateRule(999L, testRule);
        });
    }

    /**
     * 测试删除评级规则
     */
    @Test
    void testDeleteRule() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));

        // 执行测试
        ratingRuleService.deleteRule(1L);

        // 验证删除操作
        verify(ratingRuleRepository).delete(testRule);
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试根据ID获取评级规则
     */
    @Test
    void testGetRuleById() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));

        // 执行测试
        Optional<RatingRule> result = ratingRuleService.getRuleById(1L);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals("信息完整度", result.get().getRuleName());
    }

    /**
     * 测试获取所有评级规则
     */
    @Test
    void testGetAllRules() {
        // 准备测试数据
        Pageable pageable = PageRequest.of(0, 10);
        Page<RatingRule> page = new PageImpl<>(testRules, pageable, testRules.size());
        when(ratingRuleRepository.findAll(pageable)).thenReturn(page);

        // 执行测试
        PageResult<RatingRule> result = ratingRuleService.getAllRules(pageable);

        // 验证结果
        assertNotNull(result);
        assertEquals(7, result.getRecords().size());
        assertEquals(7, result.getTotal());
    }

    /**
     * 测试获取启用的评级规则
     */
    @Test
    void testGetEnabledRules() {
        // 准备测试数据
        List<RatingRule> enabledRules = testRules.stream()
            .filter(RatingRule::getIsEnabled)
            .toList();
        when(ratingRuleRepository.findByIsEnabledTrueOrderBySortOrder()).thenReturn(enabledRules);

        // 执行测试
        List<RatingRule> result = ratingRuleService.getEnabledRules();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.size() > 0);
        assertTrue(result.stream().allMatch(RatingRule::getIsEnabled));
    }

    /**
     * 测试根据类型获取评级规则
     */
    @Test
    void testGetRulesByType() {
        // 准备测试数据
        List<RatingRule> completenessRules = testRules.stream()
            .filter(rule -> rule.getRuleType() == RatingRuleType.COMPLETENESS)
            .toList();
        when(ratingRuleRepository.findByRuleType(RatingRuleType.COMPLETENESS))
            .thenReturn(completenessRules);

        // 执行测试
        List<RatingRule> result = ratingRuleService.getRulesByType(RatingRuleType.COMPLETENESS);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.stream().allMatch(rule -> rule.getRuleType() == RatingRuleType.COMPLETENESS));
    }

    /**
     * 测试启用评级规则
     */
    @Test
    void testEnableRule() {
        // 准备测试数据
        testRule.setIsEnabled(false);
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(testRule);

        // 执行测试
        ratingRuleService.enableRule(1L);

        // 验证结果
        assertTrue(testRule.getIsEnabled());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试禁用评级规则
     */
    @Test
    void testDisableRule() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(testRule);

        // 执行测试
        ratingRuleService.disableRule(1L);

        // 验证结果
        assertFalse(testRule.getIsEnabled());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试批量启用评级规则
     */
    @Test
    void testBatchEnableRules() {
        // 准备测试数据
        List<Long> ruleIds = Arrays.asList(1L, 2L, 3L);
        when(ratingRuleRepository.findAllById(ruleIds)).thenReturn(testRules.subList(0, 3));

        // 执行测试
        ratingRuleService.batchEnableRules(ruleIds);

        // 验证结果
        verify(ratingRuleRepository).saveAll(any());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试批量禁用评级规则
     */
    @Test
    void testBatchDisableRules() {
        // 准备测试数据
        List<Long> ruleIds = Arrays.asList(1L, 2L, 3L);
        when(ratingRuleRepository.findAllById(ruleIds)).thenReturn(testRules.subList(0, 3));

        // 执行测试
        ratingRuleService.batchDisableRules(ruleIds);

        // 验证结果
        verify(ratingRuleRepository).saveAll(any());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试批量删除评级规则
     */
    @Test
    void testBatchDeleteRules() {
        // 准备测试数据
        List<Long> ruleIds = Arrays.asList(1L, 2L, 3L);
        when(ratingRuleRepository.findAllById(ruleIds)).thenReturn(testRules.subList(0, 3));

        // 执行测试 - 逐个删除规则（因为接口中没有批量删除方法）
        for (Long id : ruleIds) {
            ratingRuleService.deleteRule(id);
        }

        // 验证结果
        verify(ratingRuleRepository).deleteAll(any());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试调整规则排序
     */
    @Test
    void testAdjustRuleOrder() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(ratingRuleRepository.existsBySortOrderAndIdNot(5, 1L)).thenReturn(false);
        when(ratingRuleRepository.save(any(RatingRule.class))).thenReturn(testRule);

        // 执行测试
        ratingRuleService.updateRuleSortOrder(1L, 5);

        // 验证结果
        assertEquals(5, testRule.getSortOrder());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试验证评级规则
     */
    @Test
    void testValidateRule_Success() {
        // 执行测试
        RatingRuleService.RuleValidationResult result = ratingRuleService.validateRule(testRule);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isValid());
        assertNotNull(result.getErrors());
    }

    /**
     * 测试验证评级规则 - 权重超出范围
     */
    @Test
    void testValidateRule_InvalidWeight() {
        // 准备测试数据
        testRule.setWeight(BigDecimal.valueOf(1.5)); // 权重超出范围

        // 执行测试
        RatingRuleService.RuleValidationResult result = ratingRuleService.validateRule(testRule);

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrors());
        assertTrue(result.getErrors().stream().anyMatch(error -> error.contains("权重")));
    }

    /**
     * 测试重置为默认规则
     */
    @Test
    void testResetToDefaultRules() {
        // 执行测试
        ratingRuleService.resetToDefaultRules();

        // 验证结果
        verify(ratingRuleRepository).deleteAll();
        verify(ratingRuleRepository).saveAll(any());
        verify(ratingCacheService).clearAllRuleConfigs();
    }

    /**
     * 测试获取规则配置模板
     */
    @Test
    void testGetRuleConfigTemplate() {
        // 执行测试
        String template = ratingRuleService.getRuleConfigTemplate(RatingRuleType.COMPLETENESS);

        // 验证结果
        assertNotNull(template);
        assertFalse(template.isEmpty());
    }

    /**
     * 测试获取规则统计信息
     */
    @Test
    void testGetRuleStatistics() {
        // 准备测试数据
        when(ratingRuleRepository.count()).thenReturn(7L);
        when(ratingRuleRepository.countByIsEnabledTrue()).thenReturn(5L);
        when(ratingRuleRepository.countByIsEnabledFalse()).thenReturn(2L);

        // 执行测试
        RatingRuleService.RuleStatistics result = ratingRuleService.getRuleStatistics();

        // 验证结果
        assertNotNull(result);
        assertEquals(7, result.getTotalRules());
        assertEquals(5, result.getEnabledRules());
        assertEquals(2, result.getDisabledRules());
    }

    /**
     * 测试复制评级规则
     */
    @Test
    void testCopyRule() {
        // 准备测试数据
        when(ratingRuleRepository.findById(1L)).thenReturn(Optional.of(testRule));
        when(ratingRuleRepository.existsByRuleNameIgnoreCase("信息完整度_副本")).thenReturn(false);
        when(ratingRuleRepository.save(any(RatingRule.class))).thenAnswer(invocation -> {
            RatingRule rule = invocation.getArgument(0);
            rule.setId(2L);
            return rule;
        });

        // 执行测试
        RatingRule result = ratingRuleService.copyRule(1L, "信息完整度_副本");

        // 验证结果
        assertNotNull(result);
        assertEquals("信息完整度_副本", result.getRuleName());
        assertEquals(testRule.getRuleType(), result.getRuleType());
        assertEquals(testRule.getWeight(), result.getWeight());
        assertNotEquals(testRule.getId(), result.getId());
    }

    /**
     * 测试验证所有规则
     */
    @Test
    void testValidateAllRules() {
        // 准备测试数据
        when(ratingRuleRepository.findByIsEnabledTrueOrderBySortOrder()).thenReturn(testRules);

        // 执行测试
        RatingRuleService.RuleValidationResult result = ratingRuleService.validateAllRules();

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isValid());
        assertNotNull(result.getErrors());
    }

    /**
     * 测试验证所有规则 - 权重总和不为1.0
     */
    @Test
    void testValidateAllRules_Invalid() {
        // 准备测试数据 - 修改权重使总和不为1.0
        List<RatingRule> invalidRules = createTestRules();
        invalidRules.get(0).setWeight(BigDecimal.valueOf(0.5)); // 修改第一个规则的权重
        when(ratingRuleRepository.findByIsEnabledTrueOrderBySortOrder()).thenReturn(invalidRules);

        // 执行测试
        RatingRuleService.RuleValidationResult result = ratingRuleService.validateAllRules();

        // 验证结果
        assertNotNull(result);
        assertFalse(result.isValid());
        assertNotNull(result.getErrors());
        assertFalse(result.getErrors().isEmpty());
    }

    /**
     * 创建测试评级规则列表
     */
    private List<RatingRule> createTestRules() {
        return Arrays.asList(
            createRule(1L, "信息完整度", RatingRuleType.COMPLETENESS, 0.20),
            createRule(2L, "企业资质", RatingRuleType.QUALIFICATION, 0.15),
            createRule(3L, "企业规模", RatingRuleType.SCALE, 0.15),
            createRule(4L, "产业价值", RatingRuleType.INDUSTRY_VALUE, 0.20),
            createRule(5L, "地理位置", RatingRuleType.LOCATION, 0.10),
            createRule(6L, "时效性", RatingRuleType.TIMELINESS, 0.10),
            createRule(7L, "用户信誉", RatingRuleType.USER_REPUTATION, 0.10)
        );
    }

    /**
     * 创建测试规则
     */
    private RatingRule createRule(Long id, String name, RatingRuleType type, double weight) {
        RatingRule rule = new RatingRule();
        rule.setId(id);
        rule.setRuleName(name);
        rule.setRuleType(type);
        rule.setWeight(BigDecimal.valueOf(weight));
        rule.setCalculationMethod(CalculationMethod.WEIGHTED_SUM);
        rule.setConfigParams("{}");
        rule.setIsEnabled(true);
        rule.setSortOrder(id.intValue());
        rule.setDescription(name + "评级规则");
        rule.setCreateTime(LocalDateTime.now());
        rule.setUpdateTime(LocalDateTime.now());
        return rule;
    }
}