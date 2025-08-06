package com.leadexchange.repository.user;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leadexchange.domain.user.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色数据访问层接口
 * 继承MyBatis Plus的BaseMapper，提供基础CRUD操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
// @Mapper
public interface RoleRepository extends BaseMapper<Role> {

    /**
     * 根据用户ID查询用户角色列表
     * 
     * @param userId 用户ID
     * @return 角色列表
     */
    @Select("SELECT r.* FROM roles r " +
            "INNER JOIN user_roles ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1 AND r.deleted = 0")
    List<Role> findByUserId(@Param("userId") Long userId);

    /**
     * 根据角色代码查询角色
     * 
     * @param roleCode 角色代码
     * @return 角色信息
     */
    @Select("SELECT * FROM roles WHERE role_code = #{roleCode} AND status = 1 AND deleted = 0")
    Role findByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查询所有启用的角色
     * 
     * @return 角色列表
     */
    @Select("SELECT * FROM roles WHERE status = 1 AND deleted = 0 ORDER BY sort_order ASC")
    List<Role> findAllEnabled();

    /**
     * 根据角色ID列表查询角色
     * 
     * @param roleIds 角色ID列表
     * @return 角色列表
     */
    @Select("<script>" +
            "SELECT * FROM roles WHERE id IN " +
            "<foreach collection='roleIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "AND status = 1 AND deleted = 0" +
            "</script>")
    List<Role> findByIds(@Param("roleIds") List<Long> roleIds);

    /**
     * 检查角色代码是否存在
     * 
     * @param roleCode 角色代码
     * @param excludeId 排除的角色ID（用于更新时检查）
     * @return 存在数量
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM roles WHERE role_code = #{roleCode} AND deleted = 0" +
            "<if test='excludeId != null'> AND id != #{excludeId}</if>" +
            "</script>")
    int existsByRoleCode(@Param("roleCode") String roleCode, @Param("excludeId") Long excludeId);
}