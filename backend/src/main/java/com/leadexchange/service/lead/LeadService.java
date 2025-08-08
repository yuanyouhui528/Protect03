package com.leadexchange.service.lead;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.AuditStatus;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.service.rating.RatingEngineService.RatingTrendData;
import com.leadexchange.service.rating.RatingEngineService.RatingBatchCondition;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 线索服务接口
 * 定义线索管理相关的业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface LeadService {

    /**
     * 创建线索
     * 
     * @param lead 线索信息
     * @return 创建的线索
     */
    Lead createLead(Lead lead);

    /**
     * 更新线索信息
     * 
     * @param lead 线索信息
     * @return 更新后的线索
     */
    Lead updateLead(Lead lead);

    /**
     * 删除线索（软删除）
     * 
     * @param id 线索ID
     * @return 是否删除成功
     */
    boolean deleteLead(Long id);

    /**
     * 根据ID获取线索详情
     * 
     * @param id 线索ID
     * @return 线索详情
     */
    Lead getLeadByIdRequired(Long id);

    /**
     * 分页查询线索列表
     * 
     * @param pageable 分页参数
     * @param ownerId 所有者ID（可选）
     * @param status 状态（可选）
     * @return 线索分页列表
     */
    Page<Lead> getLeadPage(Pageable pageable, Long ownerId, LeadStatus status);

    /**
     * 搜索线索
     * 
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> searchLeads(String keyword, Pageable pageable);

    /**
     * 发布线索
     * 
     * @param id 线索ID
     * @return 是否发布成功
     */
    boolean publishLead(Long id);

    /**
     * 下架线索
     * 
     * @param id 线索ID
     * @return 是否下架成功
     */
    boolean offlineLead(Long id);

    /**
     * 审核线索
     * 
     * @param id 线索ID
     * @param auditStatus 审核状态
     * @param auditRemark 审核备注
     * @param auditorId 审核人ID
     * @return 是否审核成功
     */
    boolean auditLead(Long id, AuditStatus auditStatus, String auditRemark, Long auditorId);

    /**
     * 更新线索状态
     * 
     * @param id 线索ID
     * @param status 新状态
     * @return 是否更新成功
     */
    boolean updateLeadStatus(Long id, LeadStatus status);

    /**
     * 检测线索重复
     * 
     * @param lead 待检测的线索
     * @return 重复的线索列表
     */
    List<Lead> detectDuplicateLeads(Lead lead);

    /**
     * 计算线索评级
     * 
     * @param lead 线索信息
     * @return 评级分数
     */
    int calculateLeadRating(Lead lead);

    /**
     * 增加浏览次数
     * 
     * @param id 线索ID
     * @param userId 用户ID（可选）
     * @param ipAddress IP地址
     */
    void incrementViewCount(Long id, Long userId, String ipAddress);

    /**
     * 获取用户收藏的线索
     * 
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 收藏的线索列表
     */
    Page<Lead> getFavoriteLeads(Long userId, Pageable pageable);

    /**
     * 收藏线索
     * 
     * @param userId 用户ID
     * @param leadId 线索ID
     * @return 是否收藏成功
     */
    boolean favoriteLead(Long userId, Long leadId);

    /**
     * 取消收藏线索
     * 
     * @param userId 用户ID
     * @param leadId 线索ID
     * @return 是否取消成功
     */
    boolean unfavoriteLead(Long userId, Long leadId);

    /**
     * 获取评级分布统计
     * 
     * @return 评级分布数据
     */
    Map<LeadRating, Long> getRatingDistribution();

    /**
     * 获取评级趋势数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param granularity 时间粒度（day/week/month）
     * @return 趋势数据列表
     */
    List<RatingTrendData> getRatingTrend(LocalDateTime startTime, LocalDateTime endTime, String granularity);

    /**
     * 根据条件获取线索ID列表
     * 
     * @param condition 筛选条件
     * @return 线索ID列表
     */
    List<Long> getLeadIdsByCondition(RatingBatchCondition condition);

    /**
     * 根据ID获取线索（可选）
     * 
     * @param id 线索ID
     * @return 线索对象
     */
    Optional<Lead> getLeadById(Long id);

    /**
     * 更新线索评级
     * 
     * @param leadId 线索ID
     * @param rating 新评级
     * @param score 新分数
     * @return 是否更新成功
     */
    boolean updateLeadRating(Long leadId, LeadRating rating, Double score);

    /**
     * 根据ID列表批量获取线索
     * 
     * @param ids 线索ID列表
     * @return 线索列表
     */
    List<Lead> getLeadsByIds(List<Long> ids);

    /**
     * 转移线索所有权
     * 
     * @param leadId 线索ID
     * @param newOwnerId 新所有者ID
     * @return 是否转移成功
     */
    boolean transferLeadOwnership(Long leadId, Long newOwnerId);
}