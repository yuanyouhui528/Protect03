package com.leadexchange.repository;

import com.leadexchange.domain.exchange.ExchangeApplication;
import com.leadexchange.domain.exchange.ExchangeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 交换申请数据访问接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface ExchangeApplicationRepository extends JpaRepository<ExchangeApplication, Long> {
    
    /**
     * 根据申请人ID查询交换申请
     * @param applicantId 申请人ID
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByApplicantIdOrderByCreateTimeDesc(
            Long applicantId, Pageable pageable);
    
    /**
     * 根据目标线索所有者ID查询收到的交换申请
     * @param targetOwnerId 目标线索所有者ID
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByTargetOwnerIdOrderByCreateTimeDesc(
            Long targetOwnerId, Pageable pageable);
    
    /**
     * 根据状态查询交换申请
     * @param status 申请状态
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByStatusOrderByCreateTimeDesc(
            ExchangeStatus status, Pageable pageable);
    
    /**
     * 根据申请人ID和状态查询交换申请
     * @param applicantId 申请人ID
     * @param status 申请状态
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByApplicantIdAndStatusOrderByCreateTimeDesc(
            Long applicantId, ExchangeStatus status, Pageable pageable);
    
    /**
     * 根据目标线索所有者ID和状态查询交换申请
     * @param targetOwnerId 目标线索所有者ID
     * @param status 申请状态
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByTargetOwnerIdAndStatusOrderByCreateTimeDesc(
            Long targetOwnerId, ExchangeStatus status, Pageable pageable);
    
    /**
     * 查询指定用户的待处理交换申请（作为目标方）
     * @param targetOwnerId 目标线索所有者ID
     * @return 待处理的交换申请列表
     */
    List<ExchangeApplication> findByTargetOwnerIdAndStatus(
            Long targetOwnerId, ExchangeStatus status);
    
    /**
     * 查询过期的交换申请
     * @param currentTime 当前时间
     * @return 过期的交换申请列表
     */
    @Query("SELECT ea FROM ExchangeApplication ea WHERE ea.expireTime < :currentTime " +
           "AND ea.status = 'PENDING'")
    List<ExchangeApplication> findExpiredApplications(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * 查询指定目标线索的所有交换申请
     * @param targetLeadId 目标线索ID
     * @return 交换申请列表
     */
    List<ExchangeApplication> findByTargetLeadId(Long targetLeadId);
    
    /**
     * 查询指定申请人对指定目标线索的待处理申请
     * @param applicantId 申请人ID
     * @param targetLeadId 目标线索ID
     * @param status 申请状态
     * @return 交换申请（如果存在）
     */
    Optional<ExchangeApplication> findByApplicantIdAndTargetLeadIdAndStatus(
            Long applicantId, Long targetLeadId, ExchangeStatus status);
    
    /**
     * 检查指定申请人对指定目标线索是否存在指定状态的申请
     * @param applicantId 申请人ID
     * @param targetLeadId 目标线索ID
     * @param status 申请状态
     * @return 是否存在
     */
    boolean existsByApplicantIdAndTargetLeadIdAndStatus(
            Long applicantId, Long targetLeadId, ExchangeStatus status);
    
    /**
     * 统计用户的交换申请数量
     * @param applicantId 申请人ID
     * @param status 申请状态
     * @return 申请数量
     */
    long countByApplicantIdAndStatus(Long applicantId, ExchangeStatus status);
    
    /**
     * 统计用户收到的交换申请数量
     * @param targetOwnerId 目标线索所有者ID
     * @param status 申请状态
     * @return 申请数量
     */
    long countByTargetOwnerIdAndStatus(Long targetOwnerId, ExchangeStatus status);
    
    /**
     * 查询指定时间范围内的交换申请
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    @Query("SELECT ea FROM ExchangeApplication ea WHERE ea.createTime >= :startTime " +
           "AND ea.createTime <= :endTime ORDER BY ea.createTime DESC")
    Page<ExchangeApplication> findByCreateTimeBetween(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            Pageable pageable);
    
    /**
     * 查询用户参与的所有交换申请（作为申请人或目标方）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    @Query("SELECT ea FROM ExchangeApplication ea WHERE ea.applicantId = :userId " +
           "OR ea.targetOwnerId = :userId ORDER BY ea.createTime DESC")
    Page<ExchangeApplication> findByUserInvolved(
            @Param("userId") Long userId, Pageable pageable);
    
    /**
     * 查询指定状态列表的交换申请
     * @param statuses 状态列表
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    Page<ExchangeApplication> findByStatusInOrderByCreateTimeDesc(
            List<ExchangeStatus> statuses, Pageable pageable);
}