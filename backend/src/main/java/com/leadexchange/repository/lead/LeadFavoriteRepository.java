package com.leadexchange.repository.lead;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadexchange.domain.lead.LeadFavorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;

/**
 * 线索收藏数据访问层接口
 * 管理用户收藏线索的相关操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface LeadFavoriteRepository extends BaseMapper<LeadFavorite> {

    /**
     * 检查用户是否已收藏指定线索
     * 
     * @param userId 用户ID
     * @param leadId 线索ID
     * @return 收藏记录，如果未收藏则返回null
     */
    @Select("SELECT * FROM lead_favorites WHERE user_id = #{userId} AND lead_id = #{leadId} LIMIT 1")
    LeadFavorite selectByUserIdAndLeadId(@Param("userId") Long userId, @Param("leadId") Long leadId);

    /**
     * 删除用户对指定线索的收藏
     * 
     * @param userId 用户ID
     * @param leadId 线索ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM lead_favorites WHERE user_id = #{userId} AND lead_id = #{leadId}")
    int deleteByUserIdAndLeadId(@Param("userId") Long userId, @Param("leadId") Long leadId);

    /**
     * 统计线索的收藏次数
     * 
     * @param leadId 线索ID
     * @return 收藏次数
     */
    @Select("SELECT COUNT(*) FROM lead_favorites WHERE lead_id = #{leadId}")
    Long countByLeadId(@Param("leadId") Long leadId);

    /**
     * 统计用户的收藏数量
     * 
     * @param userId 用户ID
     * @return 收藏数量
     */
    @Select("SELECT COUNT(*) FROM lead_favorites WHERE user_id = #{userId}")
    Long countByUserId(@Param("userId") Long userId);

    /**
     * 插入收藏记录
     * 
     * @param leadFavorite 收藏记录
     * @return 插入行数
     */
    int insert(LeadFavorite leadFavorite);

    /**
     * 获取用户收藏的线索ID列表
     * 
     * @param userId 用户ID
     * @return 线索ID列表
     */
    @Select("SELECT lead_id FROM lead_favorites WHERE user_id = #{userId} ORDER BY created_time DESC")
    List<Long> getFavoriteLeadIdsByUserId(@Param("userId") Long userId);

    /**
     * 检查用户是否已收藏指定线索
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @return 是否已收藏
     */
    @Select("SELECT COUNT(*) > 0 FROM lead_favorites WHERE lead_id = #{leadId} AND user_id = #{userId}")
    boolean isFavorited(@Param("leadId") Long leadId, @Param("userId") Long userId);

    /**
     * 删除收藏记录
     * 
     * @param leadId 线索ID
     * @param userId 用户ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM lead_favorites WHERE lead_id = #{leadId} AND user_id = #{userId}")
    int deleteByLeadIdAndUserId(@Param("leadId") Long leadId, @Param("userId") Long userId);

    /**
     * 统计线索的收藏次数
     * 
     * @param leadId 线索ID
     * @return 收藏次数
     */
    @Select("SELECT COUNT(*) FROM lead_favorites WHERE lead_id = #{leadId}")
    int countByLeadId(@Param("leadId") Long leadId);
}