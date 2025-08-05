package com.leadexchange.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadexchange.domain.user.UserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户角色关联数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface UserRoleRepository extends BaseMapper<UserRole> {

    /**
     * 根据用户ID查询用户角色关联
     * 
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    @Select("SELECT * FROM user_roles WHERE user_id = #{userId}")
    List<UserRole> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询用户角色关联
     * 
     * @param roleId 角色ID
     * @return 用户角色关联列表
     */
    @Select("SELECT * FROM user_roles WHERE role_id = #{roleId}")
    List<UserRole> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * 
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 是否拥有角色
     */
    @Select("SELECT COUNT(*) > 0 FROM user_roles WHERE user_id = #{userId} AND role_id = #{roleId}")
    boolean existsByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * 删除用户的所有角色
     * 
     * @param userId 用户ID
     * @return 删除数量
     */
    @Delete("DELETE FROM user_roles WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 删除角色的所有用户关联
     * 
     * @param roleId 角色ID
     * @return 删除数量
     */
    @Delete("DELETE FROM user_roles WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入用户角色关联
     * 
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     * @param createdBy 创建者ID
     * @return 插入数量
     */
    @Insert("<script>" +
            "INSERT INTO user_roles (user_id, role_id, created_by, create_time) VALUES " +
            "<foreach collection='roleIds' item='roleId' separator=','>" +
            "(#{userId}, #{roleId}, #{createdBy}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("userId") Long userId, 
                   @Param("roleIds") List<Long> roleIds, 
                   @Param("createdBy") Long createdBy);

    /**
     * 根据用户ID获取角色ID列表
     * 
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM user_roles WHERE user_id = #{userId}")
    List<Long> findRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID获取用户ID列表
     * 
     * @param roleId 角色ID
     * @return 用户ID列表
     */
    @Select("SELECT user_id FROM user_roles WHERE role_id = #{roleId}")
    List<Long> findUserIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 统计拥有指定角色的用户数量
     * 
     * @param roleId 角色ID
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM user_roles WHERE role_id = #{roleId}")
    int countUsersByRoleId(@Param("roleId") Long roleId);
}