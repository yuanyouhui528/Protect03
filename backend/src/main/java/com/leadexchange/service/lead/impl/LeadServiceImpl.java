package com.leadexchange.service.lead.impl;

import com.leadexchange.service.lead.LeadService;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.lead.AuditStatus;
import com.leadexchange.domain.lead.LeadFavorite;
import com.leadexchange.domain.lead.LeadView;
import com.leadexchange.repository.lead.LeadRepository;
import com.leadexchange.repository.lead.LeadViewRepository;
import com.leadexchange.repository.lead.LeadFavoriteRepository;
import com.leadexchange.util.EditDistanceUtil;
// import com.leadexchange.repository.elasticsearch.LeadSearchRepository;
// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 线索服务实现类
 * 实现线索管理相关的业务逻辑
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class LeadServiceImpl implements LeadService {

    private static final Logger logger = LoggerFactory.getLogger(LeadServiceImpl.class);

    @Autowired
    private LeadRepository leadRepository;
    
    @Autowired
    private LeadViewRepository leadViewRepository;
    
    // @Autowired
    // private LeadFavoriteRepository leadFavoriteRepository;
    
    // @Autowired
    // private LeadSearchRepository leadSearchRepository;

    @Override
    public Lead createLead(Lead lead) {
        logger.info("创建线索: {}", lead.getCompanyName());
        
        // 设置初始状态
        lead.setStatus(LeadStatus.DRAFT);
        lead.setAuditStatus(AuditStatus.PENDING);
        
        // 计算评级
        int ratingScore = calculateLeadRating(lead);
        lead.setRatingScore(ratingScore);
        lead.setRating(LeadRating.fromScore(ratingScore));
        
        // 保存到数据库
        leadRepository.save(lead);
        
        // 同步到Elasticsearch (暂时注释)
        // try {
        //     leadSearchRepository.save(lead);
        // } catch (Exception e) {
        //     logger.warn("同步线索到Elasticsearch失败: {}", e.getMessage());
        // }
        
        logger.info("线索创建成功，ID: {}", lead.getId());
        return lead;
    }

    @Override
    public Lead updateLead(Lead lead) {
        logger.info("更新线索: {}", lead.getId());
        
        Lead existingLead = leadRepository.findById(lead.getId()).orElse(null);
        if (existingLead == null) {
            throw new IllegalArgumentException("线索不存在: " + lead.getId());
        }
        
        // 重新计算评级
        int ratingScore = calculateLeadRating(lead);
        lead.setRatingScore(ratingScore);
        lead.setRating(LeadRating.fromScore(ratingScore));
        
        // 更新数据库
        leadRepository.save(lead);
        
        // 同步到Elasticsearch (暂时注释)
        // try {
        //     leadSearchRepository.save(lead);
        // } catch (Exception e) {
        //     logger.warn("同步线索到Elasticsearch失败: {}", e.getMessage());
        // }
        
        logger.info("线索更新成功: {}", lead.getId());
        return lead;
    }

    @Override
    public boolean deleteLead(Long id) {
        logger.info("删除线索: {}", id);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null) {
            logger.warn("线索不存在: {}", id);
            return false;
        }
        
        // 软删除
        lead.setDeleted(1);
        leadRepository.save(lead);
        
        // 从Elasticsearch删除 (暂时注释)
        // try {
        //     leadSearchRepository.deleteById(id);
        // } catch (Exception e) {
        //     logger.warn("从Elasticsearch删除线索失败: {}", e.getMessage());
        // }
        
        logger.info("线索删除成功: {}", id);
        return true;
    }

    @Override
    public Lead getLeadById(Long id) {
        logger.debug("获取线索详情: {}", id);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null || lead.getDeleted() == 1) {
            logger.warn("线索不存在或已删除: {}", id);
            return null;
        }
        
        return lead;
    }

    @Override
    public Page<Lead> getLeadPage(Pageable pageable, Long ownerId, LeadStatus status) {
        logger.debug("分页查询线索列表，ownerId: {}, status: {}", ownerId, status);
        
        // 暂时返回所有数据，后续可以添加自定义查询方法
        return leadRepository.findAll(pageable);
    }

    @Override
    public Page<Lead> searchLeads(String keyword, Pageable pageable) {
        logger.debug("搜索线索，关键词: {}", keyword);
        
        if (!StringUtils.hasText(keyword)) {
            return getLeadPage(pageable, null, LeadStatus.PUBLISHED);
        }
        
        // 暂时返回所有数据，后续可以添加自定义搜索方法
        return leadRepository.findAll(pageable);
    }

    @Override
    public boolean publishLead(Long id) {
        logger.info("发布线索: {}", id);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null) {
            logger.warn("线索不存在: {}", id);
            return false;
        }
        
        // 暂时注释审核状态检查
        // if (!lead.getAuditStatus().isApproved()) {
        //     logger.warn("线索未通过审核，无法发布: {}", id);
        //     return false;
        // }
        
        lead.setStatus(LeadStatus.PUBLISHED);
        lead.setPublishedTime(LocalDateTime.now());
        leadRepository.save(lead);
        
        logger.info("线索发布成功: {}", id);
        return true;
    }

    @Override
    public boolean offlineLead(Long id) {
        logger.info("下架线索: {}", id);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null) {
            logger.warn("线索不存在: {}", id);
            return false;
        }
        
        lead.setStatus(LeadStatus.OFFLINE);
        leadRepository.save(lead);
        
        logger.info("线索下架成功: {}", id);
        return true;
    }

    @Override
    public boolean auditLead(Long id, AuditStatus auditStatus, String auditRemark, Long auditorId) {
        logger.info("审核线索: {}, 状态: {}", id, auditStatus);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null) {
            logger.warn("线索不存在: {}", id);
            return false;
        }
        
        lead.setAuditStatus(auditStatus);
        lead.setAuditRemark(auditRemark);
        lead.setAuditorId(auditorId);
        lead.setAuditTime(LocalDateTime.now());
        
        // 如果审核通过，自动发布
        // if (auditStatus.isApproved()) {
        //     lead.setStatus(LeadStatus.PUBLISHED);
        //     lead.setPublishedTime(LocalDateTime.now());
        // } else if (auditStatus == AuditStatus.REJECTED) {
        //     lead.setStatus(LeadStatus.REJECTED);
        // }
        
        leadRepository.save(lead);
        
        logger.info("线索审核完成: {}", id);
        return true;
    }

    @Override
    public boolean updateLeadStatus(Long id, LeadStatus status) {
        logger.info("更新线索状态: {}, 状态: {}", id, status);
        
        Lead lead = leadRepository.findById(id).orElse(null);
        if (lead == null) {
            logger.warn("线索不存在: {}", id);
            return false;
        }
        
        lead.setStatus(status);
        leadRepository.save(lead);
        
        logger.info("线索状态更新成功: {}", id);
        return true;
    }

    @Override
    public List<Lead> detectDuplicateLeads(Lead lead) {
        logger.debug("检测线索重复: {}", lead.getCompanyName());
        
        List<Lead> duplicates = new ArrayList<>();
        
        try {
            // 1. 获取所有已发布的线索（排除当前线索）
            List<Lead> allLeads;
            if (lead.getId() != null) {
                allLeads = leadRepository.findByStatusAndIdNot(LeadStatus.PUBLISHED, lead.getId());
            } else {
                allLeads = leadRepository.findByStatus(LeadStatus.PUBLISHED);
            }
            
            // 2. 设置相似度阈值
            double companyNameThreshold = 0.8;  // 公司名称相似度阈值
            double contactPhoneThreshold = 0.9;  // 联系电话相似度阈值
            double emailThreshold = 0.9;         // 邮箱相似度阈值
            double descriptionThreshold = 0.7;   // 项目描述相似度阈值
            
            // 3. 遍历所有线索进行相似度比较
            for (Lead existingLead : allLeads) {
                boolean isDuplicate = false;
                
                // 检查公司名称相似度
                if (StringUtils.hasText(lead.getCompanyName()) && 
                    StringUtils.hasText(existingLead.getCompanyName())) {
                    double companySimilarity = EditDistanceUtil.calculateChineseSimilarity(
                        lead.getCompanyName(), existingLead.getCompanyName());
                    if (companySimilarity >= companyNameThreshold) {
                        isDuplicate = true;
                        logger.debug("发现公司名称相似线索: {} vs {}, 相似度: {}", 
                            lead.getCompanyName(), existingLead.getCompanyName(), companySimilarity);
                    }
                }
                
                // 检查联系电话相似度
                if (!isDuplicate && StringUtils.hasText(lead.getContactPhone()) && 
                    StringUtils.hasText(existingLead.getContactPhone())) {
                    double phoneSimilarity = EditDistanceUtil.calculateSimilarity(
                        lead.getContactPhone(), existingLead.getContactPhone());
                    if (phoneSimilarity >= contactPhoneThreshold) {
                        isDuplicate = true;
                        logger.debug("发现联系电话相似线索: {} vs {}, 相似度: {}", 
                            lead.getContactPhone(), existingLead.getContactPhone(), phoneSimilarity);
                    }
                }
                
                // 检查邮箱相似度
                if (!isDuplicate && StringUtils.hasText(lead.getContactEmail()) && 
                    StringUtils.hasText(existingLead.getContactEmail())) {
                    double emailSimilarity = EditDistanceUtil.calculateSimilarity(
                        lead.getContactEmail(), existingLead.getContactEmail());
                    if (emailSimilarity >= emailThreshold) {
                        isDuplicate = true;
                        logger.debug("发现邮箱相似线索: {} vs {}, 相似度: {}", 
                            lead.getContactEmail(), existingLead.getContactEmail(), emailSimilarity);
                    }
                }
                
                // 检查项目描述相似度（作为辅助判断）
                if (!isDuplicate && StringUtils.hasText(lead.getDescription()) && 
                    StringUtils.hasText(existingLead.getDescription()) &&
                    lead.getDescription().length() > 20 && existingLead.getDescription().length() > 20) {
                    double descriptionSimilarity = EditDistanceUtil.calculateChineseSimilarity(
                        lead.getDescription(), existingLead.getDescription());
                    if (descriptionSimilarity >= descriptionThreshold) {
                        // 项目描述相似度高，需要结合其他条件判断
                        boolean hasOtherSimilarity = false;
                        
                        // 检查投资金额是否相近（差异在20%以内）
                        if (lead.getInvestmentAmount() != null && existingLead.getInvestmentAmount() != null) {
                            double amountDiff = Math.abs(lead.getInvestmentAmount().doubleValue() - 
                                existingLead.getInvestmentAmount().doubleValue());
                            double maxAmount = Math.max(lead.getInvestmentAmount().doubleValue(), 
                                existingLead.getInvestmentAmount().doubleValue());
                            if (maxAmount > 0 && amountDiff / maxAmount <= 0.2) {
                                hasOtherSimilarity = true;
                            }
                        }
                        
                        // 检查行业方向是否相同
                        if (!hasOtherSimilarity && StringUtils.hasText(lead.getIndustryDirection()) && 
                            StringUtils.hasText(existingLead.getIndustryDirection()) &&
                            lead.getIndustryDirection().equals(existingLead.getIndustryDirection())) {
                            hasOtherSimilarity = true;
                        }
                        
                        if (hasOtherSimilarity) {
                            isDuplicate = true;
                            logger.debug("发现项目描述相似线索: 相似度: {}", descriptionSimilarity);
                        }
                    }
                }
                
                if (isDuplicate) {
                    duplicates.add(existingLead);
                }
            }
            
        } catch (Exception e) {
            logger.error("检测线索重复时发生异常", e);
        }
        
        logger.debug("发现 {} 个可能重复的线索", duplicates.size());
        return duplicates;
    }

    @Override
    public int calculateLeadRating(Lead lead) {
        logger.debug("计算线索评级: {}", lead.getCompanyName());
        
        int score = 0;
        
        // 基础信息完整度 (40分)
        if (StringUtils.hasText(lead.getCompanyName())) score += 10;
        if (StringUtils.hasText(lead.getContactPerson())) score += 10;
        if (StringUtils.hasText(lead.getContactPhone())) score += 10;
        if (StringUtils.hasText(lead.getDescription())) score += 10;
        
        // 企业规模 (30分)
        if (lead.getRegisteredCapital() != null) {
            if (lead.getRegisteredCapital().doubleValue() >= 1000) {
                score += 30; // 注册资本1000万以上
            } else if (lead.getRegisteredCapital().doubleValue() >= 100) {
                score += 20; // 注册资本100-1000万
            } else {
                score += 10; // 注册资本100万以下
            }
        }
        
        // 投资金额 (20分)
        if (lead.getInvestmentAmount() != null) {
            if (lead.getInvestmentAmount().doubleValue() >= 5000) {
                score += 20; // 投资额5000万以上
            } else if (lead.getInvestmentAmount().doubleValue() >= 1000) {
                score += 15; // 投资额1000-5000万
            } else {
                score += 10; // 投资额1000万以下
            }
        }
        
        // 行业方向 (10分)
        if (StringUtils.hasText(lead.getIndustryDirection())) {
            score += 10;
        }
        
        logger.debug("线索评级计算完成，分数: {}", score);
        return score;
    }

    @Override
    public void incrementViewCount(Long id, Long userId, String ipAddress) {
        logger.debug("增加线索浏览次数: leadId={}, userId={}, ip={}", id, userId, ipAddress);
        
        try {
            // 1. 检查线索是否存在
            Lead lead = leadRepository.findById(id).orElse(null);
            if (lead == null || lead.getDeleted() == 1) {
                logger.warn("线索不存在或已删除: {}", id);
                return;
            }
            
            // 2. 防止短时间内重复统计（5分钟内同一用户或IP不重复计数）
            LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
            boolean recentlyViewed = leadViewRepository.existsByLeadIdAndUserOrIpAndViewTimeAfter(
                id, userId, ipAddress, fiveMinutesAgo);
            
            if (recentlyViewed) {
                logger.debug("用户或IP在5分钟内已浏览过该线索，跳过重复统计: leadId={}, userId={}, ip={}", 
                    id, userId, ipAddress);
                return;
            }
            
            // 3. 创建浏览记录
            LeadView leadView = new LeadView(id, userId, ipAddress);
            leadView.setCreatedBy(userId);
            leadView.setCreatedTime(LocalDateTime.now());
            
            // 4. 保存浏览记录
            leadViewRepository.save(leadView);
            
            // 5. 更新线索的浏览次数（如果Lead实体有viewCount字段）
            // 这里可以选择实时更新或者通过定时任务批量更新
            long totalViews = leadViewRepository.countByLeadId(id);
            logger.debug("线索浏览记录保存成功: leadId={}, 总浏览次数={}", id, totalViews);
            
        } catch (Exception e) {
            logger.error("记录线索浏览失败: leadId=" + id, e);
        }
    }

    @Override
    public Page<Lead> getFavoriteLeads(Long userId, Pageable pageable) {
        logger.debug("获取用户收藏的线索: {}", userId);
        
        // 获取用户收藏的线索ID列表
        List<Long> favoriteLeadIds = leadFavoriteRepository.getFavoriteLeadIdsByUserId(userId);
        
        if (favoriteLeadIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        // 根据线索ID列表查询线索详情
        List<Lead> favoriteLeads = leadRepository.findAllById(favoriteLeadIds);
        
        // 手动分页处理
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), favoriteLeads.size());
        
        List<Lead> pageContent = favoriteLeads.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, favoriteLeads.size());
    }

    @Override
    public boolean favoriteLead(Long userId, Long leadId) {
        logger.info("收藏线索: userId={}, leadId={}", userId, leadId);
        
        try {
            // 1. 检查线索是否存在
            Lead lead = leadRepository.findById(leadId).orElse(null);
            if (lead == null || lead.getDeleted() == 1) {
                logger.warn("线索不存在或已删除: {}", leadId);
                return false;
            }
            
            // 2. 检查是否已收藏
            boolean isFavorited = leadFavoriteRepository.isFavorited(leadId, userId);
            if (isFavorited) {
                logger.warn("用户已收藏此线索: userId={}, leadId={}", userId, leadId);
                return false;
            }
            
            // 3. 创建收藏记录
            LeadFavorite leadFavorite = new LeadFavorite(userId, leadId);
            int inserted = leadFavoriteRepository.insert(leadFavorite);
            if (inserted > 0) {
                // 4. 更新线索收藏数
                int favoriteCount = leadFavoriteRepository.countByLeadId(leadId);
                lead.setFavoriteCount(favoriteCount);
                leadRepository.save(lead);
                
                logger.info("收藏成功: userId={}, leadId={}", userId, leadId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("收藏线索失败: userId=" + userId + ", leadId=" + leadId, e);
            return false;
        }
    }

    @Override
    public boolean unfavoriteLead(Long userId, Long leadId) {
        logger.info("取消收藏线索: userId={}, leadId={}", userId, leadId);
        
        try {
            // 检查线索是否存在
            Lead lead = leadRepository.findById(leadId)
                    .orElseThrow(() -> new RuntimeException("线索不存在"));
            
            // 检查是否已收藏
            boolean isFavorited = leadFavoriteRepository.isFavorited(leadId, userId);
            if (!isFavorited) {
                logger.warn("该线索未被收藏: leadId={}, userId={}", leadId, userId);
                return false;
            }
            
            // 删除收藏记录
            int deleted = leadFavoriteRepository.deleteByLeadIdAndUserId(leadId, userId);
            if (deleted > 0) {
                // 更新线索的收藏次数
                int favoriteCount = leadFavoriteRepository.countByLeadId(leadId);
                lead.setFavoriteCount(favoriteCount);
                leadRepository.save(lead);
                
                logger.info("取消收藏成功: leadId={}, userId={}", leadId, userId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            logger.error("取消收藏失败: leadId=" + leadId + ", userId=" + userId, e);
            return false;
        }
    }
}