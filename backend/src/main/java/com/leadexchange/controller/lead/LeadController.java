package com.leadexchange.controller.lead;

import com.leadexchange.service.lead.LeadService;
import com.leadexchange.domain.lead.Lead;
import com.leadexchange.domain.lead.LeadStatus;
import com.leadexchange.domain.lead.AuditStatus;
import com.leadexchange.common.result.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 线索管理控制器
 * 处理线索发布、编辑、审核、搜索等操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Tag(name = "线索管理", description = "线索管理相关接口")
@RestController
@RequestMapping("/api/leads")
public class LeadController {

    private static final Logger logger = LoggerFactory.getLogger(LeadController.class);

    @Autowired
    private LeadService leadService;

    /**
     * 创建线索
     */
    @Operation(summary = "创建线索", description = "创建新的线索信息")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public Result<Object> createLead(@Valid @RequestBody Lead lead, HttpServletRequest request) {
        try {
            logger.info("创建线索请求: {}", lead.getCompanyName());
            
            // TODO: 从JWT token中获取当前用户ID
            Long currentUserId = getCurrentUserId(request);
            lead.setOwnerId(currentUserId);
            
            // 检测重复线索
            List<Lead> duplicates = leadService.detectDuplicateLeads(lead);
            if (!duplicates.isEmpty()) {
                logger.warn("发现重复线索: {}", duplicates.size());
                return Result.warning("发现可能重复的线索，请检查后再提交", duplicates);
            }
            
            Lead createdLead = leadService.createLead(lead);
            return Result.success("线索创建成功", createdLead);
            
        } catch (Exception e) {
            logger.error("创建线索失败", e);
            return Result.error("创建线索失败: " + e.getMessage());
        }
    }

    /**
     * 获取线索列表
     */
    @Operation(summary = "获取线索列表", description = "分页获取线索列表")
    @GetMapping
    public Result<IPage<Lead>> getLeadList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "线索状态") @RequestParam(required = false) LeadStatus status,
            @Parameter(description = "所有者ID") @RequestParam(required = false) Long ownerId) {
        try {
            logger.debug("获取线索列表: page={}, size={}, status={}", page, size, status);
            
            Page<Lead> pageParam = new Page<>(page, size);
            IPage<Lead> leadPage = leadService.getLeadPage(pageParam, ownerId, status);
            
            return Result.success("获取线索列表成功", leadPage);
            
        } catch (Exception e) {
            logger.error("获取线索列表失败", e);
            return Result.error("获取线索列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取线索详情
     */
    @Operation(summary = "获取线索详情", description = "根据ID获取线索详细信息")
    @GetMapping("/{id}")
    public Result<Lead> getLeadById(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            logger.debug("获取线索详情: {}", id);
            
            Lead lead = leadService.getLeadById(id);
            if (lead == null) {
                return Result.error("线索不存在");
            }
            
            // 增加浏览次数
            Long currentUserId = getCurrentUserId(request);
            String ipAddress = getClientIpAddress(request);
            leadService.incrementViewCount(id, currentUserId, ipAddress);
            
            return Result.success("获取线索详情成功", lead);
            
        } catch (Exception e) {
            logger.error("获取线索详情失败", e);
            return Result.error("获取线索详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新线索信息
     */
    @Operation(summary = "更新线索信息", description = "更新线索的详细信息")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Lead> updateLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            @Valid @RequestBody Lead lead,
            HttpServletRequest request) {
        try {
            logger.info("更新线索: {}", id);
            
            // 验证权限：只有线索所有者或管理员可以更新
            Long currentUserId = getCurrentUserId(request);
            Lead existingLead = leadService.getLeadById(id);
            if (existingLead == null) {
                return Result.error("线索不存在");
            }
            
            if (!existingLead.getOwnerId().equals(currentUserId) && !isAdmin(request)) {
                return Result.error("无权限更新此线索");
            }
            
            lead.setId(id);
            Lead updatedLead = leadService.updateLead(lead);
            
            return Result.success("线索更新成功", updatedLead);
            
        } catch (Exception e) {
            logger.error("更新线索失败", e);
            return Result.error("更新线索失败: " + e.getMessage());
        }
    }

    /**
     * 删除线索
     */
    @Operation(summary = "删除线索", description = "删除指定的线索")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> deleteLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            logger.info("删除线索: {}", id);
            
            // 验证权限：只有线索所有者或管理员可以删除
            Long currentUserId = getCurrentUserId(request);
            Lead existingLead = leadService.getLeadById(id);
            if (existingLead == null) {
                return Result.error("线索不存在");
            }
            
            if (!existingLead.getOwnerId().equals(currentUserId) && !isAdmin(request)) {
                return Result.error("无权限删除此线索");
            }
            
            boolean deleted = leadService.deleteLead(id);
            if (deleted) {
                return Result.success("线索删除成功");
            } else {
                return Result.error("线索删除失败");
            }
            
        } catch (Exception e) {
            logger.error("删除线索失败", e);
            return Result.error("删除线索失败: " + e.getMessage());
        }
    }

    /**
     * 发布线索
     */
    @Operation(summary = "发布线索", description = "将线索发布到平台")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> publishLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            logger.info("发布线索: {}", id);
            
            // 验证权限
            Long currentUserId = getCurrentUserId(request);
            Lead existingLead = leadService.getLeadById(id);
            if (existingLead == null) {
                return Result.error("线索不存在");
            }
            
            if (!existingLead.getOwnerId().equals(currentUserId) && !isAdmin(request)) {
                return Result.error("无权限发布此线索");
            }
            
            boolean published = leadService.publishLead(id);
            if (published) {
                return Result.success("线索发布成功");
            } else {
                return Result.error("线索发布失败，请检查审核状态");
            }
            
        } catch (Exception e) {
            logger.error("发布线索失败", e);
            return Result.error("发布线索失败: " + e.getMessage());
        }
    }

    /**
     * 下架线索
     */
    @Operation(summary = "下架线索", description = "将线索从平台下架")
    @PostMapping("/{id}/offline")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> offlineLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            logger.info("下架线索: {}", id);
            
            // 验证权限
            Long currentUserId = getCurrentUserId(request);
            Lead existingLead = leadService.getLeadById(id);
            if (existingLead == null) {
                return Result.error("线索不存在");
            }
            
            if (!existingLead.getOwnerId().equals(currentUserId) && !isAdmin(request)) {
                return Result.error("无权限下架此线索");
            }
            
            boolean offline = leadService.offlineLead(id);
            if (offline) {
                return Result.success("线索下架成功");
            } else {
                return Result.error("线索下架失败");
            }
            
        } catch (Exception e) {
            logger.error("下架线索失败", e);
            return Result.error("下架线索失败: " + e.getMessage());
        }
    }

    /**
     * 搜索线索
     */
    @Operation(summary = "搜索线索", description = "根据关键词搜索线索")
    @GetMapping("/search")
    public Result<IPage<Lead>> searchLeads(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        try {
            logger.debug("搜索线索: keyword={}, page={}, size={}", keyword, page, size);
            
            Page<Lead> pageParam = new Page<>(page, size);
            IPage<Lead> leadPage = leadService.searchLeads(keyword, pageParam);
            
            return Result.success("搜索线索成功", leadPage);
            
        } catch (Exception e) {
            logger.error("搜索线索失败", e);
            return Result.error("搜索线索失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的线索
     */
    @Operation(summary = "获取我的线索", description = "获取当前用户的线索列表")
    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public Result<IPage<Lead>> getMyLeads(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "线索状态") @RequestParam(required = false) LeadStatus status,
            HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);
            logger.debug("获取我的线索: userId={}, page={}, size={}", currentUserId, page, size);
            
            Page<Lead> pageParam = new Page<>(page, size);
            IPage<Lead> leadPage = leadService.getLeadPage(pageParam, currentUserId, status);
            
            return Result.success("获取我的线索成功", leadPage);
            
        } catch (Exception e) {
            logger.error("获取我的线索失败", e);
            return Result.error("获取我的线索失败: " + e.getMessage());
        }
    }

    /**
     * 审核线索
     */
    @Operation(summary = "审核线索", description = "审核线索信息")
    @PostMapping("/{id}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> auditLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam AuditStatus auditStatus,
            @Parameter(description = "审核备注") @RequestParam(required = false) String auditRemark,
            HttpServletRequest request) {
        try {
            logger.info("审核线索: id={}, status={}", id, auditStatus);
            
            Long auditorId = getCurrentUserId(request);
            boolean audited = leadService.auditLead(id, auditStatus, auditRemark, auditorId);
            
            if (audited) {
                return Result.success("线索审核完成");
            } else {
                return Result.error("线索审核失败");
            }
            
        } catch (Exception e) {
            logger.error("审核线索失败", e);
            return Result.error("审核线索失败: " + e.getMessage());
        }
    }

    /**
     * 收藏线索
     */
    @Operation(summary = "收藏线索", description = "收藏指定的线索")
    @PostMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> favoriteLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);
            logger.info("收藏线索: userId={}, leadId={}", currentUserId, id);
            
            boolean favorited = leadService.favoriteLead(currentUserId, id);
            if (favorited) {
                return Result.success("线索收藏成功");
            } else {
                return Result.error("线索收藏失败");
            }
            
        } catch (Exception e) {
            logger.error("收藏线索失败", e);
            return Result.error("收藏线索失败: " + e.getMessage());
        }
    }

    /**
     * 取消收藏线索
     */
    @Operation(summary = "取消收藏线索", description = "取消收藏指定的线索")
    @DeleteMapping("/{id}/favorite")
    @PreAuthorize("hasRole('USER')")
    public Result<Void> unfavoriteLead(
            @Parameter(description = "线索ID") @PathVariable Long id,
            HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);
            logger.info("取消收藏线索: userId={}, leadId={}", currentUserId, id);
            
            boolean unfavorited = leadService.unfavoriteLead(currentUserId, id);
            if (unfavorited) {
                return Result.success("取消收藏成功");
            } else {
                return Result.error("取消收藏失败");
            }
            
        } catch (Exception e) {
            logger.error("取消收藏线索失败", e);
            return Result.error("取消收藏线索失败: " + e.getMessage());
        }
    }

    /**
     * 获取收藏的线索
     */
    @Operation(summary = "获取收藏的线索", description = "获取当前用户收藏的线索列表")
    @GetMapping("/favorites")
    @PreAuthorize("hasRole('USER')")
    public Result<IPage<Lead>> getFavoriteLeads(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long currentUserId = getCurrentUserId(request);
            logger.debug("获取收藏线索: userId={}, page={}, size={}", currentUserId, page, size);
            
            Page<Lead> pageParam = new Page<>(page, size);
            IPage<Lead> leadPage = leadService.getFavoriteLeads(currentUserId, pageParam);
            
            return Result.success("获取收藏线索成功", leadPage);
            
        } catch (Exception e) {
            logger.error("获取收藏线索失败", e);
            return Result.error("获取收藏线索失败: " + e.getMessage());
        }
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        // TODO: 从JWT token中解析用户ID
        // 这里暂时返回固定值，实际应该从Security Context或JWT中获取
        return 1L;
    }

    /**
     * 判断当前用户是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        // TODO: 从JWT token中解析用户角色
        // 这里暂时返回false，实际应该从Security Context中获取
        return false;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0];
        }
        
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        return request.getRemoteAddr();
    }
}