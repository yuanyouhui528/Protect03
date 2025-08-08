package com.leadexchange.service;

import com.leadexchange.domain.exchange.*;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.event.exchange.ExchangeApplicationEvent;
import com.leadexchange.repository.ExchangeApplicationRepository;
import com.leadexchange.repository.ExchangeHistoryRepository;
import com.leadexchange.service.lead.LeadService;
import com.leadexchange.service.rating.RatingEngineService;
import com.leadexchange.util.ExchangeValueCalculator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 交换引擎核心服务类
 * 负责线索交换的申请、审核、执行等核心业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class ExchangeEngineService {
    
    private static final Logger log = LoggerFactory.getLogger(ExchangeEngineService.class);
    
    private final ExchangeApplicationRepository applicationRepository;
    private final ExchangeHistoryRepository historyRepository;
    private final UserCreditService userCreditService;
    private final LeadService leadService;
    private final RatingEngineService ratingEngineService;
    private final ExchangeValueCalculator valueCalculator;
    private final ApplicationEventPublisher eventPublisher;
    
    // 交换申请过期时间（小时）
    private static final int EXCHANGE_EXPIRY_HOURS = 72;
    
    /**
     * 申请线索交换
     * @param applicantId 申请人ID
     * @param targetLeadId 目标线索ID
     * @param offeredLeadIds 提供的线索ID列表
     * @param reason 交换理由
     * @return 交换申请记录
     */
    @Transactional
    public ExchangeApplication applyForExchange(Long applicantId, Long targetLeadId, 
                                               List<Long> offeredLeadIds, String reason) {
        // 1. 参数验证
        validateExchangeApplication(applicantId, targetLeadId, offeredLeadIds);
        
        // 2. 获取线索信息
        Lead targetLead = leadService.getLeadById(targetLeadId)
                .orElseThrow(() -> new IllegalArgumentException("目标线索不存在，ID：" + targetLeadId));
        List<Lead> offeredLeads = leadService.getLeadsByIds(offeredLeadIds);
        
        // 3. 验证交换条件
        validateExchangeConditions(applicantId, targetLead, offeredLeads);
        
        // 4. 计算交换价值
        BigDecimal targetValue = valueCalculator.calculateLeadValue(targetLead);
        BigDecimal offeredValue = valueCalculator.calculateLeadsValue(offeredLeads);
        BigDecimal creditDifference = valueCalculator.calculateValueDifference(offeredLeads, targetLead);
        
        // 5. 检查用户积分是否足够（如果需要补充积分）
        if (creditDifference.compareTo(BigDecimal.ZERO) > 0) {
            if (!userCreditService.hasEnoughCredits(applicantId, creditDifference)) {
                throw new IllegalStateException("用户积分不足，需要补充积分：" + creditDifference + 
                        "，当前可用积分：" + userCreditService.getUserCredit(applicantId).getAvailableCredits());
            }
            
            // 冻结需要补充的积分
            userCreditService.freezeCredits(applicantId, creditDifference, "EXCHANGE", 
                    null, "交换申请冻结积分，申请ID将在创建后更新");
        }
        
        // 6. 创建交换申请
        ExchangeApplication application = new ExchangeApplication();
        application.setApplicantId(applicantId);
        application.setTargetLeadId(targetLeadId);
        application.setTargetOwnerId(targetLead.getOwnerId());
        application.setOfferedLeadIds(offeredLeadIds.toString());
        application.setRequiredCredits(creditDifference.max(BigDecimal.ZERO));
        application.setTargetLeadValue(targetValue);
        application.setCreditDifference(creditDifference);
        application.setReason(reason);
        application.setStatus(ExchangeStatus.PENDING);
        application.setExpiryTime(LocalDateTime.now().plusHours(EXCHANGE_EXPIRY_HOURS));
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        application.setCreatedBy(applicantId);
        application.setUpdatedBy(applicantId);
        
        application = applicationRepository.save(application);
        
        // 发布交换申请提交事件
        publishExchangeEvent(ExchangeApplicationEvent.EventType.SUBMITTED, application, applicantId, "交换申请已提交");
        
        log.info("交换申请创建成功，申请ID：{}，申请人：{}，目标线索：{}，提供线索：{}，积分差额：{}", 
                application.getId(), applicantId, targetLeadId, offeredLeadIds, creditDifference);
        
        return application;
    }
    
    /**
     * 审核交换申请（同意）
     * @param applicationId 申请ID
     * @param reviewerId 审核人ID
     * @param responseMessage 响应消息
     * @return 更新后的申请记录
     */
    @Transactional
    public ExchangeApplication approveExchange(Long applicationId, Long reviewerId, String responseMessage) {
        ExchangeApplication application = getApplicationById(applicationId);
        
        // 验证审核权限
        if (!application.getTargetLeadOwnerId().equals(reviewerId)) {
            throw new IllegalStateException("只有目标线索的所有者才能审核此申请");
        }
        
        // 验证申请状态
        if (!application.getStatus().canReview()) {
            throw new IllegalStateException("申请状态不允许审核，当前状态：" + application.getStatus());
        }
        
        // 检查是否已过期
        if (application.isExpired()) {
            return expireApplication(application, "申请已过期");
        }
        
        // 执行交换
        executeExchange(application);
        
        // 更新申请状态
        application.approve(responseMessage);
        application.setUpdateBy(reviewerId);
        application = applicationRepository.save(application);
        
        // 发布交换申请批准事件
        publishExchangeEvent(ExchangeApplicationEvent.EventType.APPROVED, application, reviewerId, responseMessage);
        
        log.info("交换申请审核通过，申请ID：{}，审核人：{}，响应消息：{}", 
                applicationId, reviewerId, responseMessage);
        
        return application;
    }
    
    /**
     * 审核交换申请（拒绝）
     * @param applicationId 申请ID
     * @param reviewerId 审核人ID
     * @param responseMessage 拒绝原因
     * @return 更新后的申请记录
     */
    @Transactional
    public ExchangeApplication rejectExchange(Long applicationId, Long reviewerId, String responseMessage) {
        ExchangeApplication application = getApplicationById(applicationId);
        
        // 验证审核权限
        if (!application.getTargetLeadOwnerId().equals(reviewerId)) {
            throw new IllegalStateException("只有目标线索的所有者才能审核此申请");
        }
        
        // 验证申请状态
        if (!application.getStatus().canReview()) {
            throw new IllegalStateException("申请状态不允许审核，当前状态：" + application.getStatus());
        }
        
        // 解冻申请人的积分（如果有冻结的话）
        if (application.getCreditDifference().compareTo(BigDecimal.ZERO) > 0) {
            userCreditService.unfreezeCredits(application.getApplicantId(), 
                    application.getCreditDifference(), "EXCHANGE", applicationId, 
                    "交换申请被拒绝，解冻积分");
        }
        
        // 更新申请状态
        application.reject(responseMessage);
        application.setUpdateBy(reviewerId);
        application = applicationRepository.save(application);
        
        // 发布交换申请拒绝事件
        publishExchangeEvent(ExchangeApplicationEvent.EventType.REJECTED, application, reviewerId, responseMessage);
        
        // 记录交换历史
        recordExchangeHistory(application);
        
        log.info("交换申请被拒绝，申请ID：{}，审核人：{}，拒绝原因：{}", 
                applicationId, reviewerId, responseMessage);
        
        return application;
    }
    
    /**
     * 取消交换申请
     * @param applicationId 申请ID
     * @param userId 用户ID
     * @return 更新后的申请记录
     */
    @Transactional
    public ExchangeApplication cancelExchange(Long applicationId, Long userId) {
        ExchangeApplication application = getApplicationById(applicationId);
        
        // 验证取消权限（只有申请人可以取消）
        if (!application.getApplicantId().equals(userId)) {
            throw new IllegalStateException("只有申请人才能取消此申请");
        }
        
        // 验证申请状态
        if (!application.getStatus().canCancel()) {
            throw new IllegalStateException("申请状态不允许取消，当前状态：" + application.getStatus());
        }
        
        // 解冻申请人的积分（如果有冻结的话）
        if (application.getCreditDifference().compareTo(BigDecimal.ZERO) > 0) {
            userCreditService.unfreezeCredits(application.getApplicantId(), 
                    application.getCreditDifference(), "EXCHANGE", applicationId, 
                    "交换申请被取消，解冻积分");
        }
        
        // 更新申请状态
        application.cancel("用户主动取消");
        application.setUpdateBy(userId);
        application = applicationRepository.save(application);
        
        // 发布交换申请取消事件
        publishExchangeEvent(ExchangeApplicationEvent.EventType.CANCELLED, application, userId, "交换申请已取消");
        
        // 记录交换历史
        recordExchangeHistory(application);
        
        log.info("交换申请被取消，申请ID：{}，取消人：{}", applicationId, userId);
        
        return application;
    }
    
    /**
     * 执行交换逻辑
     * @param application 交换申请
     */
    @Transactional
    public void executeExchange(ExchangeApplication application) {
        Long applicantId = application.getApplicantId();
        Long targetLeadOwnerId = application.getTargetLeadOwnerId();
        Long targetLeadId = application.getTargetLeadId();
        List<Long> offeredLeadIds = parseOfferedLeadIds(application.getOfferedLeadIds());
        BigDecimal creditDifference = application.getCreditDifference();
        
        try {
            // 1. 转移线索所有权
            // 目标线索转给申请人
            leadService.transferLeadOwnership(targetLeadId, applicantId);
            
            // 提供的线索转给目标线索原所有者
            for (Long offeredLeadId : offeredLeadIds) {
                leadService.transferLeadOwnership(offeredLeadId, targetLeadOwnerId);
            }
            
            // 2. 处理积分交易
            if (creditDifference.compareTo(BigDecimal.ZERO) > 0) {
                // 申请人需要补充积分给目标线索所有者
                // 扣减申请人的冻结积分
                userCreditService.deductFrozenCredits(applicantId, creditDifference, 
                        "EXCHANGE", application.getId(), "交换执行，支付积分差额");
                
                // 增加目标线索所有者的积分
                userCreditService.addCredits(targetLeadOwnerId, creditDifference, 
                        "EXCHANGE", application.getId(), "交换执行，收到积分差额");
            } else if (creditDifference.compareTo(BigDecimal.ZERO) < 0) {
                // 目标线索所有者需要补充积分给申请人
                BigDecimal compensationAmount = creditDifference.abs();
                
                // 扣减目标线索所有者的积分
                userCreditService.deductCredits(targetLeadOwnerId, compensationAmount, 
                        "EXCHANGE", application.getId(), "交换执行，支付积分差额");
                
                // 增加申请人的积分
                userCreditService.addCredits(applicantId, compensationAmount, 
                        "EXCHANGE", application.getId(), "交换执行，收到积分差额");
            }
            
            // 3. 更新申请状态为已完成
            application.complete();
            applicationRepository.save(application);
            
            // 发布交换完成事件
            publishExchangeEvent(ExchangeApplicationEvent.EventType.EXCHANGE_COMPLETED, application, null, "交换已完成");
            
            // 5. 记录交换历史
            recordExchangeHistory(application);
            
            log.info("交换执行成功，申请ID：{}，申请人：{}，目标线索所有者：{}，积分差额：{}", 
                    application.getId(), applicantId, targetLeadOwnerId, creditDifference);
            
        } catch (Exception e) {
            log.error("交换执行失败，申请ID：{}，错误信息：{}", application.getId(), e.getMessage(), e);
            
            // 回滚：解冻申请人的积分（如果有的话）
            if (creditDifference.compareTo(BigDecimal.ZERO) > 0) {
                try {
                    userCreditService.unfreezeCredits(applicantId, creditDifference, 
                            "EXCHANGE", application.getId(), "交换执行失败，解冻积分");
                } catch (Exception unfreezeException) {
                    log.error("解冻积分失败，申请ID：{}，用户ID：{}，金额：{}", 
                            application.getId(), applicantId, creditDifference, unfreezeException);
                }
            }
            
            throw new RuntimeException("交换执行失败：" + e.getMessage(), e);
        }
    }
    
    /**
     * 处理过期的交换申请
     * @param application 交换申请
     * @param reason 过期原因
     * @return 更新后的申请记录
     */
    @Transactional
    public ExchangeApplication expireApplication(ExchangeApplication application, String reason) {
        // 解冻申请人的积分（如果有冻结的话）
        if (application.getCreditDifference().compareTo(BigDecimal.ZERO) > 0) {
            userCreditService.unfreezeCredits(application.getApplicantId(), 
                    application.getCreditDifference(), "EXCHANGE", application.getId(), 
                    "交换申请过期，解冻积分");
        }
        
        // 更新申请状态
        application.expire();
        application.setResponseMessage(reason);
        application = applicationRepository.save(application);
        
        // 发布交换申请过期事件
        publishExchangeEvent(ExchangeApplicationEvent.EventType.EXCHANGE_EXPIRED, application, null, reason);
        
        // 记录交换历史
        recordExchangeHistory(application);
        
        log.info("交换申请已过期，申请ID：{}，过期原因：{}", application.getId(), reason);
        
        return application;
    }
    
    /**
     * 记录交换历史
     * @param application 交换申请
     */
    @Transactional
    public void recordExchangeHistory(ExchangeApplication application) {
        ExchangeHistory history = ExchangeHistory.fromApplication(
                application, 
                "申请人", // TODO: 从用户服务获取真实姓名
                "目标所有者", // TODO: 从用户服务获取真实姓名
                "目标线索", // TODO: 从线索服务获取真实标题
                "提供线索" // TODO: 从线索服务获取真实标题列表
        );
        historyRepository.save(history);
        
        log.debug("交换历史记录已保存，申请ID：{}，最终状态：{}", 
                application.getId(), application.getStatus());
    }
    
    /**
     * 解析提供的线索ID列表
     * 
     * @param offeredLeadIds 提供的线索ID列表（JSON格式字符串）
     * @return 线索ID列表
     */
    private List<Long> parseOfferedLeadIds(String offeredLeadIds) {
        List<Long> leadIds = new ArrayList<>();
        
        if (offeredLeadIds == null || offeredLeadIds.trim().isEmpty()) {
            return leadIds;
        }
        
        try {
            // 简单解析JSON数组格式的字符串，如 "[1,2,3]" 或 "1,2,3"
            String cleaned = offeredLeadIds.trim().replaceAll("[\\[\\]]", "");
            String[] ids = cleaned.split(",");
            
            for (String id : ids) {
                String trimmedId = id.trim();
                if (!trimmedId.isEmpty()) {
                    leadIds.add(Long.parseLong(trimmedId));
                }
            }
        } catch (Exception e) {
            log.error("解析提供的线索ID列表失败，原始数据：{}，错误信息：{}", offeredLeadIds, e.getMessage());
            // 解析失败时返回空列表
        }
        
        return leadIds;
    }
    
    /**
     * 发布交换申请事件
     * 
     * @param eventType 事件类型
     * @param application 交换申请
     * @param operatorId 操作人ID
     * @param remark 备注
     */
    private void publishExchangeEvent(ExchangeApplicationEvent.EventType eventType, 
                                    ExchangeApplication application, 
                                    Long operatorId, 
                                    String remark) {
        try {
            ExchangeApplicationEvent event = new ExchangeApplicationEvent(
                    this, eventType, application, operatorId, remark, null);
            eventPublisher.publishEvent(event);
            log.debug("交换申请事件已发布，事件类型：{}，申请ID：{}", eventType, application.getId());
        } catch (Exception e) {
            log.error("发布交换申请事件失败，事件类型：{}，申请ID：{}", eventType, application.getId(), e);
        }
    }
    
    /**
     * 获取交换申请详情
     * @param applicationId 申请ID
     * @return 交换申请
     */
    @Transactional(readOnly = true)
    public ExchangeApplication getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("交换申请不存在，ID：" + applicationId));
    }
    
    /**
     * 获取用户的交换申请列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    @Transactional(readOnly = true)
    public Page<ExchangeApplication> getUserApplications(Long userId, Pageable pageable) {
        return applicationRepository.findByApplicantIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 获取用户收到的交换申请列表
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交换申请分页列表
     */
    @Transactional(readOnly = true)
    public Page<ExchangeApplication> getReceivedApplications(Long userId, Pageable pageable) {
        return applicationRepository.findByTargetOwnerIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 获取用户的交换历史
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 交换历史分页列表
     */
    @Transactional(readOnly = true)
    public Page<ExchangeHistory> getUserExchangeHistory(Long userId, Pageable pageable) {
        return historyRepository.findByApplicantIdOrTargetOwnerIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 批量处理过期申请
     * @return 处理的过期申请数量
     */
    @Transactional
    public int processExpiredApplications() {
        List<ExchangeApplication> expiredApplications = applicationRepository.findExpiredApplications(LocalDateTime.now());
        
        int processedCount = 0;
        for (ExchangeApplication application : expiredApplications) {
            try {
                expireApplication(application, "系统自动过期处理");
                processedCount++;
            } catch (Exception e) {
                log.error("处理过期申请失败，申请ID：{}，错误信息：{}", 
                        application.getId(), e.getMessage(), e);
            }
        }
        
        log.info("批量处理过期申请完成，总数：{}，成功：{}", expiredApplications.size(), processedCount);
        
        return processedCount;
    }
    
    /**
     * 验证交换申请参数
     */
    private void validateExchangeApplication(Long applicantId, Long targetLeadId, List<Long> offeredLeadIds) {
        if (applicantId == null) {
            throw new IllegalArgumentException("申请人ID不能为空");
        }
        if (targetLeadId == null) {
            throw new IllegalArgumentException("目标线索ID不能为空");
        }
        if (CollectionUtils.isEmpty(offeredLeadIds)) {
            throw new IllegalArgumentException("提供的线索ID列表不能为空");
        }
        if (offeredLeadIds.contains(targetLeadId)) {
            throw new IllegalArgumentException("不能用目标线索作为交换条件");
        }
    }
    
    /**
     * 验证交换条件
     */
    private void validateExchangeConditions(Long applicantId, Lead targetLead, List<Lead> offeredLeads) {
        // 检查目标线索是否属于申请人
        if (targetLead.getOwnerId().equals(applicantId)) {
            throw new IllegalArgumentException("不能申请交换自己的线索");
        }
        
        // 检查目标线索状态是否允许交换
        if (!targetLead.isExchangeable()) {
            throw new IllegalArgumentException("目标线索当前状态不允许交换");
        }
        
        // 检查提供的线索是否都属于申请人
        for (Lead offeredLead : offeredLeads) {
            if (!offeredLead.getOwnerId().equals(applicantId)) {
                throw new IllegalArgumentException("只能用自己的线索进行交换，线索ID：" + offeredLead.getId());
            }
            if (!offeredLead.isExchangeable()) {
                throw new IllegalArgumentException("提供的线索状态不允许交换，线索ID：" + offeredLead.getId());
            }
        }
        
        // 检查是否存在重复的交换申请
        boolean hasPendingApplication = applicationRepository.existsByApplicantIdAndTargetLeadIdAndStatus(
                applicantId, targetLead.getId(), ExchangeStatus.PENDING);
        if (hasPendingApplication) {
            throw new IllegalArgumentException("已存在针对此线索的待处理交换申请");
        }
    }
}