package com.leadexchange.repository.lead;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.AuditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 线索数据访问层接口
 * 继承JpaRepository，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    /**
     * 根据状态查找线索
     * 
     * @param status 线索状态
     * @return 线索列表
     */
    List<Lead> findByStatus(LeadStatus status);
    
    /**
     * 根据状态查找线索，排除指定ID
     * 
     * @param status 线索状态
     * @param id 要排除的线索ID
     * @return 线索列表
     */
    List<Lead> findByStatusAndIdNot(LeadStatus status, Long id);
    
    /**
     * 根据状态和删除标记查找线索
     * 
     * @param status 线索状态
     * @param deleted 删除标记
     * @return 线索列表
     */
    List<Lead> findByStatusAndDeleted(LeadStatus status, Integer deleted);
    
    /**
     * 根据发布者ID查找线索
     * 
     * @param publisherId 发布者ID
     * @return 线索列表
     */
    List<Lead> findByPublisherId(Long publisherId);
    
    /**
     * 根据审核状态查找线索
     * 
     * @param auditStatus 审核状态
     * @return 线索列表
     */
    List<Lead> findByAuditStatus(AuditStatus auditStatus);
    
    /**
     * 查找指定时间范围内创建的线索
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 线索列表
     */
    List<Lead> findByCreatedTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 根据投资金额范围查找线索
     * 
     * @param minAmount 最小投资金额
     * @param maxAmount 最大投资金额
     * @return 线索列表
     */
    List<Lead> findByInvestmentAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    /**
     * 根据行业方向查找线索
     * 
     * @param industryDirection 行业方向
     * @return 线索列表
     */
    List<Lead> findByIndustryDirection(String industryDirection);
    
    /**
     * 根据意向区域查找线索
     * 
     * @param intendedRegion 意向区域
     * @return 线索列表
     */
    List<Lead> findByIntendedRegion(String intendedRegion);
    
    /**
     * 统计指定状态的线索数量
     * 
     * @param status 线索状态
     * @return 线索数量
     */
    long countByStatus(LeadStatus status);
    
    /**
     * 统计指定发布者的线索数量
     * 
     * @param publisherId 发布者ID
     * @return 线索数量
     */
    long countByPublisherId(Long publisherId);
}