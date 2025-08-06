package com.leadexchange.repository.lead;

import com.leadexchange.domain.lead.LeadView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线索浏览记录数据访问层接口
 * 继承JpaRepository，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface LeadViewRepository extends JpaRepository<LeadView, Long> {
    
    /**
     * 根据线索ID查找浏览记录
     * 
     * @param leadId 线索ID
     * @return 浏览记录列表
     */
    List<LeadView> findByLeadId(Long leadId);
    
    /**
     * 根据用户ID查找浏览记录
     * 
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    List<LeadView> findByUserId(Long userId);
    
    /**
     * 根据线索ID和用户ID查找浏览记录
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @return 浏览记录列表
     */
    List<LeadView> findByLeadIdAndUserId(Long leadId, Long userId);
    
    /**
     * 统计线索的浏览次数
     * 
     * @param leadId 线索ID
     * @return 浏览次数
     */
    long countByLeadId(Long leadId);
    
    /**
     * 统计线索的独立访客数（去重用户ID）
     * 
     * @param leadId 线索ID
     * @return 独立访客数
     */
    @Query("SELECT COUNT(DISTINCT lv.userId) FROM LeadView lv WHERE lv.leadId = :leadId AND lv.userId IS NOT NULL")
    long countDistinctUsersByLeadId(@Param("leadId") Long leadId);
    
    /**
     * 统计用户的浏览次数
     * 
     * @param userId 用户ID
     * @return 浏览次数
     */
    long countByUserId(Long userId);
    
    /**
     * 查找指定时间范围内的浏览记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 浏览记录列表
     */
    List<LeadView> findByViewTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查找指定线索在指定时间范围内的浏览记录
     * 
     * @param leadId 线索ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 浏览记录列表
     */
    List<LeadView> findByLeadIdAndViewTimeBetween(Long leadId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内线索的浏览次数
     * 
     * @param leadId 线索ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 浏览次数
     */
    long countByLeadIdAndViewTimeBetween(Long leadId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 检查用户是否在指定时间内浏览过该线索（防止重复统计）
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param startTime 开始时间
     * @return 是否存在浏览记录
     */
    @Query("SELECT COUNT(lv) > 0 FROM LeadView lv WHERE lv.leadId = :leadId " +
           "AND (lv.userId = :userId OR lv.ipAddress = :ipAddress) " +
           "AND lv.viewTime >= :startTime")
    boolean existsByLeadIdAndUserOrIpAndViewTimeAfter(
        @Param("leadId") Long leadId, 
        @Param("userId") Long userId, 
        @Param("ipAddress") String ipAddress, 
        @Param("startTime") LocalDateTime startTime);
    
    /**
     * 获取线索的最近浏览记录
     * 
     * @param leadId 线索ID
     * @param limit 限制数量
     * @return 最近浏览记录列表
     */
    @Query("SELECT lv FROM LeadView lv WHERE lv.leadId = :leadId " +
           "ORDER BY lv.viewTime DESC")
    List<LeadView> findRecentViewsByLeadId(@Param("leadId") Long leadId);
    
    /**
     * 获取用户的最近浏览记录
     * 
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近浏览记录列表
     */
    @Query("SELECT lv FROM LeadView lv WHERE lv.userId = :userId " +
           "ORDER BY lv.viewTime DESC")
    List<LeadView> findRecentViewsByUserId(@Param("userId") Long userId);
    
    /**
     * 删除指定时间之前的浏览记录（数据清理）
     * 
     * @param beforeTime 指定时间
     * @return 删除的记录数
     */
    long deleteByViewTimeBefore(LocalDateTime beforeTime);
    
    /**
     * 获取热门线索统计（按浏览次数排序）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 线索ID和浏览次数的统计结果
     */
    @Query("SELECT lv.leadId, COUNT(lv) as viewCount FROM LeadView lv " +
           "WHERE lv.viewTime BETWEEN :startTime AND :endTime " +
           "GROUP BY lv.leadId ORDER BY viewCount DESC")
    List<Object[]> findHotLeadsStatistics(
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime);
}