package com.leadexchange.repository.lead.mapper;

import com.leadexchange.domain.lead.LeadFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索收藏Mapper接口
 * 继承MyBatis Plus的BaseMapper，提供基础的CRUD操作
 * 
 * @author AI Assistant
 * @since 2024-01-01
 */
@Mapper
public interface LeadFavoriteMapper extends BaseMapper<LeadFavorite> {
    // 继承BaseMapper已提供基础的CRUD方法
    // 如需要复杂查询，可在此添加自定义方法
}