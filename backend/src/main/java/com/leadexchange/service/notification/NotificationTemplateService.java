package com.leadexchange.service.notification;

import com.leadexchange.domain.notification.NotificationTemplate;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.TemplateType;
import com.leadexchange.domain.notification.TemplateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 通知模板服务接口
 * 提供通知模板的管理和渲染功能
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
public interface NotificationTemplateService {
    
    /**
     * 创建通知模板
     * 
     * @param templateName 模板名称
     * @param templateCode 模板编码
     * @param templateType 模板类型
     * @param notificationType 通知类型
     * @param title 模板标题
     * @param content 模板内容
     * @param variables 模板变量
     * @param priority 优先级
     * @param remark 备注
     * @return 创建的模板
     */
    NotificationTemplate createTemplate(String templateName, String templateCode, 
                                       TemplateType templateType, NotificationType notificationType, 
                                       String title, String content, 
                                       String variables, Integer priority, String remark);
    
    /**
     * 更新通知模板
     * 
     * @param templateId 模板ID
     * @param templateName 模板名称
     * @param title 模板标题
     * @param content 模板内容
     * @param variables 模板变量
     * @param priority 优先级
     * @param remark 备注
     * @return 更新的模板
     */
    NotificationTemplate updateTemplate(Long templateId, String templateName, String title, 
                                       String content, String variables, Integer priority, 
                                       String remark);
    
    /**
     * 删除通知模板
     * 
     * @param templateId 模板ID
     * @return 删除结果
     */
    boolean deleteTemplate(Long templateId);
    
    /**
     * 启用通知模板
     * 
     * @param templateId 模板ID
     * @return 启用结果
     */
    boolean enableTemplate(Long templateId);
    
    /**
     * 禁用通知模板
     * 
     * @param templateId 模板ID
     * @return 禁用结果
     */
    boolean disableTemplate(Long templateId);
    
    /**
     * 根据ID查询模板
     * 
     * @param templateId 模板ID
     * @return 模板信息
     */
    NotificationTemplate getTemplateById(Long templateId);
    
    /**
     * 根据编码查询模板
     * 
     * @param templateCode 模板编码
     * @return 模板信息
     */
    NotificationTemplate getTemplateByCode(String templateCode);
    
    /**
     * 根据事件类型查询可用模板
     * 
     * @param eventType 事件类型
     * @return 模板列表
     */
    List<NotificationTemplate> getTemplatesByEventType(String eventType);
    
    /**
     * 根据通知类型查询可用模板
     * 
     * @param notificationType 通知类型
     * @return 模板列表
     */
    List<NotificationTemplate> getTemplatesByNotificationType(NotificationType notificationType);
    
    /**
     * 查询所有可用模板
     * 
     * @return 模板列表
     */
    List<NotificationTemplate> getAllAvailableTemplates();
    
    /**
     * 查询模板列表（分页）
     * 
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> getTemplates(Pageable pageable);
    
    /**
     * 根据条件查询模板列表
     * 
     * @param templateName 模板名称（模糊查询）
     * @param notificationType 通知类型
     * @param eventType 事件类型
     * @param enabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> searchTemplates(String templateName, NotificationType notificationType, 
                                              String eventType, Boolean enabled, Pageable pageable);
    
    /**
     * 根据条件查询模板
     * 
     * @param templateType 模板类型
     * @param notificationType 通知类型
     * @param status 状态
     * @param isEnabled 是否启用
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> getTemplatesByCondition(TemplateType templateType, NotificationType notificationType, 
                                                      TemplateStatus status, Boolean isEnabled, Pageable pageable);
    
    /**
     * 查询所有模板（分页）
     * 
     * @param pageable 分页参数
     * @return 模板分页
     */
    Page<NotificationTemplate> getAllTemplates(Pageable pageable);
    
    /**
     * 渲染模板标题
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @return 渲染后的标题
     */
    String renderTitle(String templateCode, Map<String, Object> data);
    
    /**
     * 渲染模板内容
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @return 渲染后的内容
     */
    String renderContent(String templateCode, Map<String, Object> data);
    
    /**
     * 渲染模板（标题和内容）
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @return 渲染结果（包含标题和内容）
     */
    Map<String, String> renderTemplate(String templateCode, Map<String, Object> data);
    
    /**
     * 验证模板语法
     * 
     * @param title 模板标题
     * @param content 模板内容
     * @return 验证结果
     */
    boolean validateTemplateSyntax(String title, String content);
    
    /**
     * 验证模板变量
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @return 验证结果
     */
    boolean validateTemplateVariables(String templateCode, Map<String, Object> data);
    
    /**
     * 获取模板所需的变量列表
     * 
     * @param templateCode 模板编码
     * @return 变量列表
     */
    List<String> getTemplateVariables(String templateCode);
    
    /**
     * 检查模板编码是否存在
     * 
     * @param templateCode 模板编码
     * @return 是否存在
     */
    boolean existsByCode(String templateCode);
    
    /**
     * 检查模板是否可用
     * 
     * @param templateCode 模板编码
     * @return 是否可用
     */
    boolean isTemplateAvailable(String templateCode);
    
    /**
     * 复制模板
     * 
     * @param sourceTemplateId 源模板ID
     * @param newTemplateName 新模板名称
     * @param newTemplateCode 新模板编码
     * @return 复制的模板
     */
    NotificationTemplate copyTemplate(Long sourceTemplateId, String newTemplateName, String newTemplateCode);
    
    /**
     * 批量启用模板
     * 
     * @param templateIds 模板ID列表
     * @return 启用数量
     */
    int enableBatchTemplates(List<Long> templateIds);
    
    /**
     * 批量禁用模板
     * 
     * @param templateIds 模板ID列表
     * @return 禁用数量
     */
    int disableBatchTemplates(List<Long> templateIds);
    
    /**
     * 批量删除模板
     * 
     * @param templateIds 模板ID列表
     * @return 删除数量
     */
    int deleteBatchTemplates(List<Long> templateIds);
    
    /**
     * 获取模板统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getTemplateStatistics();
    
    /**
     * 导入模板
     * 
     * @param templates 模板列表
     * @param overwrite 是否覆盖已存在的模板
     * @return 导入结果
     */
    Map<String, Integer> importTemplates(List<NotificationTemplate> templates, boolean overwrite);
    
    /**
     * 导出模板
     * 
     * @param templateIds 模板ID列表（为空则导出所有）
     * @return 模板列表
     */
    List<NotificationTemplate> exportTemplates(List<Long> templateIds);
    
    /**
     * 预览模板渲染效果
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @return 预览结果
     */
    Map<String, String> previewTemplate(String templateCode, Map<String, Object> data);
    
    /**
     * 测试模板发送
     * 
     * @param templateCode 模板编码
     * @param data 模板数据
     * @param testRecipient 测试接收方
     * @return 测试结果
     */
    boolean testTemplate(String templateCode, Map<String, Object> data, String testRecipient);
    
    /**
     * 获取模板使用统计
     * 
     * @param templateCode 模板编码
     * @return 使用统计
     */
    Map<String, Object> getTemplateUsageStatistics(String templateCode);
    
    /**
     * 查询最近使用的模板
     * 
     * @param limit 数量限制
     * @return 模板列表
     */
    List<NotificationTemplate> getRecentlyUsedTemplates(int limit);
    
    /**
     * 查询热门模板
     * 
     * @param limit 数量限制
     * @return 模板列表
     */
    List<NotificationTemplate> getPopularTemplates(int limit);
}