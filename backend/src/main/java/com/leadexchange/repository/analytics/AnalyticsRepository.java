package com.leadexchange.repository.analytics;

import com.leadexchange.domain.analytics.TrendData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 数据分析Repository接口
 * 提供复杂的统计查询功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface AnalyticsRepository extends JpaRepository<Object, Long> {

    /**
     * 获取用户个人线索统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [总数, 有效数, 已交换数]
     */
    @Query(value = "SELECT " +
            "COUNT(*) as total_leads, " +
            "SUM(CASE WHEN audit_status = 'APPROVED' THEN 1 ELSE 0 END) as valid_leads, " +
            "SUM(CASE WHEN status = 'EXCHANGED' THEN 1 ELSE 0 END) as exchanged_leads " +
            "FROM leads WHERE publisher_id = :userId " +
            "AND create_time BETWEEN :startTime AND :endTime", nativeQuery = true)
    Object[] getPersonalLeadStats(@Param("userId") Long userId, 
                                  @Param("startTime") LocalDateTime startTime, 
                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户个人交换统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [获得线索数, 积分收入, 积分支出]
     */
    @Query(value = "SELECT " +
            "COUNT(DISTINCT er.received_lead_id) as received_leads, " +
            "COALESCE(SUM(CASE WHEN er.requester_id = :userId THEN -er.points_cost ELSE er.points_cost END), 0) as points_earned, " +
            "COALESCE(SUM(CASE WHEN er.requester_id = :userId THEN er.points_cost ELSE 0 END), 0) as points_spent " +
            "FROM exchange_records er " +
            "WHERE (er.requester_id = :userId OR er.provider_id = :userId) " +
            "AND er.status = 'COMPLETED' " +
            "AND er.create_time BETWEEN :startTime AND :endTime", nativeQuery = true)
    Object[] getPersonalExchangeStats(@Param("userId") Long userId, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户线索评级分布
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级分布结果
     */
    @Query(value = "SELECT rating, COUNT(*) as count " +
            "FROM leads WHERE publisher_id = :userId " +
            "AND create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY rating", nativeQuery = true)
    List<Object[]> getPersonalRatingDistribution(@Param("userId") Long userId, 
                                                 @Param("startTime") LocalDateTime startTime, 
                                                 @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户线索浏览和收藏统计
     * 
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组 [总浏览数, 总收藏数]
     */
    @Query(value = "SELECT " +
            "COALESCE(SUM(lv.view_count), 0) as total_views, " +
            "COALESCE(COUNT(DISTINCT lf.id), 0) as total_favorites " +
            "FROM leads l " +
            "LEFT JOIN lead_views lv ON l.id = lv.lead_id " +
            "LEFT JOIN lead_favorites lf ON l.id = lf.lead_id " +
            "WHERE l.publisher_id = :userId " +
            "AND l.create_time BETWEEN :startTime AND :endTime", nativeQuery = true)
    Object[] getPersonalViewFavoriteStats(@Param("userId") Long userId, 
                                          @Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 获取系统总体统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果数组
     */
    @Query(value = "SELECT " +
            "(SELECT COUNT(*) FROM users) as total_users, " +
            "(SELECT COUNT(*) FROM users WHERE last_login_time BETWEEN :startTime AND :endTime) as active_users, " +
            "(SELECT COUNT(*) FROM users WHERE create_time BETWEEN :startTime AND :endTime) as new_users, " +
            "(SELECT COUNT(*) FROM leads WHERE create_time BETWEEN :startTime AND :endTime) as total_leads, " +
            "(SELECT COUNT(*) FROM leads WHERE audit_status = 'APPROVED' AND create_time BETWEEN :startTime AND :endTime) as valid_leads, " +
            "(SELECT COUNT(*) FROM exchange_records WHERE create_time BETWEEN :startTime AND :endTime) as total_exchanges, " +
            "(SELECT COUNT(*) FROM exchange_records WHERE status = 'COMPLETED' AND create_time BETWEEN :startTime AND :endTime) as successful_exchanges", 
            nativeQuery = true)
    Object[] getSystemOverallStats(@Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取系统线索评级分布
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级分布结果
     */
    @Query(value = "SELECT rating, COUNT(*) as count " +
            "FROM leads WHERE create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY rating", nativeQuery = true)
    List<Object[]> getSystemRatingDistribution(@Param("startTime") LocalDateTime startTime, 
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 获取行业分布统计
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 行业分布结果
     */
    @Query(value = "SELECT industry, COUNT(*) as count " +
            "FROM leads WHERE (:userId IS NULL OR publisher_id = :userId) " +
            "AND create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY industry ORDER BY count DESC", nativeQuery = true)
    List<Object[]> getIndustryDistribution(@Param("userId") Long userId, 
                                           @Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 获取线索趋势数据（按天）
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据
     */
    @Query(value = "SELECT DATE(create_time) as date_key, COUNT(*) as count " +
            "FROM leads WHERE (:userId IS NULL OR publisher_id = :userId) " +
            "AND create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY DATE(create_time) ORDER BY date_key", nativeQuery = true)
    List<Object[]> getLeadTrendsByDay(@Param("userId") Long userId, 
                                      @Param("startTime") LocalDateTime startTime, 
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 获取交换趋势数据（按天）
     * 
     * @param userId 用户ID（可选）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 趋势数据
     */
    @Query(value = "SELECT DATE(create_time) as date_key, COUNT(*) as count " +
            "FROM exchange_records WHERE (:userId IS NULL OR requester_id = :userId OR provider_id = :userId) " +
            "AND create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY DATE(create_time) ORDER BY date_key", nativeQuery = true)
    List<Object[]> getExchangeTrendsByDay(@Param("userId") Long userId, 
                                          @Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 获取用户活跃度趋势数据（按天）
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活跃度趋势数据
     */
    @Query(value = "SELECT DATE(last_login_time) as date_key, COUNT(DISTINCT id) as count " +
            "FROM users WHERE last_login_time BETWEEN :startTime AND :endTime " +
            "GROUP BY DATE(last_login_time) ORDER BY date_key", nativeQuery = true)
    List<Object[]> getUserActivityTrendsByDay(@Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 获取最热门行业
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 最热门行业信息 [行业名称, 线索数量]
     */
    @Query(value = "SELECT industry, COUNT(*) as count " +
            "FROM leads WHERE create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY industry ORDER BY count DESC LIMIT 1", nativeQuery = true)
    Object[] getTopIndustry(@Param("startTime") LocalDateTime startTime, 
                            @Param("endTime") LocalDateTime endTime);

    /**
     * 获取最活跃用户
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 最活跃用户信息 [用户ID, 线索数量]
     */
    @Query(value = "SELECT publisher_id, COUNT(*) as count " +
            "FROM leads WHERE create_time BETWEEN :startTime AND :endTime " +
            "GROUP BY publisher_id ORDER BY count DESC LIMIT 1", nativeQuery = true)
    Object[] getMostActiveUser(@Param("startTime") LocalDateTime startTime, 
                               @Param("endTime") LocalDateTime endTime);
}