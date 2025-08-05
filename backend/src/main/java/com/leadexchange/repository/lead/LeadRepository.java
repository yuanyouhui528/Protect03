package com.leadexchange.repository.lead;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadRating;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.AuditStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 线索数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface LeadRepository extends BaseMapper<Lead> {

    /**
     * 根据用户ID查询线索列表
     * 
     * @param ownerId 所有者ID
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE owner_id = #{ownerId} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Lead> selectByOwnerId(@Param("ownerId") Long ownerId, Page<Lead> page);

    /**
     * 根据状态查询线索
     * 
     * @param status 线索状态
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Lead> selectByStatus(@Param("status") LeadStatus status, Page<Lead> page);

    /**
     * 根据评级查询线索
     * 
     * @param rating 线索评级
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE rating = #{rating} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Lead> selectByRating(@Param("rating") LeadRating rating, Page<Lead> page);

    /**
     * 根据时间范围查询线索
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE create_time BETWEEN #{startTime} AND #{endTime} AND deleted = 0 ORDER BY create_time DESC")
    IPage<Lead> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime, 
                                  Page<Lead> page);

    /**
     * 统计用户线索数量
     * 
     * @param ownerId 所有者ID
     * @return 线索数量
     */
    @Select("SELECT COUNT(*) FROM leads WHERE owner_id = #{ownerId} AND deleted = 0")
    Long countByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 查询热门线索（按浏览量排序）
     * 
     * @param page 分页参数
     * @return 热门线索列表
     */
    @Select("SELECT * FROM leads WHERE status = 'PUBLISHED' AND audit_status = 'APPROVED' AND deleted = 0 ORDER BY view_count DESC, create_time DESC")
    IPage<Lead> selectHotLeads(Page<Lead> page);

    /**
     * 根据投资额范围查询线索
     * 
     * @param minAmount 最小投资额
     * @param maxAmount 最大投资额
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE investment_amount BETWEEN #{minAmount} AND #{maxAmount} AND deleted = 0 ORDER BY investment_amount DESC")
    IPage<Lead> selectByInvestmentRange(@Param("minAmount") BigDecimal minAmount, 
                                        @Param("maxAmount") BigDecimal maxAmount, 
                                        Page<Lead> page);

    /**
     * 查询已发布且审核通过的线索
     * 
     * @param page 分页参数
     * @return 线索分页列表
     */
    @Select("SELECT * FROM leads WHERE status = 'PUBLISHED' AND audit_status = 'APPROVED' AND deleted = 0 ORDER BY create_time DESC")
    IPage<Lead> selectPublishedLeads(Page<Lead> page);

    /**
     * 更新线索状态
     * 
     * @param id 线索ID
     * @param status 新状态
     * @return 更新行数
     */
    @Update("UPDATE leads SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") LeadStatus status);

    /**
     * 更新线索评级
     * 
     * @param id 线索ID
     * @param rating 新评级
     * @param ratingScore 评级分数
     * @return 更新行数
     */
    @Update("UPDATE leads SET rating = #{rating}, rating_score = #{ratingScore}, update_time = NOW() WHERE id = #{id}")
    int updateRating(@Param("id") Long id, @Param("rating") LeadRating rating, @Param("ratingScore") Integer ratingScore);

    /**
     * 更新审核状态
     * 
     * @param id 线索ID
     * @param auditStatus 审核状态
     * @param auditorId 审核人ID
     * @param auditRemark 审核备注
     * @return 更新行数
     */
    @Update("UPDATE leads SET audit_status = #{auditStatus}, auditor_id = #{auditorId}, audit_remark = #{auditRemark}, audit_time = NOW(), update_time = NOW() WHERE id = #{id}")
    int updateAuditStatus(@Param("id") Long id, 
                          @Param("auditStatus") AuditStatus auditStatus, 
                          @Param("auditorId") Long auditorId, 
                          @Param("auditRemark") String auditRemark);
}