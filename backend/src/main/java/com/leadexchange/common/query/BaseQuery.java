package com.leadexchange.common.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分页查询基类
 * 
 * @author AI Assistant
 * @since 1.0.0
 */
@ApiModel(description = "分页查询基类")
public class BaseQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于0")
    @ApiModelProperty(value = "页码", example = "1", notes = "从1开始")
    private Integer page = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @ApiModelProperty(value = "每页大小", example = "10", notes = "最大100")
    private Integer size = 10;

    /**
     * 排序字段
     */
    @ApiModelProperty(value = "排序字段", example = "createTime", notes = "支持多字段排序，用逗号分隔")
    private String sortBy;

    /**
     * 排序方向（ASC/DESC）
     */
    @ApiModelProperty(value = "排序方向", example = "DESC", notes = "ASC：升序，DESC：降序")
    private String sortDir = "DESC";

    /**
     * 关键词搜索
     */
    @ApiModelProperty(value = "关键词搜索", example = "招商")
    private String keyword;

    /**
     * 创建时间开始
     */
    @ApiModelProperty(value = "创建时间开始", example = "2024-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    /**
     * 创建时间结束
     */
    @ApiModelProperty(value = "创建时间结束", example = "2024-12-31 23:59:59")
    private LocalDateTime createTimeEnd;

    /**
     * 更新时间开始
     */
    @ApiModelProperty(value = "更新时间开始", example = "2024-01-01 00:00:00")
    private LocalDateTime updateTimeStart;

    /**
     * 更新时间结束
     */
    @ApiModelProperty(value = "更新时间结束", example = "2024-12-31 23:59:59")
    private LocalDateTime updateTimeEnd;

    /**
     * 创建人ID列表
     */
    @ApiModelProperty(value = "创建人ID列表", example = "[1, 2, 3]")
    private List<Long> createByList;

    /**
     * 状态列表
     */
    @ApiModelProperty(value = "状态列表", example = "[1, 2]")
    private List<Integer> statusList;

    /**
     * 是否包含已删除数据
     */
    @ApiModelProperty(value = "是否包含已删除数据", example = "false")
    private Boolean includeDeleted = false;

    /**
     * 获取偏移量
     * 
     * @return 偏移量
     */
    public Integer getOffset() {
        return (page - 1) * size;
    }

    /**
     * 获取排序字段数组
     * 
     * @return 排序字段数组
     */
    public String[] getSortFields() {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            return new String[]{"createTime"};
        }
        return sortBy.split(",");
    }

    /**
     * 是否升序排序
     * 
     * @return 是否升序
     */
    public boolean isAscending() {
        return "ASC".equalsIgnoreCase(sortDir);
    }

    /**
     * 是否降序排序
     * 
     * @return 是否降序
     */
    public boolean isDescending() {
        return "DESC".equalsIgnoreCase(sortDir);
    }

    /**
     * 验证分页参数
     */
    public void validate() {
        if (page == null || page < 1) {
            page = 1;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100;
        }
        if (sortDir == null || (!"ASC".equalsIgnoreCase(sortDir) && !"DESC".equalsIgnoreCase(sortDir))) {
            sortDir = "DESC";
        }
    }

    // Getter and Setter methods
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDateTime getCreateTimeStart() {
        return createTimeStart;
    }

    public void setCreateTimeStart(LocalDateTime createTimeStart) {
        this.createTimeStart = createTimeStart;
    }

    public LocalDateTime getCreateTimeEnd() {
        return createTimeEnd;
    }

    public void setCreateTimeEnd(LocalDateTime createTimeEnd) {
        this.createTimeEnd = createTimeEnd;
    }

    public LocalDateTime getUpdateTimeStart() {
        return updateTimeStart;
    }

    public void setUpdateTimeStart(LocalDateTime updateTimeStart) {
        this.updateTimeStart = updateTimeStart;
    }

    public LocalDateTime getUpdateTimeEnd() {
        return updateTimeEnd;
    }

    public void setUpdateTimeEnd(LocalDateTime updateTimeEnd) {
        this.updateTimeEnd = updateTimeEnd;
    }

    public List<Long> getCreateByList() {
        return createByList;
    }

    public void setCreateByList(List<Long> createByList) {
        this.createByList = createByList;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    @Override
    public String toString() {
        return "BaseQuery{" +
                "page=" + page +
                ", size=" + size +
                ", sortBy='" + sortBy + '\'' +
                ", sortDir='" + sortDir + '\'' +
                ", keyword='" + keyword + '\'' +
                ", createTimeStart=" + createTimeStart +
                ", createTimeEnd=" + createTimeEnd +
                ", updateTimeStart=" + updateTimeStart +
                ", updateTimeEnd=" + updateTimeEnd +
                ", createByList=" + createByList +
                ", statusList=" + statusList +
                ", includeDeleted=" + includeDeleted +
                '}';
    }
}