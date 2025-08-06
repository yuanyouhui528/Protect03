package com.leadexchange.domain.lead;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 线索收藏实体类
 * 对应数据库表：lead_favorites
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Schema(description = "线索收藏")
@TableName("lead_favorites")
public class LeadFavorite {

    /**
     * 主键ID
     */
    @Schema(description = "收藏ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    /**
     * 线索ID
     */
    @Schema(description = "线索ID")
    @TableField("lead_id")
    private Long leadId;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 构造函数
    public LeadFavorite() {}

    public LeadFavorite(Long userId, Long leadId) {
        this.userId = userId;
        this.leadId = leadId;
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    // Getter和Setter方法
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

    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "LeadFavorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", leadId=" + leadId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}