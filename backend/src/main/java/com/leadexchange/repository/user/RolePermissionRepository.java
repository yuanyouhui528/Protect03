package com.leadexchange.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadexchange.domain.user.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Mapper
public interface RolePermissionRepository extends BaseMapper<RolePermission> {

    /**
     * 根据角色ID查询角色权限关联
     * 
     * @param roleId 角色ID
     * @return 角色权限关联列表
     */
    @Select("SELECT * FROM role_permissions WHERE role_id = #{roleId}")
    List<RolePermission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID查询角色权限关联
     * 
     * @param permissionId 权限ID
     * @return 角色权限关联列表
     */
    @Select("SELECT * FROM role_permissions WHERE permission_id = #{permissionId}")
    List<RolePermission> findByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 检查角色是否拥有指定权限
     * 
     * @param roleId 角色ID
     * @param permissionId 权限ID
     * @return 是否拥有权限
     */
    @Select("SELECT COUNT(*) > 0 FROM role_permissions WHERE role_id = #{roleId} AND permission_id = #{permissionId}")
    boolean existsByRoleIdAndPermissionId(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

    /**
     * 删除角色的所有权限
     * 
     * @param roleId 角色ID
     * @return 删除数量
     */
    @Delete("DELETE FROM role_permissions WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Long roleId);

    /**
     * 删除权限的所有角色关联
     * 
     * @param permissionId 权限ID
     * @return 删除数量
     */
    @Delete("DELETE FROM role_permissions WHERE permission_id = #{permissionId}")
    int deleteByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 批量插入角色权限关联
     * 
     * @param roleId 角色ID
     * @param permissionIds 权限ID列表
     * @param createdBy 创建者ID
     * @return 插入数量
     */
    @Insert("<script>" +
            "INSERT INTO role_permissions (role_id, permission_id, created_by, create_time) VALUES " +
            "<foreach collection='permissionIds' item='permissionId' separator=','>" +
            "(#{roleId}, #{permissionId}, #{createdBy}, NOW())" +
            "</foreach>" +
            "</script>")
    int batchInsert(@Param("roleId") Long roleId, 
                   @Param("permissionIds") List<Long> permissionIds, 
                   @Param("createdBy") Long createdBy);

    /**
     * 根据角色ID获取权限ID列表
     * 
     * @param roleId 角色ID
     * @return 权限ID列表
     */
    @Select("SELECT permission_id FROM role_permissions WHERE role_id = #{roleId}")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限ID获取角色ID列表
     * 
     * @param permissionId 权限ID
     * @return 角色ID列表
     */
    @Select("SELECT role_id FROM role_permissions WHERE permission_id = #{permissionId}")
    List<Long> findRoleIdsByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 统计拥有指定权限的角色数量
     * 
     * @param permissionId 权限ID
     * @return 角色数量
     */
    @Select("SELECT COUNT(*) FROM role_permissions WHERE permission_id = #{permissionId}")
    int countRolesByPermissionId(@Param("permissionId") Long permissionId);

    /**
     * 根据角色ID列表获取所有权限ID
     * 
     * @param roleIds 角色ID列表
     * @return 权限ID列表
     */
    @Select("<script>" +
            "SELECT DISTINCT permission_id FROM role_permissions WHERE role_id IN " +
            "<foreach collection='roleIds' item='roleId' open='(' separator=',' close=')'>" +
            "#{roleId}" +
            "</foreach>" +
            "</script>")
    List<Long> findPermissionIdsByRoleIds(@Param("roleIds") List<Long> roleIds);
}