package com.leadexchange.repository.rating;

import com.leadexchange.domain.rating.RatingHistory;
import com.leadexchange.domain.rating.RatingChangeReason;
import com.leadexchange.domain.lead.LeadRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 评级历史Repository接口
 * 提供评级历史数据的持久化操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface RatingHistoryRepository extends JpaRepository<RatingHistory, Long> {

    /**
     * 根据线索ID查询评级历史，按时间倒序
     * 
     * @param leadId 线索ID
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> findByLeadIdOrderByRatingTimeDesc(Long leadId, Pageable pageable);

    /**
     * 根据线索ID查询最新的评级历史
     * 
     * @param leadId 线索ID
     * @return 最新的评级历史
     */
    Optional<RatingHistory> findFirstByLeadIdOrderByRatingTimeDesc(Long leadId);

    /**
     * 统计线索的评级变更次数
     * 
     * @param leadId 线索ID
     * @return 变更次数
     */
    Long countByLeadId(Long leadId);

    /**
     * 根据变更原因查询评级历史，按时间倒序
     * 
     * @param changeReason 变更原因
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> findByChangeReasonOrderByRatingTimeDesc(RatingChangeReason changeReason, Pageable pageable);

    /**
     * 根据操作员ID查询评级历史，按时间倒序
     * 
     * @param operatorId 操作员ID
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> findByOperatorIdOrderByRatingTimeDesc(Long operatorId, Pageable pageable);

    /**
     * 根据时间范围查询评级历史，按时间倒序
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> findByRatingTimeBetweenOrderByRatingTimeDesc(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据评级变更查询历史，按时间倒序
     * 
     * @param previousRating 原评级
     * @param currentRating 新评级
     * @param pageable 分页参数
     * @return 评级历史分页数据
     */
    Page<RatingHistory> findByPreviousRatingAndCurrentRatingOrderByRatingTimeDesc(LeadRating previousRating, LeadRating currentRating, Pageable pageable);

    /**
     * 查询评级升级历史
     * 
     * @param pageable 分页参数
     * @return 评级升级历史分页数据
     */
    @Query("SELECT h FROM RatingHistory h WHERE h.currentRating > h.previousRating ORDER BY h.ratingTime DESC")
    Page<RatingHistory> findRatingUpgradeHistory(Pageable pageable);

    /**
     * 查询评级降级历史
     * 
     * @param pageable 分页参数
     * @return 评级降级历史分页数据
     */
    @Query("SELECT h FROM RatingHistory h WHERE h.currentRating < h.previousRating ORDER BY h.ratingTime DESC")
    Page<RatingHistory> findRatingDowngradeHistory(Pageable pageable);

    /**
     * 根据时间范围查询评级历史
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级历史列表
     */
    List<RatingHistory> findByRatingTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除指定时间之前的评级历史
     * 
     * @param cutoffTime 截止时间
     * @return 删除的记录数
     */
    Long deleteByRatingTimeBefore(LocalDateTime cutoffTime);

    /**
     * 根据线索ID和时间范围查询评级历史
     * 
     * @param leadId 线索ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级历史列表
     */
    List<RatingHistory> findByLeadIdAndRatingTimeBetweenOrderByRatingTimeDesc(Long leadId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据时间范围查询评级历史，按时间倒序
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级历史列表
     */
    List<RatingHistory> findByRatingTimeBetweenOrderByRatingTimeDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询线索的最近10条评级历史
     * 
     * @param leadId 线索ID
     * @return 最近10条评级历史
     */
    List<RatingHistory> findTop10ByLeadIdOrderByRatingTimeDesc(Long leadId);

    /**
     * 根据评级变更和时间范围统计记录数
     * 
     * @param previousRating 原评级
     * @param currentRating 新评级
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录数
     */
    Long countByPreviousRatingAndCurrentRatingAndCreateTimeBetween(LeadRating previousRating, LeadRating currentRating, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据创建时间范围统计记录数
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录数
     */
    Long countByCreateTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定创建时间之前的记录数
     * 
     * @param cutoffTime 截止时间
     * @return 记录数
     */
    Long countByCreateTimeBefore(LocalDateTime cutoffTime);

    /**
     * 删除指定创建时间之前的记录
     * 
     * @param cutoffTime 截止时间
     * @return 删除的记录数
     */
    Long deleteByCreateTimeBefore(LocalDateTime cutoffTime);

    /**
     * 根据创建时间范围查询记录，按创建时间倒序
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评级历史列表
     */
    List<RatingHistory> findByCreateTimeBetweenOrderByCreateTimeDesc(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 根据线索ID查询记录，按创建时间倒序
     * 
     * @param leadId 线索ID
     * @return 评级历史列表
     */
    List<RatingHistory> findByLeadIdOrderByCreateTimeDesc(Long leadId);

    /**
     * 根据操作人ID查询评级历史
     */
    List<RatingHistory> findByOperatorIdOrderByRatingTimeDesc(Long operatorId);

    /**
     * 根据变更原因和时间范围统计数量
     */
    Long countByChangeReasonAndCreateTimeBetween(RatingChangeReason changeReason, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询操作员统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 操作员统计信息
     */
    @Query("SELECT new map(h.operatorId as operatorId, h.operatorName as operatorName, " +
           "COUNT(h) as totalOperations, " +
           "SUM(CASE WHEN h.currentRating > h.previousRating THEN 1 ELSE 0 END) as upgradeOperations, " +
           "SUM(CASE WHEN h.currentRating < h.previousRating THEN 1 ELSE 0 END) as downgradeOperations, " +
           "AVG(h.currentScore - h.previousScore) as averageScoreChange, " +
           "MAX(h.ratingTime) as lastOperationTime) " +
           "FROM RatingHistory h " +
           "WHERE h.ratingTime BETWEEN :startTime AND :endTime " +
           "GROUP BY h.operatorId, h.operatorName " +
           "ORDER BY COUNT(h) DESC")
    List<Map<String, Object>> findOperatorStatistics(@Param("startTime") LocalDateTime startTime,
                                                     @Param("endTime") LocalDateTime endTime);
}