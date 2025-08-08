package com.leadexchange.domain.exchange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 交换交易记录实体类
 * 记录积分交易的详细信息
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "exchange_transactions")
public class ExchangeTransaction {
    
    /**
     * 交易ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 交易类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;
    
    /**
     * 交易金额
     */
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    
    /**
     * 交易前余额
     */
    @Column(name = "balance_before", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceBefore;
    
    /**
     * 交易后余额
     */
    @Column(name = "balance_after", nullable = false, precision = 10, scale = 2)
    private BigDecimal balanceAfter;
    
    /**
     * 来源类型（EXCHANGE：交换，SYSTEM：系统，MANUAL：手动）
     */
    @Column(name = "source_type", nullable = false, length = 20)
    private String sourceType;
    
    /**
     * 来源ID（如交换申请ID）
     */
    @Column(name = "source_id")
    private Long sourceId;
    
    /**
     * 交易描述
     */
    @Column(name = "description", length = 200)
    private String description;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;
    
    // 构造函数
    public ExchangeTransaction() {
        this.createTime = LocalDateTime.now();
    }
    
    public ExchangeTransaction(Long userId, TransactionType transactionType, BigDecimal amount) {
        this();
        this.userId = userId;
        this.transactionType = transactionType;
        this.amount = amount;
    }
    
    /**
     * 创建交换相关的交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @param amount 交易金额
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @param exchangeId 交换申请ID
     * @param description 交易描述
     * @return 交易记录
     */
    public static ExchangeTransaction createExchangeTransaction(
            Long userId, TransactionType transactionType, BigDecimal amount,
            BigDecimal balanceBefore, BigDecimal balanceAfter,
            Long exchangeId, String description) {
        ExchangeTransaction transaction = new ExchangeTransaction(userId, transactionType, amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setSourceType("EXCHANGE");
        transaction.setSourceId(exchangeId);
        transaction.setDescription(description);
        return transaction;
    }
    
    /**
     * 创建系统相关的交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @param amount 交易金额
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @param description 交易描述
     * @return 交易记录
     */
    public static ExchangeTransaction createSystemTransaction(
            Long userId, TransactionType transactionType, BigDecimal amount,
            BigDecimal balanceBefore, BigDecimal balanceAfter, String description) {
        ExchangeTransaction transaction = new ExchangeTransaction(userId, transactionType, amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setSourceType("SYSTEM");
        transaction.setDescription(description);
        return transaction;
    }
    
    /**
     * 创建手动调整的交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @param amount 交易金额
     * @param balanceBefore 交易前余额
     * @param balanceAfter 交易后余额
     * @param operatorId 操作人ID
     * @param description 交易描述
     * @return 交易记录
     */
    public static ExchangeTransaction createManualTransaction(
            Long userId, TransactionType transactionType, BigDecimal amount,
            BigDecimal balanceBefore, BigDecimal balanceAfter,
            Long operatorId, String description) {
        ExchangeTransaction transaction = new ExchangeTransaction(userId, transactionType, amount);
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(balanceAfter);
        transaction.setSourceType("MANUAL");
        transaction.setCreateBy(operatorId);
        transaction.setDescription(description);
        return transaction;
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public TransactionType getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }
    
    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }
    
    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }
    
    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    public String getSourceType() {
        return sourceType;
    }
    
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public Long getSourceId() {
        return sourceId;
    }
    
    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
}