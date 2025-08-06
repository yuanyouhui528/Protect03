package com.leadexchange.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadexchange.domain.user.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
// @Mapper
public interface PermissionRepository extends BaseMapper<Permission> {

    /**
     * 根据用户ID查询用户权限列表
     * 
     * @param userId 用户ID
     * @return 权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.status = 1 AND p.deleted = 0")
    List<Permission> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询权限列表
     * 
     * @param roleId 角色ID
     * @return 权限列表
     */
    @Select("SELECT p.* FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.status = 1 AND p.deleted = 0")
    List<Permission> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据权限代码查询权限
     * 
     * @param permissionCode 权限代码
     * @return 权限信息
     */
    @Select("SELECT * FROM permissions WHERE permission_code = #{permissionCode} AND status = 1 AND deleted = 0")
    Permission findByPermissionCode(@Param("permissionCode") String permissionCode);

    /**
     * 查询所有启用的权限
     * 
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Permission> findAllEnabled();

    /**
     * 根据权限类型查询权限
     * 
     * @param permissionType 权限类型（MENU、BUTTON、API）
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE permission_type = #{permissionType} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Permission> findByType(@Param("permissionType") String permissionType);

    /**
     * 根据父权限ID查询子权限
     * 
     * @param parentId 父权限ID
     * @return 权限列表
     */
    @Select("SELECT * FROM permissions WHERE parent_id = #{parentId} AND status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Permission> findByParentId(@Param("parentId") Long parentId);

    /**
     * 检查权限代码是否存在
     * 
     * @param permissionCode 权限代码
     * @param excludeId 排除的权限ID（用于更新时检查）
     * @return 存在数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM permissions WHERE permission_code = #{permissionCode} AND deleted = 0" +
            "<if test='excludeId != null'> AND id != #{excludeId}</if>" +
            "</script>")
    int existsByPermissionCode(@Param("permissionCode") String permissionCode, @Param("excludeId") Long excludeId);

    /**
     * 检查用户是否拥有指定权限
     * 
     * @param userId 用户ID
     * @param permissionCode 权限代码
     * @return 是否拥有权限
     */
    @Select("SELECT COUNT(*) > 0 FROM permissions p " +
            "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
            "INNER JOIN user_roles ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.permission_code = #{permissionCode} " +
            "AND p.status = 1 AND p.deleted = 0")
    boolean hasPermission(@Param("userId") Long userId, @Param("permissionCode") String permissionCode);
}