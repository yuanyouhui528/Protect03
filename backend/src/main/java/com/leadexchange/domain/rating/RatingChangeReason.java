package com.leadexchange.domain.rating;

/**
 * 评级变更原因枚举
 * 定义线索评级变更的各种原因
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public enum RatingChangeReason {
    
    /**
     * 系统自动评级
     * 系统根据算法自动计算的评级
     */
    SYSTEM_AUTO("系统自动评级", "系统根据评级算法自动计算的评级结果"),
    
    /**
     * 手动调整
     * 管理员或审核员手动调整的评级
     */
    MANUAL_ADJUSTMENT("手动调整", "管理员或审核员根据实际情况手动调整评级"),
    
    /**
     * 信息更新
     * 线索信息更新后重新评级
     */
    INFO_UPDATE("信息更新", "线索信息更新后系统重新计算评级"),
    
    /**
     * 规则变更
     * 评级规则变更后重新评级
     */
    RULE_CHANGE("规则变更", "评级规则配置变更后重新计算评级"),
    
    /**
     * 审核调整
     * 审核过程中调整评级
     */
    AUDIT_ADJUSTMENT("审核调整", "在线索审核过程中调整评级"),
    
    /**
     * 质量反馈
     * 根据用户反馈调整评级
     */
    QUALITY_FEEDBACK("质量反馈", "根据用户对线索质量的反馈调整评级"),
    
    /**
     * 数据修正
     * 数据错误修正后重新评级
     */
    DATA_CORRECTION("数据修正", "发现数据错误并修正后重新计算评级"),
    
    /**
     * 批量重评
     * 批量重新评级操作
     */
    BATCH_RERATING("批量重评", "批量重新评级操作"),
    
    /**
     * 系统升级
     * 系统升级后重新评级
     */
    SYSTEM_UPGRADE("系统升级", "系统升级后使用新算法重新评级"),
    
    /**
     * 投诉处理
     * 处理投诉后调整评级
     */
    COMPLAINT_HANDLING("投诉处理", "处理用户投诉后调整评级"),
    
    /**
     * 定期复评
     * 定期复评机制触发的评级
     */
    PERIODIC_REVIEW("定期复评", "定期复评机制触发的重新评级"),
    
    /**
     * 评级回滚
     * 回滚到之前的评级状态
     */
    ROLLBACK("评级回滚", "回滚到之前的评级状态");
    
    private final String displayName;
    private final String description;
    
    RatingChangeReason(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 根据显示名称获取枚举值
     * 
     * @param displayName 显示名称
     * @return 对应的枚举值
     */
    public static RatingChangeReason fromDisplayName(String displayName) {
        for (RatingChangeReason reason : values()) {
            if (reason.displayName.equals(displayName)) {
                return reason;
            }
        }
        throw new IllegalArgumentException("未知的评级变更原因: " + displayName);
    }
    
    /**
     * 判断是否为自动触发的变更
     * 
     * @return 是否为自动触发
     */
    public boolean isAutomatic() {
        return this == SYSTEM_AUTO || this == INFO_UPDATE || 
               this == RULE_CHANGE || this == BATCH_RERATING ||
               this == SYSTEM_UPGRADE || this == PERIODIC_REVIEW;
    }
    
    /**
     * 判断是否为人工干预的变更
     * 
     * @return 是否为人工干预
     */
    public boolean isManual() {
        return this == MANUAL_ADJUSTMENT || this == AUDIT_ADJUSTMENT ||
               this == QUALITY_FEEDBACK || this == DATA_CORRECTION ||
               this == COMPLAINT_HANDLING;
    }
    
    /**
     * 判断是否需要记录操作人信息
     * 
     * @return 是否需要记录操作人
     */
    public boolean requiresOperator() {
        return isManual();
    }
    
    /**
     * 获取变更类型分类
     * 
     * @return 变更类型分类
     */
    public String getCategory() {
        if (isAutomatic()) {
            return "自动变更";
        } else if (isManual()) {
            return "人工变更";
        } else {
            return "其他变更";
        }
    }
}