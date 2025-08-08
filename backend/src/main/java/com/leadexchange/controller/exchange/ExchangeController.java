package com.leadexchange.controller.exchange;

import com.leadexchange.common.result.Result;
import com.leadexchange.domain.exchange.ExchangeApplication;
import com.leadexchange.domain.exchange.ExchangeHistory;
import com.leadexchange.domain.exchange.UserCredit;
import com.leadexchange.service.ExchangeEngineService;
import com.leadexchange.service.UserCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * 交换引擎控制器
 * 处理线索交换申请、审核、执行等操作
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@Tag(name = "交换引擎", description = "线索交换相关接口")
@RestController
@RequestMapping("/api/exchanges")
@RequiredArgsConstructor
public class ExchangeController {
    
    private static final Logger log = LoggerFactory.getLogger(ExchangeController.class);
    
    private final ExchangeEngineService exchangeEngineService;
    private final UserCreditService userCreditService;
    
    /**
     * 申请线索交换
     */
    @Operation(summary = "申请线索交换", description = "用户申请用自己的线索交换其他用户的线索")
    @PostMapping("/apply")
    public Result<ExchangeApplication> applyForExchange(
            @Valid @RequestBody ExchangeApplicationRequest request,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        
        ExchangeApplication application = exchangeEngineService.applyForExchange(
                userId, request.getTargetLeadId(), request.getOfferedLeadIds(), request.getReason());
        
        log.info("用户[{}]申请线索交换，申请ID：{}", userId, application.getId());
        
        return Result.success(application);
    }
    
    /**
     * 获取交换申请详情
     */
    @Operation(summary = "获取交换申请详情", description = "根据申请ID获取交换申请的详细信息")
    @GetMapping("/{id}")
    public Result<ExchangeApplication> getExchangeApplication(
            @Parameter(description = "申请ID") @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        ExchangeApplication application = exchangeEngineService.getApplicationById(id);
        
        // 验证访问权限（只有申请人或目标线索所有者可以查看）
        if (!application.getApplicantId().equals(userId) && 
            !application.getTargetOwnerId().equals(userId)) {
            return Result.error("无权限查看此交换申请");
        }
        
        return Result.success(application);
    }
    
    /**
     * 审核通过交换申请
     */
    @Operation(summary = "审核通过交换申请", description = "线索所有者审核通过交换申请")
    @PostMapping("/{id}/approve")
    public Result<ExchangeApplication> approveExchange(
            @Parameter(description = "申请ID") @PathVariable Long id,
            @Valid @RequestBody ExchangeReviewRequest request,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        
        ExchangeApplication application = exchangeEngineService.approveExchange(
                id, userId, request.getResponseMessage());
        
        log.info("用户[{}]审核通过交换申请，申请ID：{}", userId, id);
        
        return Result.success(application);
    }
    
    /**
     * 拒绝交换申请
     */
    @Operation(summary = "拒绝交换申请", description = "线索所有者拒绝交换申请")
    @PostMapping("/{id}/reject")
    public Result<ExchangeApplication> rejectExchange(
            @Parameter(description = "申请ID") @PathVariable Long id,
            @Valid @RequestBody ExchangeReviewRequest request,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        
        ExchangeApplication application = exchangeEngineService.rejectExchange(
                id, userId, request.getResponseMessage());
        
        log.info("用户[{}]拒绝交换申请，申请ID：{}", userId, id);
        
        return Result.success(application);
    }
    
    /**
     * 取消交换申请
     */
    @Operation(summary = "取消交换申请", description = "申请人取消自己的交换申请")
    @PostMapping("/{id}/cancel")
    public Result<ExchangeApplication> cancelExchange(
            @Parameter(description = "申请ID") @PathVariable Long id,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        
        ExchangeApplication application = exchangeEngineService.cancelExchange(id, userId);
        
        log.info("用户[{}]取消交换申请，申请ID：{}", userId, id);
        
        return Result.success(application);
    }
    
    /**
     * 获取我发起的交换申请
     */
    @Operation(summary = "获取我发起的交换申请", description = "获取当前用户发起的交换申请列表")
    @GetMapping("/my/applications")
    public Result<Page<ExchangeApplication>> getMyApplications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ExchangeApplication> applications = exchangeEngineService.getUserApplications(userId, pageable);
        
        return Result.success(applications);
    }
    
    /**
     * 获取我收到的交换申请
     */
    @Operation(summary = "获取我收到的交换申请", description = "获取其他用户向我发起的交换申请列表")
    @GetMapping("/my/received")
    public Result<Page<ExchangeApplication>> getReceivedApplications(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ExchangeApplication> applications = exchangeEngineService.getReceivedApplications(userId, pageable);
        
        return Result.success(applications);
    }
    
    /**
     * 获取我的交换历史
     */
    @Operation(summary = "获取我的交换历史", description = "获取当前用户参与的所有交换历史记录")
    @GetMapping("/my/history")
    public Result<Page<ExchangeHistory>> getMyExchangeHistory(
            @Parameter(description = "页码") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        Pageable pageable = PageRequest.of(page, size);
        
        Page<ExchangeHistory> history = exchangeEngineService.getUserExchangeHistory(userId, pageable);
        
        return Result.success(history);
    }
    
    /**
     * 获取用户积分信息
     */
    @Operation(summary = "获取用户积分信息", description = "获取当前用户的积分余额和交易记录")
    @GetMapping("/credits/info")
    public Result<UserCredit> getUserCreditInfo(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        UserCredit userCredit = userCreditService.getUserCredit(userId);
        return Result.success(userCredit);
    }
    
    /**
     * 获取积分排行榜
     */
    @Operation(summary = "获取积分排行榜", description = "获取系统积分排行榜")
    @GetMapping("/credits/ranking")
    public Result<List<UserCredit>> getCreditRanking(
            @Parameter(description = "排行榜数量") @RequestParam(defaultValue = "10") int limit) {
        
        List<UserCredit> ranking = userCreditService.getCreditRanking(limit);
        return Result.success(ranking);
    }
    
    /**
     * 批量处理过期申请（管理员接口）
     */
    @Operation(summary = "批量处理过期申请", description = "系统管理员批量处理过期的交换申请")
    @PostMapping("/admin/process-expired")
    public Result<Integer> processExpiredApplications(Authentication authentication) {
        // TODO: 添加管理员权限验证
        
        int processedCount = exchangeEngineService.processExpiredApplications();
        
        log.info("管理员[{}]批量处理过期申请，处理数量：{}", getCurrentUserId(authentication), processedCount);
        
        return Result.success(processedCount);
    }
    
    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId(Authentication authentication) {
        // TODO: 从认证信息中获取用户ID，这里暂时返回固定值
        return 1L;
    }
    
    /**
     * 交换申请请求DTO
     */
    public static class ExchangeApplicationRequest {
        @NotNull(message = "目标线索ID不能为空")
        @Positive(message = "目标线索ID必须为正数")
        private Long targetLeadId;
        
        @NotEmpty(message = "提供的线索ID列表不能为空")
        private List<@NotNull @Positive Long> offeredLeadIds;
        
        @NotBlank(message = "交换理由不能为空")
        private String reason;
        
        // Getters and Setters
        public Long getTargetLeadId() {
            return targetLeadId;
        }
        
        public void setTargetLeadId(Long targetLeadId) {
            this.targetLeadId = targetLeadId;
        }
        
        public List<Long> getOfferedLeadIds() {
            return offeredLeadIds;
        }
        
        public void setOfferedLeadIds(List<Long> offeredLeadIds) {
            this.offeredLeadIds = offeredLeadIds;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
    
    /**
     * 交换审核请求DTO
     */
    public static class ExchangeReviewRequest {
        @NotBlank(message = "响应消息不能为空")
        private String responseMessage;
        
        // Getters and Setters
        public String getResponseMessage() {
            return responseMessage;
        }
        
        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }
    }
}