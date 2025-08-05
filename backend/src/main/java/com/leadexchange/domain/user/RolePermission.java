package com.leadexchange.domain.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 * 对应数据库role_permissions表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "role_permissions")
@TableName("role_permissions")
@ApiModel(description = "角色权限关联实体")
public class RolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "关联ID", example = "1")
    private Long id;

    /**
     * 角色ID
     */
    @Column(name = "role_id", nullable = false)
    @TableField("role_id")
    @ApiModelProperty(value = "角色ID", required = true, example = "1")
    private Long roleId;

    /**
     * 权限ID
     */
    @Column(name = "permission_id", nullable = false)
    @TableField("permission_id")
    @ApiModelProperty(value = "权限ID", required = true, example = "1")
    private Long permissionId;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false)
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建时间", example = "2024-01-01 12:00:00")
    private LocalDateTime createTime;

    /**
     * 创建人ID
     */
    @Column(name = "create_by")
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建人ID", example = "1")
    private Long createBy;

    /**
     * 角色实体（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @TableField(exist = false)
    private Role role;

    /**
     * 权限实体（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", insertable = false, updatable = false)
    @TableField(exist = false)
    private Permission permission;

    // 构造函数
    public RolePermission() {
    }

    public RolePermission(Long roleId, Long permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public RolePermission(Long roleId, Long permissionId, Long createBy) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createBy = createBy;
    }

    /**
     * 预创建时间
     */
    @PrePersist
    protected void onCreate() {
        if (createTime == null) {
            createTime = LocalDateTime.now();
        }
    }

    // Getter and Setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Long permissionId) {
        this.permissionId = permissionId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "RolePermission{" +
                "id=" + id +
                ", roleId=" + roleId +
                ", permissionId=" + permissionId +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                '}';
    }
}