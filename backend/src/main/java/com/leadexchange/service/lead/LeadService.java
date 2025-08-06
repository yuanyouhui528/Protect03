package com.leadexchange.service.lead;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.AuditStatus;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

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
    Lead getLeadById(Long id);

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
}