package com.leadexchange.service;

import com.leadexchange.domain.exchange.ExchangeTransaction;
import com.leadexchange.domain.exchange.TransactionType;
import com.leadexchange.domain.exchange.UserCredit;
import com.leadexchange.repository.ExchangeTransactionRepository;
import com.leadexchange.repository.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户积分服务类
 * 负责用户积分的管理，包括积分的增加、扣减、冻结、解冻等操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class UserCreditService {
    
    private static final Logger log = LoggerFactory.getLogger(UserCreditService.class);
    
    private final UserCreditRepository userCreditRepository;
    private final ExchangeTransactionRepository transactionRepository;
    
    /**
     * 获取用户积分信息
     * @param userId 用户ID
     * @return 用户积分信息，如果不存在则创建新的积分记录
     */
    @Transactional(readOnly = true)
    public UserCredit getUserCredit(Long userId) {
        return userCreditRepository.findByUserId(userId)
                .orElseGet(() -> createInitialCredit(userId));
    }
    
    /**
     * 获取用户积分信息（带悲观锁）
     * @param userId 用户ID
     * @return 用户积分信息
     */
    @Transactional
    public UserCredit getUserCreditWithLock(Long userId) {
        return userCreditRepository.findByUserIdWithLock(userId)
                .orElseGet(() -> createInitialCredit(userId));
    }
    
    /**
     * 创建初始积分记录
     * @param userId 用户ID
     * @return 新创建的积分记录
     */
    @Transactional
    public UserCredit createInitialCredit(Long userId) {
        UserCredit userCredit = new UserCredit();
        userCredit.setUserId(userId);
        userCredit.setTotalCredits(BigDecimal.ZERO);
        userCredit.setAvailableCredits(BigDecimal.ZERO);
        userCredit.setFrozenCredits(BigDecimal.ZERO);
        userCredit.setTotalIncome(BigDecimal.ZERO);
        userCredit.setTotalExpense(BigDecimal.ZERO);
        userCredit.setLastTransactionTime(LocalDateTime.now());
        userCredit.setCreateTime(LocalDateTime.now());
        userCredit.setUpdateTime(LocalDateTime.now());
        userCredit.setCreateBy(userId);
        userCredit.setUpdateBy(userId);
        
        return userCreditRepository.save(userCredit);
    }
    
    /**
     * 增加用户积分
     * @param userId 用户ID
     * @param amount 增加金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit addCredits(Long userId, BigDecimal amount, String sourceType, 
                                Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("增加积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 增加积分
        userCredit.addCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.INCOME, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.INCOME, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.INCOME, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]积分增加成功，金额：{}，来源：{}，当前可用积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits());
        
        return userCredit;
    }
    
    /**
     * 扣减用户积分
     * @param userId 用户ID
     * @param amount 扣减金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit deductCredits(Long userId, BigDecimal amount, String sourceType, 
                                   Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣减积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 检查余额是否足够
        if (userCredit.getAvailableCredits().compareTo(amount) < 0) {
            throw new IllegalStateException("用户积分余额不足，当前可用积分：" + 
                    userCredit.getAvailableCredits() + "，需要扣减：" + amount);
        }
        
        // 扣减积分
        userCredit.deductCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]积分扣减成功，金额：{}，来源：{}，当前可用积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits());
        
        return userCredit;
    }
    
    /**
     * 冻结用户积分
     * @param userId 用户ID
     * @param amount 冻结金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit freezeCredits(Long userId, BigDecimal amount, String sourceType, 
                                   Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("冻结积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 检查余额是否足够
        if (userCredit.getAvailableCredits().compareTo(amount) < 0) {
            throw new IllegalStateException("用户积分余额不足，当前可用积分：" + 
                    userCredit.getAvailableCredits() + "，需要冻结：" + amount);
        }
        
        // 冻结积分
        userCredit.freezeCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.FREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.FREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.FREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]积分冻结成功，金额：{}，来源：{}，当前可用积分：{}，冻结积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits(), userCredit.getFrozenCredits());
        
        return userCredit;
    }
    
    /**
     * 解冻用户积分
     * @param userId 用户ID
     * @param amount 解冻金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit unfreezeCredits(Long userId, BigDecimal amount, String sourceType, 
                                     Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("解冻积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 检查冻结余额是否足够
        if (userCredit.getFrozenCredits().compareTo(amount) < 0) {
            throw new IllegalStateException("用户冻结积分不足，当前冻结积分：" + 
                    userCredit.getFrozenCredits() + "，需要解冻：" + amount);
        }
        
        // 解冻积分
        userCredit.unfreezeCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.UNFREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.UNFREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.UNFREEZE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]积分解冻成功，金额：{}，来源：{}，当前可用积分：{}，冻结积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits(), userCredit.getFrozenCredits());
        
        return userCredit;
    }
    
    /**
     * 扣减冻结积分
     * @param userId 用户ID
     * @param amount 扣减金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit deductFrozenCredits(Long userId, BigDecimal amount, String sourceType, 
                                         Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("扣减冻结积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 检查冻结余额是否足够
        if (userCredit.getFrozenCredits().compareTo(amount) < 0) {
            throw new IllegalStateException("用户冻结积分不足，当前冻结积分：" + 
                    userCredit.getFrozenCredits() + "，需要扣减：" + amount);
        }
        
        // 扣减冻结积分
        userCredit.deductFrozenCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.EXPENSE, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]冻结积分扣减成功，金额：{}，来源：{}，当前可用积分：{}，冻结积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits(), userCredit.getFrozenCredits());
        
        return userCredit;
    }
    
    /**
     * 退款积分
     * @param userId 用户ID
     * @param amount 退款金额
     * @param sourceType 来源类型
     * @param sourceId 来源ID
     * @param description 描述
     * @return 更新后的积分信息
     */
    @Transactional
    public UserCredit refundCredits(Long userId, BigDecimal amount, String sourceType, 
                                   Long sourceId, String description) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("退款积分金额必须大于0");
        }
        
        UserCredit userCredit = getUserCreditWithLock(userId);
        BigDecimal beforeBalance = userCredit.getAvailableCredits();
        
        // 增加积分（退款）
        userCredit.addCredits(amount);
        userCredit.setUpdateBy(userId);
        userCredit = userCreditRepository.save(userCredit);
        
        // 记录交易
        ExchangeTransaction transaction;
        if ("EXCHANGE".equals(sourceType)) {
            transaction = ExchangeTransaction.createExchangeTransaction(
                    userId, TransactionType.REFUND, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), sourceId, description);
        } else if ("MANUAL".equals(sourceType)) {
            transaction = ExchangeTransaction.createManualTransaction(
                    userId, TransactionType.REFUND, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), userId, description);
        } else {
            transaction = ExchangeTransaction.createSystemTransaction(
                    userId, TransactionType.REFUND, amount, beforeBalance, 
                    userCredit.getAvailableCredits(), description);
        }
        transactionRepository.save(transaction);
        
        log.info("用户[{}]积分退款成功，金额：{}，来源：{}，当前可用积分：{}", 
                userId, amount, sourceType, userCredit.getAvailableCredits());
        
        return userCredit;
    }
    
    /**
     * 检查用户是否有足够的可用积分
     * @param userId 用户ID
     * @param amount 需要的积分金额
     * @return 是否有足够积分
     */
    @Transactional(readOnly = true)
    public boolean hasEnoughCredits(Long userId, BigDecimal amount) {
        return userCreditRepository.hasEnoughCredits(userId, amount);
    }
    
    /**
     * 获取用户交易记录
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    @Transactional(readOnly = true)
    public Page<ExchangeTransaction> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 获取用户指定类型的交易记录
     * @param userId 用户ID
     * @param transactionType 交易类型
     * @param pageable 分页参数
     * @return 交易记录分页列表
     */
    @Transactional(readOnly = true)
    public Page<ExchangeTransaction> getUserTransactionsByType(Long userId, 
                                                              TransactionType transactionType, 
                                                              Pageable pageable) {
        return transactionRepository.findByUserIdAndTransactionTypeOrderByCreateTimeDesc(
                userId, transactionType, pageable);
    }
    
    /**
     * 获取积分排行榜
     * @param limit 限制数量
     * @return 积分排行榜
     */
    @Transactional(readOnly = true)
    public List<UserCredit> getCreditRanking(int limit) {
        return userCreditRepository.findTopByTotalCredits(limit);
    }
    
    /**
     * 获取可用积分排行榜
     * @param limit 限制数量
     * @return 可用积分排行榜
     */
    @Transactional(readOnly = true)
    public List<UserCredit> getAvailableCreditRanking(int limit) {
        return userCreditRepository.findTopByAvailableCredits(limit);
    }
    
    /**
     * 统计系统总积分
     * @return 系统总积分
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalSystemCredits() {
        return userCreditRepository.sumTotalCredits();
    }
    
    /**
     * 统计系统可用积分
     * @return 系统可用积分
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalAvailableCredits() {
        return userCreditRepository.sumAvailableCredits();
    }
    
    /**
     * 统计系统冻结积分
     * @return 系统冻结积分
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalFrozenCredits() {
        return userCreditRepository.sumFrozenCredits();
    }
    
    /**
     * 查询有冻结积分的用户
     * @param pageable 分页参数
     * @return 有冻结积分的用户列表
     */
    @Transactional(readOnly = true)
    public Page<UserCredit> getUsersWithFrozenCredits(Pageable pageable) {
        List<UserCredit> users = userCreditRepository.findByFrozenCreditsGreaterThan(BigDecimal.ZERO);
        return new PageImpl<>(users, pageable, users.size());
    }
    
    /**
     * 查询积分为零的用户
     * @param pageable 分页参数
     * @return 积分为零的用户列表
     */
    @Transactional(readOnly = true)
    public Page<UserCredit> getUsersWithZeroCredits(Pageable pageable) {
        List<UserCredit> users = userCreditRepository.findUsersWithZeroCredits();
        return new PageImpl<>(users, pageable, users.size());
    }
}