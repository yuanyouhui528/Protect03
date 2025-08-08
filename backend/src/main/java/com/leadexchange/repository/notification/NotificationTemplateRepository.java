package com.leadexchange.repository.notification;

import com.leadexchange.domain.notification.NotificationTemplate;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.SendChannel;
import com.leadexchange.domain.notification.TemplateStatus;
import com.leadexchange.domain.notification.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知模板数据访问接口
 * 提供通知模板的数据库操作方法
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long>, JpaSpecificationExecutor<NotificationTemplate> {
    
    /**
     * 根据模板编码查询模板
     * 
     * @param templateCode 模板编码
     * @return 通知模板
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
    
    /**
     * 根据事件类型和模板类型查询模板
     * 
     * @param eventType 事件类型
     * @param templateType 模板类型
     * @return 通知模板
     */
    Optional<NotificationTemplate> findByEventTypeAndTemplateType(NotificationType eventType, SendChannel templateType);
    
    /**
     * 根据事件类型查询所有可用模板
     * 
     * @param eventType 事件类型
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.eventType = :eventType AND t.isEnabled = true AND t.status = 'ACTIVE' ORDER BY t.priority DESC")
    List<NotificationTemplate> findAvailableTemplatesByEventType(@Param("eventType") NotificationType eventType);
    
    /**
     * 根据模板类型查询所有可用模板
     * 
     * @param templateType 模板类型
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateType = :templateType AND t.isEnabled = true AND t.status = 'ACTIVE' ORDER BY t.priority DESC")
    List<NotificationTemplate> findAvailableTemplatesByType(@Param("templateType") TemplateType templateType);
    
    /**
     * 查询所有可用模板
     * 
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.isEnabled = true AND t.status = 'ACTIVE' ORDER BY t.eventType, t.templateType, t.priority DESC")
    List<NotificationTemplate> findAllAvailableTemplates();
    
    /**
     * 根据状态查询模板
     * 
     * @param status 状态
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByStatus(TemplateStatus status);
    
    /**
     * 根据启用状态查询模板
     * 
     * @param isEnabled 是否启用
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByIsEnabled(Boolean isEnabled);
    
    /**
     * 根据优先级范围查询模板
     * 
     * @param minPriority 最小优先级
     * @param maxPriority 最大优先级
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.priority BETWEEN :minPriority AND :maxPriority ORDER BY t.priority DESC")
    List<NotificationTemplate> findByPriorityBetween(@Param("minPriority") Integer minPriority, @Param("maxPriority") Integer maxPriority);
    
    /**
     * 统计指定事件类型的模板数量
     * 
     * @param eventType 事件类型
     * @return 模板数量
     */
    long countByEventType(NotificationType eventType);
    
    /**
     * 统计指定模板类型的模板数量
     * 
     * @param templateType 模板类型
     * @return 模板数量
     */
    long countByTemplateType(TemplateType templateType);
    
    /**
     * 统计可用模板数量
     * 
     * @return 可用模板数量
     */
    @Query("SELECT COUNT(t) FROM NotificationTemplate t WHERE t.isEnabled = true AND t.status = 'ACTIVE'")
    long countAvailableTemplates();
    
    /**
     * 检查模板编码是否存在
     * 
     * @param templateCode 模板编码
     * @return 是否存在
     */
    boolean existsByTemplateCode(String templateCode);
    
    /**
     * 检查指定事件类型和模板类型的组合是否存在
     * 
     * @param eventType 事件类型
     * @param templateType 模板类型
     * @return 是否存在
     */
    boolean existsByEventTypeAndTemplateType(NotificationType eventType, TemplateType templateType);
    
    /**
     * 根据模板名称模糊查询
     * 
     * @param templateName 模板名称关键词
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.templateName LIKE %:templateName% ORDER BY t.createTime DESC")
    List<NotificationTemplate> findByTemplateNameContaining(@Param("templateName") String templateName);
    
    /**
     * 查询高优先级模板（优先级大于等于指定值）
     * 
     * @param priority 优先级阈值
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.priority >= :priority AND t.isEnabled = true AND t.status = 'ACTIVE' ORDER BY t.priority DESC")
    List<NotificationTemplate> findHighPriorityTemplates(@Param("priority") Integer priority);
    
    /**
     * 查询需要重试的模板（最大重试次数大于0）
     * 
     * @return 通知模板列表
     */
    @Query("SELECT t FROM NotificationTemplate t WHERE t.maxRetryCount > 0 ORDER BY t.maxRetryCount DESC")
    List<NotificationTemplate> findRetryableTemplates();
    
    /**
     * 根据过期时间查询模板
     * 
     * @param expireHours 过期小时数
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByExpireHours(Integer expireHours);
    
    /**
     * 统计指定通知类型的模板数量
     * 
     * @param notificationType 通知类型
     * @return 模板数量
     */
    long countByNotificationType(NotificationType notificationType);
    
    /**
     * 统计指定启用状态的模板数量
     * 
     * @param isEnabled 是否启用
     * @return 模板数量
     */
    long countByIsEnabled(Boolean isEnabled);
    
    /**
     * 统计指定状态的模板数量
     * 
     * @param status 模板状态
     * @return 模板数量
     */
    long countByStatus(TemplateStatus status);
    
    /**
     * 根据启用状态查询模板，按更新时间倒序排列
     * 
     * @param isEnabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByIsEnabledOrderByUpdateTimeDesc(Boolean isEnabled, Pageable pageable);
    
    /**
     * 根据启用状态查询模板，按优先级倒序排列
     * 
     * @param isEnabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByIsEnabledOrderByPriorityDesc(Boolean isEnabled, Pageable pageable);
    
    /**
     * 根据模板类型和通知类型查询模板
     * 
     * @param templateType 模板类型
     * @param notificationType 通知类型
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByTemplateTypeAndNotificationType(TemplateType templateType, NotificationType notificationType);
    
    /**
     * 根据模板类型和通知类型查询模板（分页）
     * 
     * @param templateType 模板类型
     * @param notificationType 通知类型
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByTemplateTypeAndNotificationType(TemplateType templateType, NotificationType notificationType, Pageable pageable);
    
    /**
     * 根据通知类型和启用状态查询模板
     * 
     * @param notificationType 通知类型
     * @param isEnabled 是否启用
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByNotificationTypeAndIsEnabled(NotificationType notificationType, Boolean isEnabled);
    
    /**
     * 根据通知类型和启用状态查询模板（分页）
     * 
     * @param notificationType 通知类型
     * @param isEnabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByNotificationTypeAndIsEnabled(NotificationType notificationType, Boolean isEnabled, Pageable pageable);
    
    /**
     * 根据状态查询模板（分页）
     * 
     * @param status 状态
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByStatus(TemplateStatus status, Pageable pageable);
    
    /**
     * 根据模板类型、通知类型、状态和启用状态查询模板
     * 
     * @param templateType 模板类型
     * @param notificationType 通知类型
     * @param status 状态
     * @param isEnabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> findByTemplateTypeAndNotificationTypeAndStatusAndIsEnabled(
            TemplateType templateType, NotificationType notificationType, TemplateStatus status, Boolean isEnabled, Pageable pageable);
    
    /**
     * 根据事件类型和启用状态查询模板
     * 
     * @param eventType 事件类型
     * @param isEnabled 是否启用
     * @return 通知模板列表
     */
    List<NotificationTemplate> findByEventTypeAndIsEnabled(NotificationType eventType, Boolean isEnabled);
}