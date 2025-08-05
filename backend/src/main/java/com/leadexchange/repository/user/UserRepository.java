package com.leadexchange.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface UserRepository extends BaseMapper<Object> {

    // TODO: 定义用户相关的自定义查询方法
    // - 根据用户名查询用户
    // - 根据邮箱查询用户
    // - 根据手机号查询用户
    // - 查询用户权限信息
    // - 用户统计查询
    // - 批量更新用户状态
    
    // 注意：需要在创建User实体类后，将泛型Object替换为User
}