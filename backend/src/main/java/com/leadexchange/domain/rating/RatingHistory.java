package com.leadexchange.domain.rating;

import com.leadexchange.common.entity.BaseEntity;
import com.leadexchange.domain.lead.LeadRating;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 评级历史记录实体类
 * 记录线索评级的变更历史
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Entity
@Table(name = "rating_histories")
@ApiModel(description = "评级历史记录")
public class RatingHistory extends BaseEntity {

    /**
     * 线索ID
     */
    @Column(name = "lead_id", nullable = false)
    @NotNull(message = "线索ID不能为空")
    @ApiModelProperty(value = "线索ID", example = "1")
    private Long leadId;

    /**
     * 评级前等级
     */
    @Column(name = "previous_rating", length = 10)
    @Enumerated(EnumType.STRING)
    @ApiModelProperty(value = "评级前等级", example = "B")
    private LeadRating previousRating;

    /**
     * 评级前分数
     */
    @Column(name = "previous_score")
    @ApiModelProperty(value = "评级前分数", example = "65")
    private Integer previousScore;

    /**
     * 评级后等级
     */
    @Column(name = "current_rating", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "当前评级不能为空")
    @ApiModelProperty(value = "评级后等级", example = "A")
    private LeadRating currentRating;

    /**
     * 评级后分数
     */
    @Column(name = "current_score", nullable = false)
    @NotNull(message = "当前分数不能为空")
    @ApiModelProperty(value = "评级后分数", example = "85")
    private Integer currentScore;

    /**
     * 评级变更原因
     */
    @Column(name = "change_reason", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "变更原因不能为空")
    @ApiModelProperty(value = "评级变更原因", example = "MANUAL_ADJUSTMENT")
    private RatingChangeReason changeReason;

    /**
     * 操作人ID
     */
    @Column(name = "operator_id")
    @ApiModelProperty(value = "操作人ID", example = "1")
    private Long operatorId;

    /**
     * 操作人姓名
     */
    @Column(name = "operator_name", length = 100)
    @ApiModelProperty(value = "操作人姓名", example = "张三")
    private String operatorName;

    /**
     * 变更说明
     */
    @Column(name = "change_description", length = 500)
    @ApiModelProperty(value = "变更说明", example = "根据最新企业信息调整评级")
    private String changeDescription;

    /**
     * 评级时间
     */
    @Column(name = "rating_time", nullable = false)
    @NotNull(message = "评级时间不能为空")
    @ApiModelProperty(value = "评级时间", example = "2024-01-01T10:00:00")
    private LocalDateTime ratingTime;

    /**
     * 评级详情（JSON格式）
     * 存储各维度的详细评分
     */
    @Column(name = "rating_details", columnDefinition = "TEXT")
    @ApiModelProperty(value = "评级详情", example = "{\"completeness\": 40, \"qualification\": 30, \"scale\": 20, \"industry\": 10}")
    private String ratingDetails;

    /**
     * 评级版本
     */
    @Column(name = "rating_version", length = 20)
    @ApiModelProperty(value = "评级版本", example = "v1.0")
    private String ratingVersion;

    // 构造函数
    public RatingHistory() {}

    public RatingHistory(Long leadId, LeadRating previousRating, Integer previousScore,
                        LeadRating currentRating, Integer currentScore, 
                        RatingChangeReason changeReason, Long operatorId, 
                        String operatorName, String changeDescription) {
        this.leadId = leadId;
        this.previousRating = previousRating;
        this.previousScore = previousScore;
        this.currentRating = currentRating;
        this.currentScore = currentScore;
        this.changeReason = changeReason;
        this.operatorId = operatorId;
        this.operatorName = operatorName;
        this.changeDescription = changeDescription;
        this.ratingTime = LocalDateTime.now();
    }

    // Getter和Setter方法
    public Long getLeadId() {
        return leadId;
    }

    public void setLeadId(Long leadId) {
        this.leadId = leadId;
    }

    public LeadRating getPreviousRating() {
        return previousRating;
    }

    public void setPreviousRating(LeadRating previousRating) {
        this.previousRating = previousRating;
    }

    public Integer getPreviousScore() {
        return previousScore;
    }

    public void setPreviousScore(Integer previousScore) {
        this.previousScore = previousScore;
    }

    public LeadRating getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(LeadRating currentRating) {
        this.currentRating = currentRating;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public RatingChangeReason getChangeReason() {
        return changeReason;
    }

    public void setChangeReason(RatingChangeReason changeReason) {
        this.changeReason = changeReason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getChangeDescription() {
        return changeDescription;
    }

    public void setChangeDescription(String changeDescription) {
        this.changeDescription = changeDescription;
    }

    public LocalDateTime getRatingTime() {
        return ratingTime;
    }

    public void setRatingTime(LocalDateTime ratingTime) {
        this.ratingTime = ratingTime;
    }

    public String getRatingDetails() {
        return ratingDetails;
    }

    public void setRatingDetails(String ratingDetails) {
        this.ratingDetails = ratingDetails;
    }

    public String getRatingVersion() {
        return ratingVersion;
    }

    public void setRatingVersion(String ratingVersion) {
        this.ratingVersion = ratingVersion;
    }

    /**
     * 判断是否为评级提升
     * 
     * @return 是否为评级提升
     */
    public boolean isRatingUpgrade() {
        if (previousRating == null) {
            return false;
        }
        return currentRating.getExchangeValue() > previousRating.getExchangeValue();
    }

    /**
     * 判断是否为评级降级
     * 
     * @return 是否为评级降级
     */
    public boolean isRatingDowngrade() {
        if (previousRating == null) {
            return false;
        }
        return currentRating.getExchangeValue() < previousRating.getExchangeValue();
    }

    /**
     * 获取分数变化
     * 
     * @return 分数变化值（正数表示提升，负数表示下降）
     */
    public int getScoreChange() {
        if (previousScore == null) {
            return currentScore;
        }
        return currentScore - previousScore;
    }

    @Override
    public String toString() {
        return "RatingHistory{" +
                "id=" + getId() +
                ", leadId=" + leadId +
                ", previousRating=" + previousRating +
                ", previousScore=" + previousScore +
                ", currentRating=" + currentRating +
                ", currentScore=" + currentScore +
                ", changeReason=" + changeReason +
                ", operatorId=" + operatorId +
                ", operatorName='" + operatorName + '\'' +
                ", changeDescription='" + changeDescription + '\'' +
                ", ratingTime=" + ratingTime +
                ", ratingVersion='" + ratingVersion + '\'' +
                '}';
    }
}