package com.leadexchange.repository.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 线索搜索仓库接口
 * 基于Elasticsearch的全文检索功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface LeadSearchRepository extends ElasticsearchRepository<Object, Long> {

    // TODO: 定义线索搜索相关的方法
    // - 根据关键词搜索线索
    // - 根据行业分类搜索
    // - 根据地区搜索
    // - 根据投资规模搜索
    // - 复合条件搜索
    // - 搜索建议(自动补全)
    // - 相似线索推荐
    
    // 注意：需要在创建LeadDocument实体类后，将泛型Object替换为LeadDocument
}