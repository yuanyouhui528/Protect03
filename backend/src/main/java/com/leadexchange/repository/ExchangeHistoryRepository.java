package com.leadexchange.repository;

import com.leadexchange.domain.exchange.ExchangeHistory;
import com.leadexchange.domain.exchange.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 交换历史记录数据访问接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface ExchangeHistoryRepository extends JpaRepository<ExchangeHistory, Long> {
    
    /**
     * 根据申请人ID查询交换历史
     * @param applicantId 申请人ID
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    Page<ExchangeHistory> findByApplicantIdOrderByCreateTimeDesc(
            Long applicantId, Pageable pageable);
    
    /**
     * 根据目标线索所有者ID查询交换历史
     * @param targetOwnerId 目标线索所有者ID
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    Page<ExchangeHistory> findByTargetOwnerIdOrderByCreateTimeDesc(
            Long targetOwnerId, Pageable pageable);
    
    /**
     * 根据用户ID查询相关的所有交换历史（作为申请人或目标线索所有者）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.applicantId = :userId " +
           "OR eh.targetOwnerId = :userId ORDER BY eh.createTime DESC")
    Page<ExchangeHistory> findByApplicantIdOrTargetOwnerIdOrderByCreateTimeDesc(
            @Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据最终状态查询交换历史
     * @param finalStatus 最终状态
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    Page<ExchangeHistory> findByFinalStatusOrderByCreateTimeDesc(
            ExchangeStatus finalStatus, Pageable pageable);
    
    /**
     * 查询成功的交换历史
     * @param pageable 分页参数
     * @return 成功的交换历史分页列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.finalStatus = 'COMPLETED' " +
           "ORDER BY eh.createTime DESC")
    Page<ExchangeHistory> findSuccessfulExchanges(Pageable pageable);
    
    /**
     * 查询失败的交换历史
     * @param pageable 分页参数
     * @return 失败的交换历史分页列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.finalStatus IN ('REJECTED', 'CANCELLED', 'EXPIRED') " +
           "ORDER BY eh.createTime DESC")
    Page<ExchangeHistory> findFailedExchanges(Pageable pageable);
    
    /**
     * 查询指定时间范围内的交换历史
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.createTime >= :startTime " +
           "AND eh.createTime <= :endTime ORDER BY eh.createTime DESC")
    Page<ExchangeHistory> findByCreateTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查询用户在指定时间范围内的交换历史
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE (eh.applicantId = :userId " +
           "OR eh.targetOwnerId = :userId) AND eh.createTime >= :startTime " +
           "AND eh.createTime <= :endTime ORDER BY eh.createTime DESC")
    Page<ExchangeHistory> findByUserIdAndCreateTimeBetween(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 统计用户的交换成功次数
     * @param userId 用户ID
     * @return 交换成功次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh WHERE (eh.applicantId = :userId " +
           "OR eh.targetOwnerId = :userId) AND eh.finalStatus = 'COMPLETED'")
    long countSuccessfulExchangesByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的交换失败次数
     * @param userId 用户ID
     * @return 交换失败次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh WHERE (eh.applicantId = :userId " +
           "OR eh.targetOwnerId = :userId) AND eh.finalStatus IN ('REJECTED', 'CANCELLED', 'EXPIRED')")
    long countFailedExchangesByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的总交换次数
     * @param userId 用户ID
     * @return 总交换次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh WHERE eh.applicantId = :userId " +
           "OR eh.targetOwnerId = :userId")
    long countTotalExchangesByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户作为申请人的交换次数
     * @param applicantId 申请人ID
     * @return 申请交换次数
     */
    long countByApplicantId(Long applicantId);
    
    /**
     * 统计用户作为目标线索所有者的交换次数
     * @param targetOwnerId 目标线索所有者ID
     * @return 被申请交换次数
     */
    long countByTargetOwnerId(Long targetOwnerId);
    
    /**
     * 统计系统总交换次数
     * @return 总交换次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh")
    long countTotalExchanges();
    
    /**
     * 统计成功交换次数
     * @return 成功交换次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh WHERE eh.finalStatus = 'COMPLETED'")
    long countSuccessfulExchanges();
    
    /**
     * 统计失败交换次数
     * @return 失败交换次数
     */
    @Query("SELECT COUNT(eh) FROM ExchangeHistory eh WHERE eh.finalStatus IN ('REJECTED', 'CANCELLED', 'EXPIRED')")
    long countFailedExchanges();
    
    /**
     * 计算系统交换成功率
     * @return 交换成功率（0-1之间的小数）
     */
    @Query("SELECT (COUNT(CASE WHEN eh.finalStatus = 'COMPLETED' THEN 1 END) * 1.0) / COUNT(eh) " +
           "FROM ExchangeHistory eh")
    Double calculateSuccessRate();
    
    /**
     * 计算用户的交换成功率
     * @param userId 用户ID
     * @return 用户交换成功率（0-1之间的小数）
     */
    @Query("SELECT (COUNT(CASE WHEN eh.finalStatus = 'COMPLETED' THEN 1 END) * 1.0) / COUNT(eh) " +
           "FROM ExchangeHistory eh WHERE eh.applicantId = :userId OR eh.targetOwnerId = :userId")
    Double calculateUserSuccessRate(@Param("userId") Long userId);
    
    /**
     * 统计交换积分总量
     * @return 交换积分总量
     */
    @Query("SELECT COALESCE(SUM(eh.exchangeCredits), 0) FROM ExchangeHistory eh " +
           "WHERE eh.finalStatus = 'COMPLETED'")
    BigDecimal sumTotalExchangeCredits();
    
    /**
     * 统计用户获得的交换积分总量
     * @param userId 用户ID
     * @return 用户获得的交换积分总量
     */
    @Query("SELECT COALESCE(SUM(eh.exchangeCredits), 0) FROM ExchangeHistory eh " +
           "WHERE eh.targetOwnerId = :userId AND eh.finalStatus = 'COMPLETED'")
    BigDecimal sumUserEarnedCredits(@Param("userId") Long userId);
    
    /**
     * 统计用户支出的交换积分总量
     * @param userId 用户ID
     * @return 用户支出的交换积分总量
     */
    @Query("SELECT COALESCE(SUM(eh.exchangeCredits), 0) FROM ExchangeHistory eh " +
           "WHERE eh.applicantId = :userId AND eh.finalStatus = 'COMPLETED'")
    BigDecimal sumUserSpentCredits(@Param("userId") Long userId);
    
    /**
     * 查询平均交换耗时
     * @return 平均交换耗时（小时）
     */
    @Query(value = "SELECT AVG(TIMESTAMPDIFF(HOUR, apply_time, complete_time)) FROM exchange_history " +
           "WHERE final_status = 'COMPLETED' AND complete_time IS NOT NULL", nativeQuery = true)
    Double calculateAverageExchangeDuration();
    
    /**
     * 查询最快交换记录
     * @param pageable 分页参数
     * @return 最快交换记录列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.finalStatus = 'COMPLETED' " +
           "AND eh.completeTime IS NOT NULL ORDER BY eh.completeTime ASC")
    List<ExchangeHistory> findFastestExchanges(Pageable pageable);
    
    /**
     * 查询最慢交换记录
     * @param pageable 分页参数
     * @return 最慢交换记录列表
     */
    @Query("SELECT eh FROM ExchangeHistory eh WHERE eh.finalStatus = 'COMPLETED' " +
           "AND eh.completeTime IS NOT NULL ORDER BY eh.completeTime DESC")
    List<ExchangeHistory> findSlowestExchanges(Pageable pageable);
    
    /**
     * 统计状态分布
     * @return 状态和对应的交换次数
     */
    @Query("SELECT eh.finalStatus, COUNT(eh) FROM ExchangeHistory eh GROUP BY eh.finalStatus")
    List<Object[]> countByFinalStatus();
    
    /**
     * 查询热门交换线索（被交换次数最多的线索）
     * @param pageable 分页参数
     * @return 线索ID和交换次数
     */
    @Query("SELECT eh.targetLeadId, COUNT(eh) as exchangeCount FROM ExchangeHistory eh " +
           "WHERE eh.finalStatus = 'COMPLETED' GROUP BY eh.targetLeadId " +
           "ORDER BY exchangeCount DESC")
    List<Object[]> findMostExchangedLeads(Pageable pageable);
    
    /**
     * 查询活跃交换用户（交换次数最多的用户）
     * @param pageable 分页参数
     * @return 用户ID和交换次数
     */
    @Query(value = "SELECT userId, COUNT(*) as exchangeCount FROM (" +
           "SELECT applicant_id as userId FROM exchange_history " +
           "UNION ALL " +
           "SELECT target_owner_id as userId FROM exchange_history" +
           ") as allUsers GROUP BY userId ORDER BY exchangeCount DESC", nativeQuery = true)
    List<Object[]> findMostActiveExchangeUsers(Pageable pageable);
}