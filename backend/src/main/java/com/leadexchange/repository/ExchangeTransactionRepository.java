package com.leadexchange.repository;

import com.leadexchange.domain.exchange.ExchangeTransaction;
import com.leadexchange.domain.exchange.TransactionType;
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
 * 交换交易记录数据访问接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface ExchangeTransactionRepository extends JpaRepository<ExchangeTransaction, Long> {
    
    /**
     * 根据用户ID查询交易记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findByUserIdOrderByCreateTimeDesc(
            Long userId, Pageable pageable);
    
    /**
     * 根据用户ID和交易类型查询交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findByUserIdAndTransactionTypeOrderByCreateTimeDesc(
            Long userId, TransactionType transactionType, Pageable pageable);
    
    /**
     * 根据来源类型查询交易记录
     * @param sourceType 来源类型
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findBySourceTypeOrderByCreateTimeDesc(
            String sourceType, Pageable pageable);
    
    /**
     * 根据来源ID查询交易记录
     * @param sourceId 来源ID
     * @return 交易记录列表
     */
    List<ExchangeTransaction> findBySourceIdOrderByCreateTimeDesc(Long sourceId);
    
    /**
     * 根据用户ID和来源类型查询交易记录
     * @param userId 用户ID
     * @param sourceType 来源类型
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findByUserIdAndSourceTypeOrderByCreateTimeDesc(
            Long userId, String sourceType, Pageable pageable);
    
    /**
     * 查询指定时间范围内的交易记录
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    @Query("SELECT et FROM ExchangeTransaction et WHERE et.userId = :userId " +
           "AND et.createTime >= :startTime AND et.createTime <= :endTime " +
           "ORDER BY et.createTime DESC")
    Page<ExchangeTransaction> findByUserIdAndCreateTimeBetween(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 统计用户的总收入
     * @param userId 用户ID
     * @return 总收入金额
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et " +
           "WHERE et.userId = :userId AND et.transactionType IN ('INCOME', 'UNFREEZE', 'REFUND')")
    BigDecimal sumIncomeByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的总支出
     * @param userId 用户ID
     * @return 总支出金额
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et " +
           "WHERE et.userId = :userId AND et.transactionType IN ('EXPENSE', 'FREEZE')")
    BigDecimal sumExpenseByUserId(@Param("userId") Long userId);
    
    /**
     * 统计指定时间范围内用户的收入
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 收入金额
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et " +
           "WHERE et.userId = :userId AND et.transactionType IN ('INCOME', 'UNFREEZE', 'REFUND') " +
           "AND et.createTime >= :startTime AND et.createTime <= :endTime")
    BigDecimal sumIncomeByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内用户的支出
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 支出金额
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et " +
           "WHERE et.userId = :userId AND et.transactionType IN ('EXPENSE', 'FREEZE') " +
           "AND et.createTime >= :startTime AND et.createTime <= :endTime")
    BigDecimal sumExpenseByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计系统总交易量
     * @return 总交易量
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et")
    BigDecimal sumTotalTransactionAmount();
    
    /**
     * 统计指定时间范围内的总交易量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 总交易量
     */
    @Query("SELECT COALESCE(SUM(et.amount), 0) FROM ExchangeTransaction et " +
           "WHERE et.createTime >= :startTime AND et.createTime <= :endTime")
    BigDecimal sumTransactionAmountByDateRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计交易类型分布
     * @return 交易类型和对应的交易次数
     */
    @Query("SELECT et.transactionType, COUNT(et) FROM ExchangeTransaction et GROUP BY et.transactionType")
    List<Object[]> countByTransactionType();
    
    /**
     * 统计来源类型分布
     * @return 来源类型和对应的交易次数
     */
    @Query("SELECT et.sourceType, COUNT(et) FROM ExchangeTransaction et GROUP BY et.sourceType")
    List<Object[]> countBySourceType();
    
    /**
     * 查询最近的交易记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近的交易记录列表
     */
    @Query("SELECT et FROM ExchangeTransaction et WHERE et.userId = :userId " +
           "ORDER BY et.createTime DESC")
    List<ExchangeTransaction> findRecentTransactions(
            @Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计用户的交易次数
     * @param userId 用户ID
     * @return 交易次数
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户指定类型的交易次数
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @return 交易次数
     */
    long countByUserIdAndTransactionType(Long userId, TransactionType transactionType);
    
    /**
     * 查询指定金额范围的交易记录
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findByAmountBetweenOrderByCreateTimeDesc(
            BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);
    
    /**
     * 查询大额交易记录（金额大于指定值）
     * @param minAmount 最小金额
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    Page<ExchangeTransaction> findByAmountGreaterThanOrderByAmountDesc(
            BigDecimal minAmount, Pageable pageable);
}