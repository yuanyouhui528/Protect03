package com.leadexchange.domain.user;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 对应数据库user_roles表
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "user_roles")
@TableName("user_roles")
@ApiModel(description = "用户角色关联实体")
public class UserRole implements Serializable {

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
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    @TableField("user_id")
    @ApiModelProperty(value = "用户ID", required = true, example = "1")
    private Long userId;

    /**
     * 角色ID
     */
    @Column(name = "role_id", nullable = false)
    @TableField("role_id")
    @ApiModelProperty(value = "角色ID", required = true, example = "1")
    private Long roleId;

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
     * 用户实体（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @TableField(exist = false)
    private User user;

    /**
     * 角色实体（多对一）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    @TableField(exist = false)
    private Role role;

    // 构造函数
    public UserRole() {
    }

    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    public UserRole(Long userId, Long roleId, Long createBy) {
        this.userId = userId;
        this.roleId = roleId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "id=" + id +
                ", userId=" + userId +
                ", roleId=" + roleId +
                ", createTime=" + createTime +
                ", createBy=" + createBy +
                '}';
    }
}