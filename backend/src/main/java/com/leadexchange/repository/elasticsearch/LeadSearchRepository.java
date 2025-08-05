package com.leadexchange.repository.elasticsearch;

import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.lead.LeadStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 线索搜索仓库接口
 * 基于Elasticsearch的全文检索功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface LeadSearchRepository extends ElasticsearchRepository<Lead, Long> {

    /**
     * 根据企业名称搜索线索
     * 
     * @param companyName 企业名称
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByCompanyNameContaining(String companyName, Pageable pageable);

    /**
     * 根据行业方向搜索线索
     * 
     * @param industryDirection 行业方向
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByIndustryDirectionContaining(String industryDirection, Pageable pageable);

    /**
     * 根据意向区域搜索线索
     * 
     * @param intendedRegion 意向区域
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByIntendedRegionContaining(String intendedRegion, Pageable pageable);

    /**
     * 根据线索状态搜索
     * 
     * @param status 线索状态
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByStatus(LeadStatus status, Pageable pageable);

    /**
     * 根据线索评级搜索
     * 
     * @param rating 线索评级
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByRating(LeadRating rating, Pageable pageable);

    /**
     * 根据投资额范围搜索
     * 
     * @param minAmount 最小投资额
     * @param maxAmount 最大投资额
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByInvestmentAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    /**
     * 多字段关键词搜索
     * 
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 搜索结果
     */
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"companyName^2\", \"industryDirection^1.5\", \"description\"]}}")
    Page<Lead> searchByKeyword(String keyword, Pageable pageable);

    /**
     * 根据所有者ID查询线索
     * 
     * @param ownerId 所有者ID
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByOwnerId(Long ownerId, Pageable pageable);

    /**
     * 查询已发布的线索
     * 
     * @param pageable 分页参数
     * @return 搜索结果
     */
    Page<Lead> findByStatusAndAuditStatus(LeadStatus status, com.leadexchange.domain.lead.AuditStatus auditStatus, Pageable pageable);
}