package com.leadexchange.domain.exchange;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户积分实体类
 * 管理用户的积分余额和冻结积分
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "user_credits")
public class UserCredit {
    
    /**
     * 积分记录ID
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
     * 总积分
     */
    @Column(name = "total_credits", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalCredits = BigDecimal.ZERO;
    
    /**
     * 可用积分
     */
    @Column(name = "available_credits", nullable = false, precision = 10, scale = 2)
    private BigDecimal availableCredits = BigDecimal.ZERO;
    
    /**
     * 冻结积分
     */
    @Column(name = "frozen_credits", nullable = false, precision = 10, scale = 2)
    private BigDecimal frozenCredits = BigDecimal.ZERO;
    
    /**
     * 累计收入积分
     */
    @Column(name = "total_income", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalIncome = BigDecimal.ZERO;
    
    /**
     * 累计支出积分
     */
    @Column(name = "total_expense", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalExpense = BigDecimal.ZERO;
    
    /**
     * 最后交易时间
     */
    @Column(name = "last_transaction_time")
    private LocalDateTime lastTransactionTime;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    private Long createBy;
    
    /**
     * 更新人ID
     */
    @Column(name = "update_by")
    private Long updateBy;
    
    // 构造函数
    public UserCredit() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    public UserCredit(Long userId) {
        this();
        this.userId = userId;
    }
    
    // 业务方法
    
    /**
     * 增加积分
     * @param amount 增加的积分数量
     */
    public void addCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("积分数量必须大于0");
        }
        this.totalCredits = this.totalCredits.add(amount);
        this.availableCredits = this.availableCredits.add(amount);
        this.totalIncome = this.totalIncome.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 扣减积分
     * @param amount 扣减的积分数量
     * @return true表示扣减成功，false表示余额不足
     */
    public boolean deductCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("积分数量必须大于0");
        }
        if (this.availableCredits.compareTo(amount) < 0) {
            return false;
        }
        this.availableCredits = this.availableCredits.subtract(amount);
        this.totalExpense = this.totalExpense.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        return true;
    }
    
    /**
     * 冻结积分
     * @param amount 冻结的积分数量
     * @return true表示冻结成功，false表示可用余额不足
     */
    public boolean freezeCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("积分数量必须大于0");
        }
        if (this.availableCredits.compareTo(amount) < 0) {
            return false;
        }
        this.availableCredits = this.availableCredits.subtract(amount);
        this.frozenCredits = this.frozenCredits.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        return true;
    }
    
    /**
     * 解冻积分
     * @param amount 解冻的积分数量
     * @return true表示解冻成功，false表示冻结余额不足
     */
    public boolean unfreezeCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("积分数量必须大于0");
        }
        if (this.frozenCredits.compareTo(amount) < 0) {
            return false;
        }
        this.frozenCredits = this.frozenCredits.subtract(amount);
        this.availableCredits = this.availableCredits.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        return true;
    }
    
    /**
     * 扣减冻结积分（用于交换完成时）
     * @param amount 扣减的积分数量
     * @return true表示扣减成功，false表示冻结余额不足
     */
    public boolean deductFrozenCredits(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("积分数量必须大于0");
        }
        if (this.frozenCredits.compareTo(amount) < 0) {
            return false;
        }
        this.frozenCredits = this.frozenCredits.subtract(amount);
        this.totalExpense = this.totalExpense.add(amount);
        this.lastTransactionTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        return true;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
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
    
    public BigDecimal getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    public BigDecimal getAvailableCredits() {
        return availableCredits;
    }
    
    public void setAvailableCredits(BigDecimal availableCredits) {
        this.availableCredits = availableCredits;
    }
    
    public BigDecimal getFrozenCredits() {
        return frozenCredits;
    }
    
    public void setFrozenCredits(BigDecimal frozenCredits) {
        this.frozenCredits = frozenCredits;
    }
    
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
    
    public BigDecimal getTotalExpense() {
        return totalExpense;
    }
    
    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }
    
    public LocalDateTime getLastTransactionTime() {
        return lastTransactionTime;
    }
    
    public void setLastTransactionTime(LocalDateTime lastTransactionTime) {
        this.lastTransactionTime = lastTransactionTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    
    public Long getCreateBy() {
        return createBy;
    }
    
    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }
    
    public Long getUpdateBy() {
        return updateBy;
    }
    
    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }
}