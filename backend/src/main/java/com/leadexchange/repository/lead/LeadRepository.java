package com.leadexchange.repository.lead;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线索数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface LeadRepository extends BaseMapper<Object> {

    // TODO: 定义线索相关的自定义查询方法
    // - 根据用户ID查询线索列表
    // - 根据状态查询线索
    // - 根据评级查询线索
    // - 线索统计查询
    // - 重复线索检测查询
    // - 批量更新线索状态
    // - 线索价值统计
    
    // 注意：需要在创建Lead实体类后，将泛型Object替换为Lead
}