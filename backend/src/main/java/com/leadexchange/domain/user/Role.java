package com.leadexchange.domain.user;

import com.leadexchange.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色实体类
 * 对应数据库roles表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "roles")
@TableName("roles")
@ApiModel(description = "角色实体")
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    @Column(name = "role_name", nullable = false, length = 50)
    @TableField("role_name")
    @NotBlank(message = "角色名称不能为空")
    @Length(max = 50, message = "角色名称长度不能超过50个字符")
    @ApiModelProperty(value = "角色名称", required = true, example = "管理员")
    private String roleName;

    /**
     * 角色编码
     */
    @Column(name = "role_code", nullable = false, unique = true, length = 50)
    @TableField("role_code")
    @NotBlank(message = "角色编码不能为空")
    @Length(max = 50, message = "角色编码长度不能超过50个字符")
    @ApiModelProperty(value = "角色编码", required = true, example = "ADMIN")
    private String roleCode;

    /**
     * 角色描述
     */
    @Column(name = "description", length = 200)
    @TableField("description")
    @Length(max = 200, message = "角色描述长度不能超过200个字符")
    @ApiModelProperty(value = "角色描述", example = "系统管理员，拥有所有权限")
    private String description;

    /**
     * 状态（0：禁用，1：启用）
     */
    @Column(name = "status", nullable = false)
    @TableField("status")
    @ApiModelProperty(value = "状态", example = "1", notes = "0：禁用，1：启用")
    private Integer status = 1;

    /**
     * 排序
     */
    @Column(name = "sort_order", nullable = false)
    @TableField("sort_order")
    @ApiModelProperty(value = "排序", example = "1")
    private Integer sortOrder = 0;

    /**
     * 用户角色关联（一对多）
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @TableField(exist = false)
    private List<UserRole> userRoles;

    /**
     * 角色权限关联（一对多）
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @TableField(exist = false)
    private List<RolePermission> rolePermissions;

    // 构造函数
    public Role() {
    }

    public Role(String roleName, String roleCode) {
        this.roleName = roleName;
        this.roleCode = roleCode;
    }

    public Role(String roleName, String roleCode, String description) {
        this.roleName = roleName;
        this.roleCode = roleCode;
        this.description = description;
    }

    // Getter and Setter methods
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<UserRole> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + getId() +
                ", roleName='" + roleName + '\'' +
                ", roleCode='" + roleCode + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                '}';
    }
}