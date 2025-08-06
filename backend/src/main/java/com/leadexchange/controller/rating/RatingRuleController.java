package com.leadexchange.controller.rating;

import com.leadexchange.common.result.Result;
import com.leadexchange.common.result.PageResult;
import com.leadexchange.common.result.ResultCode;
import com.leadexchange.common.exception.BusinessException;
import com.leadexchange.domain.rating.RatingRule;
import com.leadexchange.domain.rating.RatingRuleType;
import com.leadexchange.domain.rating.CalculationMethod;
import com.leadexchange.service.rating.RatingRuleService;
import com.leadexchange.service.rating.RatingRuleService.RuleValidationResult;
import com.leadexchange.service.rating.RatingRuleService.RuleImportResult;
import com.leadexchange.service.rating.RatingRuleService.RuleStatistics;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 评级规则管理控制器
 * 提供评级规则配置相关的REST API接口
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/rating/rules")
@RequiredArgsConstructor
@Validated
@Api(tags = "评级规则管理")
public class RatingRuleController {

    private static final Logger log = LoggerFactory.getLogger(RatingRuleController.class);
    private final RatingRuleService ratingRuleService;

    /**
     * 创建评级规则
     * 
     * @param ratingRule 评级规则信息
     * @return 创建的规则
     */
    @PostMapping
    @ApiOperation("创建评级规则")
    @PreAuthorize("hasAuthority('rating:rule:create')")
    public Result<RatingRule> createRule(@ApiParam("评级规则信息") @RequestBody @Valid RatingRule ratingRule) {
        log.info("开始创建评级规则，规则名称: {}", ratingRule.getRuleName());
        RatingRule createdRule = ratingRuleService.createRule(ratingRule);
        log.info("评级规则创建成功，规则ID: {}", createdRule.getId());
        return Result.success(createdRule);
    }

    /**
     * 更新评级规则
     * 
     * @param id 规则ID
     * @param ratingRule 更新的规则信息
     * @return 更新后的规则
     */
    @PutMapping("/{id}")
    @ApiOperation("更新评级规则")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<RatingRule> updateRule(
            @ApiParam("规则ID") @PathVariable @NotNull Long id,
            @ApiParam("更新的规则信息") @RequestBody @Valid RatingRule ratingRule) {
        log.info("开始更新评级规则，规则ID: {}", id);
        RatingRule updatedRule = ratingRuleService.updateRule(id, ratingRule);
        log.info("评级规则更新成功，规则ID: {}", id);
        return Result.success(updatedRule);
    }

    /**
     * 删除评级规则
     * 
     * @param id 规则ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除评级规则")
    @PreAuthorize("hasAuthority('rating:rule:delete')")
    public Result<String> deleteRule(@ApiParam("规则ID") @PathVariable @NotNull Long id) {
        log.info("开始删除评级规则，规则ID: {}", id);
        ratingRuleService.deleteRule(id);
        log.info("评级规则删除成功，规则ID: {}", id);
        return Result.success("删除成功");
    }

    /**
     * 根据ID获取评级规则
     * 
     * @param id 规则ID
     * @return 规则信息
     */
    @GetMapping("/{id}")
    @ApiOperation("根据ID获取评级规则")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<RatingRule> getRuleById(@ApiParam("规则ID") @PathVariable @NotNull Long id) {
        log.debug("获取评级规则，规则ID: {}", id);
        Optional<RatingRule> ruleOpt = ratingRuleService.getRuleById(id);
        if (!ruleOpt.isPresent()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "评级规则不存在");
        }
        return Result.success(ruleOpt.get());
    }

    /**
     * 获取所有评级规则
     * 
     * @param pageable 分页参数
     * @return 规则列表
     */
    @GetMapping
    @ApiOperation("获取所有评级规则")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<PageResult<RatingRule>> getAllRules(Pageable pageable) {
        log.debug("获取所有评级规则，分页参数: {}", pageable);
        PageResult<RatingRule> rules = ratingRuleService.getAllRules(pageable);
        return Result.success(rules);
    }

    /**
     * 获取启用的评级规则
     * 
     * @return 启用的规则列表
     */
    @GetMapping("/enabled")
    @ApiOperation("获取启用的评级规则")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<List<RatingRule>> getEnabledRules() {
        log.debug("获取启用的评级规则");
        List<RatingRule> rules = ratingRuleService.getEnabledRules();
        return Result.success(rules);
    }

    /**
     * 根据类型获取评级规则
     * 
     * @param ruleType 规则类型
     * @return 规则列表
     */
    @GetMapping("/type/{ruleType}")
    @ApiOperation("根据类型获取评级规则")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<List<RatingRule>> getRulesByType(
            @ApiParam("规则类型") @PathVariable @NotNull RatingRuleType ruleType) {
        log.debug("根据类型获取评级规则，类型: {}", ruleType);
        List<RatingRule> rules = ratingRuleService.getRulesByType(ruleType);
        return Result.success(rules);
    }

    /**
     * 根据计算方法获取评级规则
     * 
     * @param calculationMethod 计算方法
     * @return 规则列表
     */
    @GetMapping("/method/{calculationMethod}")
    @ApiOperation("根据计算方法获取评级规则")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<List<RatingRule>> getRulesByCalculationMethod(
            @ApiParam("计算方法") @PathVariable @NotNull CalculationMethod calculationMethod) {
        log.debug("根据计算方法获取评级规则，方法: {}", calculationMethod);
        List<RatingRule> rules = ratingRuleService.getRulesByCalculationMethod(calculationMethod);
        return Result.success(rules);
    }

    /**
     * 启用评级规则
     * 
     * @param id 规则ID
     * @return 操作结果
     */
    @PostMapping("/{id}/enable")
    @ApiOperation("启用评级规则")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<String> enableRule(@ApiParam("规则ID") @PathVariable @NotNull Long id) {
        log.info("启用评级规则，规则ID: {}", id);
        ratingRuleService.enableRule(id);
        log.info("评级规则启用成功，规则ID: {}", id);
        return Result.success("启用成功");
    }

    /**
     * 禁用评级规则
     * 
     * @param id 规则ID
     * @return 操作结果
     */
    @PostMapping("/{id}/disable")
    @ApiOperation("禁用评级规则")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<String> disableRule(@ApiParam("规则ID") @PathVariable @NotNull Long id) {
        log.info("禁用评级规则，规则ID: {}", id);
        ratingRuleService.disableRule(id);
        log.info("评级规则禁用成功，规则ID: {}", id);
        return Result.success("禁用成功");
    }

    /**
     * 批量启用评级规则
     * 
     * @param ids 规则ID列表
     * @return 操作结果
     */
    @PostMapping("/batch/enable")
    @ApiOperation("批量启用评级规则")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<String> enableRulesBatch(@ApiParam("规则ID列表") @RequestBody @NotNull List<Long> ids) {
        log.info("批量启用评级规则，规则数量: {}", ids.size());
        ratingRuleService.batchEnableRules(ids);
        log.info("评级规则批量启用成功");
        return Result.success("批量启用成功");
    }

    /**
     * 批量禁用评级规则
     * 
     * @param ids 规则ID列表
     * @return 操作结果
     */
    @PostMapping("/batch/disable")
    @ApiOperation("批量禁用评级规则")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<String> disableRulesBatch(@ApiParam("规则ID列表") @RequestBody @NotNull List<Long> ids) {
        log.info("批量禁用评级规则，规则数量: {}", ids.size());
        ratingRuleService.batchDisableRules(ids);
        log.info("评级规则批量禁用成功");
        return Result.success("批量禁用成功");
    }

    /**
     * 批量删除评级规则
     * 
     * @param ids 规则ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @ApiOperation("批量删除评级规则")
    @PreAuthorize("hasAuthority('rating:rule:delete')")
    public Result<String> deleteRulesBatch(@ApiParam("规则ID列表") @RequestBody @NotNull List<Long> ids) {
        log.info("批量删除评级规则，规则数量: {}", ids.size());
        // 批量删除方法需要在Service接口中添加
        for (Long id : ids) {
            ratingRuleService.deleteRule(id);
        }
        log.info("评级规则批量删除成功");
        return Result.success("批量删除成功");
    }

    /**
     * 调整规则排序
     * 
     * @param sortOrders 排序配置
     * @return 操作结果
     */
    @PostMapping("/sort")
    @ApiOperation("调整规则排序")
    @PreAuthorize("hasAuthority('rating:rule:update')")
    public Result<String> adjustRuleSort(
            @ApiParam("排序配置") @RequestBody @NotNull List<RatingRuleService.RuleSortOrder> sortOrders) {
        log.info("调整评级规则排序，规则数量: {}", sortOrders.size());
        ratingRuleService.batchUpdateRuleSortOrder(sortOrders);
        log.info("评级规则排序调整成功");
        return Result.success("排序调整成功");
    }

    /**
     * 验证评级规则
     * 
     * @param ratingRule 待验证的规则
     * @return 验证结果
     */
    @PostMapping("/validate")
    @ApiOperation("验证评级规则")
    @PreAuthorize("hasAuthority('rating:rule:validate')")
    public Result<RuleValidationResult> validateRule(
            @ApiParam("待验证的规则") @RequestBody @Valid RatingRule ratingRule) {
        log.debug("验证评级规则，规则名称: {}", ratingRule.getRuleName());
        RuleValidationResult result = ratingRuleService.validateRule(ratingRule);
        return Result.success(result);
    }

    /**
     * 导入评级规则
     * 
     * @param file 导入文件
     * @param overwrite 是否覆盖现有规则
     * @return 导入结果
     */
    @PostMapping("/import")
    @ApiOperation("导入评级规则")
    @PreAuthorize("hasAuthority('rating:rule:import')")
    public Result<RuleImportResult> importRules(
            @ApiParam("导入文件") @RequestParam("file") MultipartFile file,
            @ApiParam("是否覆盖现有规则") @RequestParam(defaultValue = "false") boolean overwrite) {
        log.info("导入评级规则，文件名: {}, 覆盖模式: {}", file.getOriginalFilename(), overwrite);
        // 需要先解析文件内容为规则列表
        List<RatingRule> rules = parseRulesFromFile(file);
        RuleImportResult result = ratingRuleService.importRules(rules);
        log.info("评级规则导入完成，成功: {}, 失败: {}", result.getSuccessCount(), result.getFailureCount());
        return Result.success(result);
    }

    /**
     * 导出评级规则
     * 
     * @param format 导出格式（EXCEL/JSON/CSV）
     * @return 导出数据
     */
    @GetMapping("/export")
    @ApiOperation("导出评级规则")
    @PreAuthorize("hasAuthority('rating:rule:export')")
    public Result<Map<String, Object>> exportRules(
            @ApiParam("导出格式") @RequestParam(defaultValue = "EXCEL") String format) {
        log.info("导出评级规则，格式: {}", format);
        List<RatingRule> rules = ratingRuleService.exportRules();
        Map<String, Object> result = formatExportData(rules, format);
        log.info("评级规则导出完成");
        return Result.success(result);
    }

    /**
     * 重置为默认规则
     * 
     * @return 操作结果
     */
    @PostMapping("/reset-default")
    @ApiOperation("重置为默认规则")
    @PreAuthorize("hasAuthority('rating:rule:reset')")
    public Result<String> resetToDefaultRules() {
        log.info("重置为默认评级规则");
        ratingRuleService.resetToDefaultRules();
        log.info("默认评级规则重置完成");
        return Result.success("重置成功");
    }

    /**
     * 获取规则模板
     * 
     * @param ruleType 规则类型
     * @return 规则模板
     */
    @GetMapping("/template/{ruleType}")
    @ApiOperation("获取规则模板")
    @PreAuthorize("hasAuthority('rating:rule:view')")
    public Result<RatingRule> getRuleTemplate(
            @ApiParam("规则类型") @PathVariable @NotNull RatingRuleType ruleType) {
        log.debug("获取规则模板，类型: {}", ruleType);
        String templateConfig = ratingRuleService.getRuleConfigTemplate(ruleType);
        RatingRule template = parseTemplateConfig(templateConfig, ruleType);
        return Result.success(template);
    }

    /**
     * 获取规则统计信息
     * 
     * @return 统计信息
     */
    @GetMapping("/statistics")
    @ApiOperation("获取规则统计信息")
    @PreAuthorize("hasAuthority('rating:rule:statistics')")
    public Result<RuleStatistics> getRuleStatistics() {
        log.debug("获取评级规则统计信息");
        RuleStatistics statistics = ratingRuleService.getRuleStatistics();
        return Result.success(statistics);
    }

    /**
     * 复制评级规则
     * 
     * @param id 源规则ID
     * @param newRuleName 新规则名称
     * @return 复制的规则
     */
    @PostMapping("/{id}/copy")
    @ApiOperation("复制评级规则")
    @PreAuthorize("hasAuthority('rating:rule:create')")
    public Result<RatingRule> copyRule(
            @ApiParam("源规则ID") @PathVariable @NotNull Long id,
            @ApiParam("新规则名称") @RequestParam @NotNull String newRuleName) {
        log.info("复制评级规则，源规则ID: {}, 新规则名称: {}", id, newRuleName);
        RatingRule copiedRule = ratingRuleService.copyRule(id, newRuleName);
        log.info("评级规则复制成功，新规则ID: {}", copiedRule.getId());
        return Result.success(copiedRule);
    }

    /**
     * 从文件解析规则列表
     * 
     * @param file 上传的文件
     * @return 规则列表
     */
    private List<RatingRule> parseRulesFromFile(MultipartFile file) {
        // TODO: 实现文件解析逻辑
        return new ArrayList<>();
    }

    /**
     * 格式化导出数据
     * 
     * @param rules 规则列表
     * @param format 导出格式
     * @return 格式化后的数据
     */
    private Map<String, Object> formatExportData(List<RatingRule> rules, String format) {
        Map<String, Object> result = new HashMap<>();
        result.put("rules", rules);
        result.put("format", format);
        result.put("count", rules.size());
        return result;
    }

    /**
     * 解析模板配置
     * 
     * @param templateConfig 模板配置字符串
     * @param ruleType 规则类型
     * @return 规则模板
     */
    private RatingRule parseTemplateConfig(String templateConfig, RatingRuleType ruleType) {
        // TODO: 实现模板配置解析逻辑
        RatingRule template = new RatingRule();
        template.setRuleType(ruleType);
        template.setRuleName("模板规则");
        template.setIsEnabled(true);
        return template;
    }
}