package com.leadexchange.repository;

import com.leadexchange.domain.exchange.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 用户积分数据访问接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, Long> {
    
    /**
     * 根据用户ID查询积分记录
     * @param userId 用户ID
     * @return 用户积分记录
     */
    Optional<UserCredit> findByUserId(Long userId);
    
    /**
     * 根据用户ID查询积分记录（加悲观锁）
     * @param userId 用户ID
     * @return 用户积分记录
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCredit uc WHERE uc.userId = :userId")
    Optional<UserCredit> findByUserIdWithLock(@Param("userId") Long userId);
    
    /**
     * 查询积分余额大于指定值的用户
     * @param minCredits 最小积分值
     * @return 用户积分记录列表
     */
    List<UserCredit> findByAvailableCreditsGreaterThan(BigDecimal minCredits);
    
    /**
     * 查询积分余额在指定范围内的用户
     * @param minCredits 最小积分值
     * @param maxCredits 最大积分值
     * @return 用户积分记录列表
     */
    List<UserCredit> findByAvailableCreditsBetween(BigDecimal minCredits, BigDecimal maxCredits);
    
    /**
     * 查询总积分排名前N的用户
     * @param limit 限制数量
     * @return 用户积分记录列表
     */
    @Query("SELECT uc FROM UserCredit uc ORDER BY uc.totalCredits DESC")
    List<UserCredit> findTopByTotalCredits(@Param("limit") int limit);
    
    /**
     * 查询可用积分排名前N的用户
     * @param limit 限制数量
     * @return 用户积分记录列表
     */
    @Query("SELECT uc FROM UserCredit uc ORDER BY uc.availableCredits DESC")
    List<UserCredit> findTopByAvailableCredits(@Param("limit") int limit);
    
    /**
     * 统计总积分
     * @return 所有用户的总积分和
     */
    @Query("SELECT SUM(uc.totalCredits) FROM UserCredit uc")
    BigDecimal sumTotalCredits();
    
    /**
     * 统计可用积分
     * @return 所有用户的可用积分和
     */
    @Query("SELECT SUM(uc.availableCredits) FROM UserCredit uc")
    BigDecimal sumAvailableCredits();
    
    /**
     * 统计冻结积分
     * @return 所有用户的冻结积分和
     */
    @Query("SELECT SUM(uc.frozenCredits) FROM UserCredit uc")
    BigDecimal sumFrozenCredits();
    
    /**
     * 查询有冻结积分的用户
     * @return 有冻结积分的用户积分记录列表
     */
    List<UserCredit> findByFrozenCreditsGreaterThan(BigDecimal zero);
    
    /**
     * 查询积分为零的用户
     * @return 积分为零的用户积分记录列表
     */
    @Query("SELECT uc FROM UserCredit uc WHERE uc.totalCredits = 0 AND uc.availableCredits = 0 AND uc.frozenCredits = 0")
    List<UserCredit> findUsersWithZeroCredits();
    
    /**
     * 根据用户ID列表查询积分记录
     * @param userIds 用户ID列表
     * @return 用户积分记录列表
     */
    List<UserCredit> findByUserIdIn(List<Long> userIds);
    
    /**
     * 检查用户是否有足够的可用积分
     * @param userId 用户ID
     * @param requiredCredits 需要的积分数量
     * @return true表示积分足够，false表示积分不足
     */
    @Query("SELECT CASE WHEN uc.availableCredits >= :requiredCredits THEN true ELSE false END " +
           "FROM UserCredit uc WHERE uc.userId = :userId")
    Boolean hasEnoughCredits(@Param("userId") Long userId, @Param("requiredCredits") BigDecimal requiredCredits);
    
    /**
     * 获取用户的可用积分余额
     * @param userId 用户ID
     * @return 可用积分余额
     */
    @Query("SELECT uc.availableCredits FROM UserCredit uc WHERE uc.userId = :userId")
    Optional<BigDecimal> getAvailableCredits(@Param("userId") Long userId);
    
    /**
     * 获取用户的总积分
     * @param userId 用户ID
     * @return 总积分
     */
    @Query("SELECT uc.totalCredits FROM UserCredit uc WHERE uc.userId = :userId")
    Optional<BigDecimal> getTotalCredits(@Param("userId") Long userId);
    
    /**
     * 获取用户的冻结积分
     * @param userId 用户ID
     * @return 冻结积分
     */
    @Query("SELECT uc.frozenCredits FROM UserCredit uc WHERE uc.userId = :userId")
    Optional<BigDecimal> getFrozenCredits(@Param("userId") Long userId);
    
    /**
     * 统计活跃用户数量（有积分的用户）
     * @return 活跃用户数量
     */
    @Query("SELECT COUNT(uc) FROM UserCredit uc WHERE uc.totalCredits > 0")
    long countActiveUsers();
    
    /**
     * 查询积分记录是否存在
     * @param userId 用户ID
     * @return true表示存在，false表示不存在
     */
    boolean existsByUserId(Long userId);
}