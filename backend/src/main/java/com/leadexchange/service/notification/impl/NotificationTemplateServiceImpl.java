package com.leadexchange.service.notification.impl;

import com.leadexchange.domain.notification.NotificationTemplate;
import com.leadexchange.domain.notification.NotificationType;
import com.leadexchange.domain.notification.TemplateStatus;
import com.leadexchange.domain.notification.TemplateType;
import com.leadexchange.repository.notification.NotificationTemplateRepository;
import com.leadexchange.service.notification.NotificationTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 通知模板服务实现类
 * 提供通知模板的创建、管理、渲染等业务操作的具体实现
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Service
@Transactional
public class NotificationTemplateServiceImpl implements NotificationTemplateService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationTemplateServiceImpl.class);
    
    @Autowired
    private NotificationTemplateRepository templateRepository;
    
    // 模板变量的正则表达式
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");
    
    @Override
    public NotificationTemplate createTemplate(String templateName, String templateCode, 
                                             TemplateType templateType, NotificationType notificationType, 
                                             String title, String content, 
                                             String variables, Integer priority, String remark) {
        
        logger.info("创建通知模板: name={}, code={}, type={}, notificationType={}", 
                   templateName, templateCode, templateType, notificationType);
        
        // 参数验证
        if (!StringUtils.hasText(templateName) || !StringUtils.hasText(templateCode) || 
            templateType == null || notificationType == null || !StringUtils.hasText(content)) {
            throw new IllegalArgumentException("创建模板的必要参数不能为空");
        }
        
        // 检查模板编码是否已存在
        if (templateRepository.existsByTemplateCode(templateCode)) {
            throw new IllegalArgumentException("模板编码已存在: " + templateCode);
        }
        
        // 验证模板语法
        validateTemplateSyntax(title, content);
        
        // 创建模板
        NotificationTemplate template = new NotificationTemplate();
        template.setTemplateName(templateName);
        template.setTemplateCode(templateCode);
        template.setTemplateType(templateType);
        template.setEventType(notificationType);
        template.setTemplateTitle(title);
        template.setTemplateContent(content);
        template.setVariables(variables);
        template.setPriority(priority != null ? priority : 5);
        template.setStatus(TemplateStatus.ACTIVE);
        template.setIsEnabled(true);
        template.setMaxRetryCount(3);
        template.setRemark(remark);
        
        // 设置过期时间（默认24小时）
        template.setExpireHours(24);
        
        template = templateRepository.save(template);
        
        logger.info("通知模板创建成功: id={}, code={}", template.getId(), template.getTemplateCode());
        return template;
    }
    
    @Override
    public NotificationTemplate updateTemplate(Long templateId, String templateName, String title, 
                                             String content, String variables, Integer priority, 
                                             String remark) {
        
        logger.info("更新通知模板: id={}", templateId);
        
        Optional<NotificationTemplate> optionalTemplate = templateRepository.findById(templateId);
        if (!optionalTemplate.isPresent()) {
            throw new IllegalArgumentException("模板不存在: " + templateId);
        }
        
        NotificationTemplate template = optionalTemplate.get();
        
        // 验证模板语法
        if (StringUtils.hasText(title) || StringUtils.hasText(content)) {
            validateTemplateSyntax(title != null ? title : template.getTemplateTitle(), 
                                 content != null ? content : template.getTemplateContent());
        }
        
        // 更新字段
        if (StringUtils.hasText(templateName)) {
            template.setTemplateName(templateName);
        }
        if (StringUtils.hasText(title)) {
            template.setTemplateTitle(title);
        }
        if (StringUtils.hasText(content)) {
            template.setTemplateContent(content);
        }
        if (StringUtils.hasText(variables)) {
            template.setVariables(variables);
        }
        if (priority != null) {
            template.setPriority(priority);
        }
        if (StringUtils.hasText(remark)) {
            template.setRemark(remark);
        }
        
        template = templateRepository.save(template);
        
        logger.info("通知模板更新成功: id={}, code={}", template.getId(), template.getTemplateCode());
        return template;
    }
    
    @Override
    public boolean deleteTemplate(Long templateId) {
        logger.info("删除通知模板: id={}", templateId);
        
        if (!templateRepository.existsById(templateId)) {
            logger.error("模板不存在: id={}", templateId);
            return false;
        }
        
        templateRepository.deleteById(templateId);
        
        logger.info("通知模板删除成功: id={}", templateId);
        return true;
    }
    
    @Override
    public boolean enableTemplate(Long templateId) {
        logger.info("启用通知模板: id={}", templateId);
        
        Optional<NotificationTemplate> optionalTemplate = templateRepository.findById(templateId);
        if (!optionalTemplate.isPresent()) {
            logger.error("模板不存在: id={}", templateId);
            return false;
        }
        
        NotificationTemplate template = optionalTemplate.get();
        template.enable();
        templateRepository.save(template);
        
        logger.info("通知模板启用成功: id={}, code={}", template.getId(), template.getTemplateCode());
        return true;
    }
    
    @Override
    public boolean disableTemplate(Long templateId) {
        logger.info("禁用通知模板: id={}", templateId);
        
        Optional<NotificationTemplate> optionalTemplate = templateRepository.findById(templateId);
        if (!optionalTemplate.isPresent()) {
            logger.error("模板不存在: id={}", templateId);
            return false;
        }
        
        NotificationTemplate template = optionalTemplate.get();
        template.disable();
        templateRepository.save(template);
        
        logger.info("通知模板禁用成功: id={}, code={}", template.getId(), template.getTemplateCode());
        return true;
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateById(Long templateId) {
        return templateRepository.findById(templateId).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public NotificationTemplate getTemplateByCode(String templateCode) {
        return templateRepository.findByTemplateCode(templateCode).orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByEventType(String eventType) {
        // 将String转换为NotificationType
        NotificationType notificationType;
        try {
            notificationType = NotificationType.valueOf(eventType);
        } catch (IllegalArgumentException e) {
            logger.error("无效的事件类型: {}", eventType);
            return Collections.emptyList();
        }
        return templateRepository.findByEventTypeAndIsEnabled(notificationType, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getTemplatesByNotificationType(NotificationType notificationType) {
        return templateRepository.findByEventTypeAndIsEnabled(notificationType, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplate> getTemplatesByCondition(TemplateType templateType, NotificationType notificationType, 
                                                            TemplateStatus status, Boolean isEnabled, Pageable pageable) {
        
        // 根据条件动态查询
        if (templateType != null && notificationType != null && status != null && isEnabled != null) {
            return templateRepository.findByTemplateTypeAndNotificationTypeAndStatusAndIsEnabled(
                    templateType, notificationType, status, isEnabled, pageable);
        } else if (templateType != null && notificationType != null) {
            return templateRepository.findByTemplateTypeAndNotificationType(templateType, notificationType, pageable);
        } else if (notificationType != null && isEnabled != null) {
            return templateRepository.findByNotificationTypeAndIsEnabled(notificationType, isEnabled, pageable);
        } else if (status != null) {
            return templateRepository.findByStatus(status, pageable);
        } else {
            return templateRepository.findAll(pageable);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplate> getAllTemplates(Pageable pageable) {
        return templateRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> renderTemplate(String templateCode, Map<String, Object> data) {
        logger.debug("渲染模板: code={}", templateCode);
        
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null) {
            logger.error("模板不存在: code={}", templateCode);
            return null;
        }
        
        if (!template.isAvailable()) {
            logger.error("模板不可用: code={}, status={}, enabled={}", 
                        templateCode, template.getStatus(), template.getIsEnabled());
            return null;
        }
        
        Map<String, String> result = new HashMap<>();
        
        try {
            // 渲染标题
            String renderedTitle = renderTemplateContent(template.getTemplateTitle(), data);
            result.put("title", renderedTitle);
            
            // 渲染内容
            String renderedContent = renderTemplateContent(template.getTemplateContent(), data);
            result.put("content", renderedContent);
            
            logger.debug("模板渲染成功: code={}", templateCode);
            return result;
            
        } catch (Exception e) {
            logger.error("模板渲染失败: code={}", templateCode, e);
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String renderTitle(String templateCode, Map<String, Object> data) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null || !template.isAvailable()) {
            return null;
        }
        
        try {
            return renderTemplateContent(template.getTemplateTitle(), data);
        } catch (Exception e) {
            logger.error("渲染模板标题失败: code={}", templateCode, e);
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public String renderContent(String templateCode, Map<String, Object> data) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null || !template.isAvailable()) {
            return null;
        }
        
        try {
            return renderTemplateContent(template.getTemplateContent(), data);
        } catch (Exception e) {
            logger.error("渲染模板内容失败: code={}", templateCode, e);
            return null;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateTemplateSyntax(String title, String content) {
        try {
            // 检查标题语法
            if (StringUtils.hasText(title)) {
                validateContent(title);
            }
            
            // 检查内容语法
            if (StringUtils.hasText(content)) {
                validateContent(content);
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("模板语法验证失败: title={}, content={}", title, content, e);
            throw new IllegalArgumentException("模板语法错误: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean validateTemplateVariables(String templateCode, Map<String, Object> data) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null) {
            return false;
        }
        
        try {
            // 获取模板中的所有变量
            Set<String> templateVariables = extractVariables(template.getTemplateTitle(), template.getTemplateContent());
            
            // 检查数据中是否包含所有必需的变量
            if (data == null || data.isEmpty()) {
                return templateVariables.isEmpty();
            }
            
            for (String variable : templateVariables) {
                if (!data.containsKey(variable)) {
                    logger.warn("缺少模板变量: template={}, variable={}", templateCode, variable);
                    return false;
                }
            }
            
            return true;
            
        } catch (Exception e) {
            logger.error("验证模板变量失败: code={}", templateCode, e);
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<String> getTemplateVariables(String templateCode) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        if (template == null) {
            return Collections.emptyList();
        }
        
        Set<String> variables = extractVariables(template.getTemplateTitle(), template.getTemplateContent());
        return new ArrayList<>(variables);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean isTemplateAvailable(String templateCode) {
        NotificationTemplate template = getTemplateByCode(templateCode);
        return template != null && template.isAvailable();
    }
    
    @Override
    public NotificationTemplate copyTemplate(Long templateId, String newTemplateCode, String newTemplateName) {
        logger.info("复制通知模板: sourceId={}, newCode={}, newName={}", templateId, newTemplateCode, newTemplateName);
        
        Optional<NotificationTemplate> optionalTemplate = templateRepository.findById(templateId);
        if (!optionalTemplate.isPresent()) {
            throw new IllegalArgumentException("源模板不存在: " + templateId);
        }
        
        // 检查新编码是否已存在
        if (templateRepository.existsByTemplateCode(newTemplateCode)) {
            throw new IllegalArgumentException("模板编码已存在: " + newTemplateCode);
        }
        
        NotificationTemplate sourceTemplate = optionalTemplate.get();
        
        // 创建新模板
        NotificationTemplate newTemplate = new NotificationTemplate();
        newTemplate.setTemplateName(newTemplateName);
        newTemplate.setTemplateCode(newTemplateCode);
        newTemplate.setTemplateType(sourceTemplate.getTemplateType());
        newTemplate.setNotificationType(sourceTemplate.getNotificationType());
        newTemplate.setEventType(sourceTemplate.getEventType());
        newTemplate.setTemplateTitle(sourceTemplate.getTemplateTitle());
        newTemplate.setTemplateContent(sourceTemplate.getTemplateContent());
        newTemplate.setVariables(sourceTemplate.getVariables());
        newTemplate.setPriority(sourceTemplate.getPriority());
        newTemplate.setStatus(TemplateStatus.ACTIVE);
        newTemplate.setIsEnabled(false); // 复制的模板默认禁用
        newTemplate.setMaxRetryCount(sourceTemplate.getMaxRetryCount());
        newTemplate.setExpireTime(sourceTemplate.getExpireTime());
        newTemplate.setRemark("复制自: " + sourceTemplate.getTemplateName());
        
        newTemplate = templateRepository.save(newTemplate);
        
        logger.info("通知模板复制成功: sourceId={}, newId={}, newCode={}", 
                   templateId, newTemplate.getId(), newTemplateCode);
        return newTemplate;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplate> searchTemplates(String templateName, NotificationType notificationType, 
                                                    String eventType, Boolean enabled, Pageable pageable) {
        logger.info("搜索模板: name={}, type={}, eventType={}, enabled={}", 
                   templateName, notificationType, eventType, enabled);
        
        // 构建查询条件
        Specification<NotificationTemplate> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 模板名称模糊查询
            if (StringUtils.hasText(templateName)) {
                predicates.add(criteriaBuilder.like(root.get("templateName"), "%" + templateName + "%"));
            }
            
            // 通知类型
            if (notificationType != null) {
                predicates.add(criteriaBuilder.equal(root.get("notificationType"), notificationType));
            }
            
            // 事件类型
            if (StringUtils.hasText(eventType)) {
                predicates.add(criteriaBuilder.equal(root.get("eventType"), eventType));
            }
            
            // 是否启用
            if (enabled != null) {
                predicates.add(criteriaBuilder.equal(root.get("enabled"), enabled));
            }
            
            // 排除已删除的记录
            predicates.add(criteriaBuilder.equal(root.get("deleted"), false));
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return templateRepository.findAll(spec, pageable);
    }
    

    
    @Override
    @Transactional(readOnly = true)
    public Page<NotificationTemplate> getTemplates(Pageable pageable) {
        logger.info("查询模板: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());
        return templateRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getAllAvailableTemplates() {
        logger.info("查询所有可用模板");
        return templateRepository.findAllAvailableTemplates();
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String templateCode) {
        return templateRepository.existsByTemplateCode(templateCode);
    }
    
    @Override
    @Transactional
    public int enableBatchTemplates(List<Long> templateIds) {
        logger.info("批量启用模板: count={}", templateIds.size());
        
        int enabledCount = 0;
        for (Long templateId : templateIds) {
            try {
                NotificationTemplate template = getTemplateById(templateId);
                if (template != null && !template.getIsEnabled()) {
                    template.enable();
                    templateRepository.save(template);
                    enabledCount++;
                }
            } catch (Exception e) {
                logger.error("启用模板失败: id={}", templateId, e);
            }
        }
        
        logger.info("批量启用模板完成: enabled={}, total={}", enabledCount, templateIds.size());
        return enabledCount;
    }
    
    @Override
    @Transactional
    public int disableBatchTemplates(List<Long> templateIds) {
        logger.info("批量禁用模板: count={}", templateIds.size());
        
        int disabledCount = 0;
        for (Long templateId : templateIds) {
            try {
                NotificationTemplate template = getTemplateById(templateId);
                if (template != null && template.getIsEnabled()) {
                    template.disable();
                    templateRepository.save(template);
                    disabledCount++;
                }
            } catch (Exception e) {
                logger.error("禁用模板失败: id={}", templateId, e);
            }
        }
        
        logger.info("批量禁用模板完成: disabled={}, total={}", disabledCount, templateIds.size());
        return disabledCount;
    }
    
    @Override
    @Transactional
    public int deleteBatchTemplates(List<Long> templateIds) {
        logger.info("批量删除模板: count={}", templateIds.size());
        
        int deletedCount = 0;
        for (Long templateId : templateIds) {
            try {
                if (templateRepository.existsById(templateId)) {
                    templateRepository.deleteById(templateId);
                    deletedCount++;
                }
            } catch (Exception e) {
                logger.error("删除模板失败: id={}", templateId, e);
            }
        }
        
        logger.info("批量删除模板完成: deleted={}, total={}", deletedCount, templateIds.size());
        return deletedCount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTemplateStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总数统计
        long totalCount = templateRepository.count();
        statistics.put("totalCount", totalCount);
        
        // 状态统计
        for (TemplateStatus status : TemplateStatus.values()) {
            long count = templateRepository.countByStatus(status);
            statistics.put(status.name().toLowerCase() + "Count", count);
        }
        
        // 类型统计
        for (TemplateType type : TemplateType.values()) {
            long count = templateRepository.countByTemplateType(type);
            statistics.put(type.name().toLowerCase() + "Count", count);
        }
        
        // 通知类型统计
        for (NotificationType notificationType : NotificationType.values()) {
            long count = templateRepository.countByNotificationType(notificationType);
            statistics.put(notificationType.name().toLowerCase() + "Count", count);
        }
        
        // 启用状态统计
        long enabledCount = templateRepository.countByIsEnabled(true);
        long disabledCount = templateRepository.countByIsEnabled(false);
        statistics.put("enabledCount", enabledCount);
        statistics.put("disabledCount", disabledCount);
        
        return statistics;
    }
    
    @Override
    public Map<String, Integer> importTemplates(List<NotificationTemplate> templates, boolean overwrite) {
        logger.info("导入模板: count={}, overwrite={}", templates.size(), overwrite);
        
        Map<String, Integer> result = new HashMap<>();
        int imported = 0;
        int skipped = 0;
        int failed = 0;
        
        for (NotificationTemplate template : templates) {
            try {
                // 检查是否已存在
                boolean exists = existsByCode(template.getTemplateCode());
                if (exists && !overwrite) {
                    skipped++;
                    continue;
                }
                
                // 保存或更新模板
                if (exists && overwrite) {
                    NotificationTemplate existingTemplate = getTemplateByCode(template.getTemplateCode());
                    template.setId(existingTemplate.getId());
                }
                
                templateRepository.save(template);
                imported++;
                
            } catch (Exception e) {
                logger.error("导入模板失败: code={}", template.getTemplateCode(), e);
                failed++;
            }
        }
        
        result.put("imported", imported);
        result.put("skipped", skipped);
        result.put("failed", failed);
        
        logger.info("导入模板完成: imported={}, skipped={}, failed={}", imported, skipped, failed);
        return result;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> exportTemplates(List<Long> templateIds) {
        logger.info("导出模板: count={}", templateIds == null ? 0 : templateIds.size());
        
        List<NotificationTemplate> exportData = new ArrayList<>();
        
        if (templateIds == null || templateIds.isEmpty()) {
            // 导出所有模板
            exportData = templateRepository.findAll();
        } else {
            // 导出指定模板
            for (Long templateId : templateIds) {
                try {
                    NotificationTemplate template = getTemplateById(templateId);
                    if (template != null) {
                        exportData.add(template);
                    }
                } catch (Exception e) {
                    logger.error("导出模板失败: id={}", templateId, e);
                }
            }
        }
        
        logger.info("导出模板完成: exported={}", exportData.size());
        return exportData;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> previewTemplate(String templateCode, Map<String, Object> data) {
        return renderTemplate(templateCode, data);
    }
    
    @Override
    public boolean testTemplate(String templateCode, Map<String, Object> data, String testRecipient) {
        logger.info("测试模板: code={}, recipient={}", templateCode, testRecipient);
        
        try {
            // 渲染模板
            Map<String, String> renderedContent = renderTemplate(templateCode, data);
            if (renderedContent == null || renderedContent.isEmpty()) {
                logger.error("模板渲染失败: code={}", templateCode);
                return false;
            }
            
            // 这里可以发送测试通知到指定接收者
            logger.info("模板测试成功: code={}, title={}, content={}", 
                       templateCode, renderedContent.get("title"), renderedContent.get("content"));
            
            return true;
            
        } catch (Exception e) {
            logger.error("模板测试失败: code={}", templateCode, e);
            return false;
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTemplateUsageStatistics(String templateCode) {
        // TODO: 实现模板使用统计
        // 这里需要统计模板的使用次数、成功率等信息
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("templateCode", templateCode);
        statistics.put("usageCount", 0);
        statistics.put("successCount", 0);
        statistics.put("failureCount", 0);
        statistics.put("successRate", 0.0);
        return statistics;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getRecentlyUsedTemplates(int limit) {
        // TODO: 实现最近使用的模板查询
        // 这里需要根据模板的使用时间排序
        return templateRepository.findByIsEnabledOrderByUpdateTimeDesc(true, 
                org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<NotificationTemplate> getPopularTemplates(int limit) {
        // TODO: 实现热门模板查询
        // 这里需要根据模板的使用频率排序
        return templateRepository.findByIsEnabledOrderByPriorityDesc(true, 
                org.springframework.data.domain.PageRequest.of(0, limit)).getContent();
    }
    
    // 私有辅助方法
    
    /**
     * 渲染内容
     */
    private String renderTemplateContent(String template, Map<String, Object> data) {
        if (!StringUtils.hasText(template)) {
            return "";
        }
        
        if (data == null || data.isEmpty()) {
            return template;
        }
        
        String result = template;
        
        // 替换变量
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String variable = matcher.group(1);
            Object value = data.get(variable);
            if (value != null) {
                result = result.replace("${" + variable + "}", value.toString());
            } else {
                logger.warn("模板变量未找到: {}", variable);
                result = result.replace("${" + variable + "}", "");
            }
        }
        
        return result;
    }
    
    /**
     * 验证内容语法
     */
    private void validateContent(String content) {
        if (!StringUtils.hasText(content)) {
            return;
        }
        
        // 检查变量语法
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            String variable = matcher.group(1);
            if (!StringUtils.hasText(variable)) {
                throw new IllegalArgumentException("变量名不能为空");
            }
            if (!variable.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
                throw new IllegalArgumentException("变量名格式错误: " + variable);
            }
        }
    }
    
    /**
     * 提取变量
     */
    private Set<String> extractVariables(String title, String content) {
        Set<String> variables = new HashSet<>();
        
        // 从标题中提取变量
        if (StringUtils.hasText(title)) {
            Matcher matcher = VARIABLE_PATTERN.matcher(title);
            while (matcher.find()) {
                variables.add(matcher.group(1));
            }
        }
        
        // 从内容中提取变量
        if (StringUtils.hasText(content)) {
            Matcher matcher = VARIABLE_PATTERN.matcher(content);
            while (matcher.find()) {
                variables.add(matcher.group(1));
            }
        }
        
        return variables;
    }
    
    /**
     * 从数据创建模板
     */
    private NotificationTemplate createTemplateFromData(Map<String, Object> data) {
        String templateName = (String) data.get("templateName");
        String templateCode = (String) data.get("templateCode");
        String templateType = (String) data.get("templateType");
        String notificationType = (String) data.get("notificationType");
        String title = (String) data.get("title");
        String content = (String) data.get("content");
        String variables = (String) data.get("variables");
        Integer priority = (Integer) data.get("priority");
        String remark = (String) data.get("remark");
        
        return createTemplate(templateName, templateCode, 
                            TemplateType.valueOf(templateType), 
                            NotificationType.valueOf(notificationType), 
                            title, content, variables, priority, remark);
    }
    
    /**
     * 将模板转换为数据
     */
    private Map<String, Object> convertTemplateToData(NotificationTemplate template) {
        Map<String, Object> data = new HashMap<>();
        data.put("templateName", template.getTemplateName());
        data.put("templateCode", template.getTemplateCode());
        data.put("templateType", template.getTemplateType().name());
        data.put("notificationType", template.getNotificationType().name());
        data.put("eventType", template.getEventType());
        data.put("title", template.getTemplateTitle());
        data.put("content", template.getTemplateContent());
        data.put("variables", template.getVariables());
        data.put("priority", template.getPriority());
        data.put("status", template.getStatus());
        data.put("isEnabled", template.getIsEnabled());
        data.put("maxRetryCount", template.getMaxRetryCount());
        data.put("expireTime", template.getExpireTime());
        data.put("remark", template.getRemark());
        return data;
    }
}