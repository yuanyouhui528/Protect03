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
 * 权限实体类
 * 对应数据库permissions表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "permissions")
@TableName("permissions")
@ApiModel(description = "权限实体")
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    @Column(name = "permission_name", nullable = false, length = 100)
    @TableField("permission_name")
    @NotBlank(message = "权限名称不能为空")
    @Length(max = 100, message = "权限名称长度不能超过100个字符")
    @ApiModelProperty(value = "权限名称", required = true, example = "用户管理")
    private String permissionName;

    /**
     * 权限编码
     */
    @Column(name = "permission_code", nullable = false, unique = true, length = 100)
    @TableField("permission_code")
    @NotBlank(message = "权限编码不能为空")
    @Length(max = 100, message = "权限编码长度不能超过100个字符")
    @ApiModelProperty(value = "权限编码", required = true, example = "user:manage")
    private String permissionCode;

    /**
     * 权限类型（menu：菜单，button：按钮，api：接口）
     */
    @Column(name = "permission_type", nullable = false, length = 20)
    @TableField("permission_type")
    @NotBlank(message = "权限类型不能为空")
    @ApiModelProperty(value = "权限类型", required = true, example = "menu", notes = "menu：菜单，button：按钮，api：接口")
    private String permissionType;

    /**
     * 父权限ID
     */
    @Column(name = "parent_id")
    @TableField("parent_id")
    @ApiModelProperty(value = "父权限ID", example = "1")
    private Long parentId = 0L;

    /**
     * 路径
     */
    @Column(name = "path", length = 200)
    @TableField("path")
    @Length(max = 200, message = "路径长度不能超过200个字符")
    @ApiModelProperty(value = "路径", example = "/user/list")
    private String path;

    /**
     * 组件
     */
    @Column(name = "component", length = 200)
    @TableField("component")
    @Length(max = 200, message = "组件长度不能超过200个字符")
    @ApiModelProperty(value = "组件", example = "UserList")
    private String component;

    /**
     * 图标
     */
    @Column(name = "icon", length = 100)
    @TableField("icon")
    @Length(max = 100, message = "图标长度不能超过100个字符")
    @ApiModelProperty(value = "图标", example = "user")
    private String icon;

    /**
     * HTTP方法
     */
    @Column(name = "method", length = 10)
    @TableField("method")
    @Length(max = 10, message = "HTTP方法长度不能超过10个字符")
    @ApiModelProperty(value = "HTTP方法", example = "GET")
    private String method;

    /**
     * API地址
     */
    @Column(name = "url", length = 200)
    @TableField("url")
    @Length(max = 200, message = "API地址长度不能超过200个字符")
    @ApiModelProperty(value = "API地址", example = "/api/users")
    private String url;

    /**
     * 权限描述
     */
    @Column(name = "description", length = 200)
    @TableField("description")
    @Length(max = 200, message = "权限描述长度不能超过200个字符")
    @ApiModelProperty(value = "权限描述", example = "用户管理相关权限")
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
     * 角色权限关联（一对多）
     */
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @TableField(exist = false)
    private List<RolePermission> rolePermissions;

    /**
     * 子权限列表（用于构建权限树）
     */
    @Transient
    @TableField(exist = false)
    private List<Permission> children;

    // 构造函数
    public Permission() {
    }

    public Permission(String permissionName, String permissionCode, String permissionType) {
        this.permissionName = permissionName;
        this.permissionCode = permissionCode;
        this.permissionType = permissionType;
    }

    public Permission(String permissionName, String permissionCode, String permissionType, Long parentId) {
        this.permissionName = permissionName;
        this.permissionCode = permissionCode;
        this.permissionType = permissionType;
        this.parentId = parentId;
    }

    // Getter and Setter methods
    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionType() {
        return permissionType;
    }

    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public List<RolePermission> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<RolePermission> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public List<Permission> getChildren() {
        return children;
    }

    public void setChildren(List<Permission> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + getId() +
                ", permissionName='" + permissionName + '\'' +
                ", permissionCode='" + permissionCode + '\'' +
                ", permissionType='" + permissionType + '\'' +
                ", parentId=" + parentId +
                ", path='" + path + '\'' +
                ", component='" + component + '\'' +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                '}';
    }
}