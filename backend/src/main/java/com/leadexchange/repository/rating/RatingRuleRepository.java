package com.leadexchange.repository.rating;

import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.domain.rating.CalculationMethod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 评级规则数据访问接口
 * 提供评级规则的数据库操作方法
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface RatingRuleRepository extends JpaRepository<RatingRule, Long> {

    /**
     * 查找所有启用的评级规则，按排序顺序排列
     * 
     * @return 启用的评级规则列表
     */
    List<RatingRule> findByIsEnabledTrueOrderBySortOrder();

    /**
     * 查找所有评级规则，按排序顺序排列
     * 
     * @return 所有评级规则列表
     */
    List<RatingRule> findAllByOrderBySortOrderAsc();

    /**
     * 根据规则类型查找评级规则
     * 
     * @param ruleType 规则类型
     * @return 评级规则列表
     */
    List<RatingRule> findByRuleType(RatingRuleType ruleType);

    /**
     * 根据规则类型查找启用的评级规则
     * 
     * @param ruleType 规则类型
     * @return 启用的评级规则列表
     */
    List<RatingRule> findByRuleTypeAndIsEnabledTrue(RatingRuleType ruleType);

    /**
     * 根据计算方法查找评级规则
     * 
     * @param calculationMethod 计算方法
     * @return 评级规则列表
     */
    List<RatingRule> findByCalculationMethod(CalculationMethod calculationMethod);

    /**
     * 根据计算方法查找启用的评级规则
     * 
     * @param calculationMethod 计算方法
     * @return 启用的评级规则列表
     */
    List<RatingRule> findByCalculationMethodAndIsEnabledTrue(CalculationMethod calculationMethod);

    /**
     * 根据计算方法查找评级规则，按排序顺序排列
     * 
     * @param calculationMethod 计算方法
     * @return 评级规则列表
     */
    List<RatingRule> findByCalculationMethodOrderBySortOrderAsc(CalculationMethod calculationMethod);

    /**
     * 根据规则名称查找评级规则（忽略大小写）
     * 
     * @param ruleName 规则名称
     * @return 评级规则
     */
    Optional<RatingRule> findByRuleNameIgnoreCase(String ruleName);

    /**
     * 检查规则名称是否已存在（排除指定ID）
     * 
     * @param ruleName 规则名称
     * @param excludeId 排除的规则ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(r) > 0 FROM RatingRule r WHERE LOWER(r.ruleName) = LOWER(:ruleName) AND r.id != :excludeId")
    boolean existsByRuleNameIgnoreCaseAndIdNot(@Param("ruleName") String ruleName, @Param("excludeId") Long excludeId);

    /**
     * 检查规则名称是否已存在
     * 
     * @param ruleName 规则名称
     * @return 是否存在
     */
    boolean existsByRuleNameIgnoreCase(String ruleName);

    /**
     * 获取指定排序顺序范围内的规则数量
     * 
     * @param minOrder 最小排序顺序
     * @param maxOrder 最大排序顺序
     * @return 规则数量
     */
    @Query("SELECT COUNT(r) FROM RatingRule r WHERE r.sortOrder BETWEEN :minOrder AND :maxOrder")
    long countBySortOrderBetween(@Param("minOrder") Integer minOrder, @Param("maxOrder") Integer maxOrder);

    /**
     * 获取最大排序顺序
     * 
     * @return 最大排序顺序
     */
    @Query("SELECT COALESCE(MAX(r.sortOrder), 0) FROM RatingRule r")
    Integer findMaxSortOrder();

    /**
     * 获取指定排序顺序之后的规则
     * 
     * @param sortOrder 排序顺序
     * @return 评级规则列表
     */
    List<RatingRule> findBySortOrderGreaterThanOrderBySortOrder(Integer sortOrder);

    /**
     * 获取指定排序顺序之前的规则
     * 
     * @param sortOrder 排序顺序
     * @return 评级规则列表
     */
    List<RatingRule> findBySortOrderLessThanOrderBySortOrderDesc(Integer sortOrder);

    /**
     * 统计启用的规则数量
     * 
     * @return 启用的规则数量
     */
    long countByIsEnabledTrue();

    /**
     * 统计禁用的规则数量
     * 
     * @return 禁用的规则数量
     */
    long countByIsEnabledFalse();

    /**
     * 根据规则类型统计规则数量
     * 
     * @param ruleType 规则类型
     * @return 规则数量
     */
    long countByRuleType(RatingRuleType ruleType);

    /**
     * 根据计算方法统计规则数量
     * 
     * @param calculationMethod 计算方法
     * @return 规则数量
     */
    long countByCalculationMethod(CalculationMethod calculationMethod);

    /**
     * 获取权重总和
     * 
     * @return 权重总和
     */
    @Query("SELECT COALESCE(SUM(r.weight), 0) FROM RatingRule r WHERE r.isEnabled = true")
    Double sumWeightByIsEnabledTrue();

    /**
     * 根据权重范围查找规则
     * 
     * @param minWeight 最小权重
     * @param maxWeight 最大权重
     * @return 评级规则列表
     */
    List<RatingRule> findByWeightBetweenOrderByWeightDesc(Double minWeight, Double maxWeight);

    /**
     * 查找权重大于指定值的规则
     * 
     * @param weight 权重阈值
     * @return 评级规则列表
     */
    List<RatingRule> findByWeightGreaterThanOrderByWeightDesc(Double weight);

    /**
     * 查找权重小于指定值的规则
     * 
     * @param weight 权重阈值
     * @return 评级规则列表
     */
    List<RatingRule> findByWeightLessThanOrderByWeightAsc(Double weight);

    /**
     * 根据描述关键词搜索规则
     * 
     * @param keyword 关键词
     * @return 评级规则列表
     */
    @Query("SELECT r FROM RatingRule r WHERE LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RatingRule> findByDescriptionContainingIgnoreCase(@Param("keyword") String keyword);

    /**
     * 根据规则名称或描述搜索规则
     * 
     * @param keyword 关键词
     * @return 评级规则列表
     */
    @Query("SELECT r FROM RatingRule r WHERE LOWER(r.ruleName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RatingRule> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 批量更新规则的启用状态
     * 
     * @param ids 规则ID列表
     * @param enabled 启用状态
     * @return 更新的记录数
     */
    @Query("UPDATE RatingRule r SET r.isEnabled = :enabled WHERE r.id IN :ids")
    int batchUpdateEnabled(@Param("ids") List<Long> ids, @Param("enabled") boolean enabled);

    /**
     * 批量删除规则
     * 
     * @param ids 规则ID列表
     * @return 删除的记录数
     */
    @Query("DELETE FROM RatingRule r WHERE r.id IN :ids")
    int batchDeleteByIds(@Param("ids") List<Long> ids);

    /**
     * 获取核心规则类型的规则
     * 
     * @return 核心评级规则列表
     */
    @Query("SELECT r FROM RatingRule r WHERE r.ruleType IN " +
           "(com.leadexchange.domain.rating.RatingRuleType.COMPLETENESS, " +
           "com.leadexchange.domain.rating.RatingRuleType.QUALIFICATION, " +
           "com.leadexchange.domain.rating.RatingRuleType.SCALE, " +
           "com.leadexchange.domain.rating.RatingRuleType.INDUSTRY_VALUE) " +
           "AND r.isEnabled = true ORDER BY r.sortOrder")
    List<RatingRule> findCoreRules();

    /**
     * 获取扩展规则类型的规则
     * 
     * @return 扩展评级规则列表
     */
    @Query("SELECT r FROM RatingRule r WHERE r.ruleType NOT IN " +
           "(com.leadexchange.domain.rating.RatingRuleType.COMPLETENESS, " +
           "com.leadexchange.domain.rating.RatingRuleType.QUALIFICATION, " +
           "com.leadexchange.domain.rating.RatingRuleType.SCALE, " +
           "com.leadexchange.domain.rating.RatingRuleType.INDUSTRY_VALUE) " +
           "AND r.isEnabled = true ORDER BY r.sortOrder")
    List<RatingRule> findExtendedRules();

    /**
     * 检查是否存在重复的排序顺序
     * 
     * @param sortOrder 排序顺序
     * @param excludeId 排除的规则ID
     * @return 是否存在重复
     */
    @Query("SELECT COUNT(r) > 0 FROM RatingRule r WHERE r.sortOrder = :sortOrder AND r.id != :excludeId")
    boolean existsBySortOrderAndIdNot(@Param("sortOrder") Integer sortOrder, @Param("excludeId") Long excludeId);

    /**
     * 检查是否存在重复的排序顺序
     * 
     * @param sortOrder 排序顺序
     * @return 是否存在重复
     */
    boolean existsBySortOrder(Integer sortOrder);
}