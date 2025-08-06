package com.leadexchange.common.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 * 
 * @param <T> 数据类型
 * @author AI Assistant
 * @since 1.0.0
 */
@ApiModel(description = "分页响应结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 数据列表
     */
    @ApiModelProperty(value = "数据列表")
    private List<T> records;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数", example = "100")
    private Long total;

    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码", example = "1")
    private Integer page;

    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer size;

    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数", example = "10")
    private Integer pages;

    /**
     * 是否有上一页
     */
    @ApiModelProperty(value = "是否有上一页", example = "false")
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @ApiModelProperty(value = "是否有下一页", example = "true")
    private Boolean hasNext;

    /**
     * 是否为第一页
     */
    @ApiModelProperty(value = "是否为第一页", example = "true")
    private Boolean isFirst;

    /**
     * 是否为最后一页
     */
    @ApiModelProperty(value = "是否为最后一页", example = "false")
    private Boolean isLast;

    /**
     * 私有构造函数
     */
    private PageResult() {
    }

    /**
     * 构造函数
     * 
     * @param records 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     */
    public PageResult(List<T> records, Long total, Integer page, Integer size) {
        this.records = records;
        this.total = total;
        this.page = page;
        this.size = size;
        this.pages = calculatePages(total, size);
        this.hasPrevious = page > 1;
        this.hasNext = page < pages;
        this.isFirst = page == 1;
        this.isLast = page.equals(pages);
    }

    /**
     * 创建分页结果
     * 
     * @param records 数据列表
     * @param total 总记录数
     * @param page 当前页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Integer page, Integer size) {
        return new PageResult<>(records, total, page, size);
    }
    
    /**
     * 从Spring Data Page对象创建分页结果
     * 
     * @param page Spring Data Page对象
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(org.springframework.data.domain.Page<T> page) {
        return new PageResult<>(
            page.getContent(),
            page.getTotalElements(),
            page.getNumber() + 1, // Spring Data页码从0开始，我们从1开始
            page.getSize()
        );
    }

    /**
     * 创建空分页结果
     * 
     * @param page 当前页码
     * @param size 每页大小
     * @param <T> 数据类型
     * @return 空分页结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer size) {
        return new PageResult<>(List.of(), 0L, page, size);
    }

    /**
     * 创建单页结果
     * 
     * @param records 数据列表
     * @param <T> 数据类型
     * @return 单页结果
     */
    public static <T> PageResult<T> single(List<T> records) {
        return new PageResult<>(records, (long) records.size(), 1, records.size());
    }

    /**
     * 计算总页数
     * 
     * @param total 总记录数
     * @param size 每页大小
     * @return 总页数
     */
    private Integer calculatePages(Long total, Integer size) {
        if (total == null || total == 0 || size == null || size == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / size);
    }

    /**
     * 获取开始记录号
     * 
     * @return 开始记录号
     */
    public Integer getStartRecord() {
        if (total == 0) {
            return 0;
        }
        return (page - 1) * size + 1;
    }

    /**
     * 获取结束记录号
     * 
     * @return 结束记录号
     */
    public Integer getEndRecord() {
        if (total == 0) {
            return 0;
        }
        int endRecord = page * size;
        return Math.min(endRecord, total.intValue());
    }

    /**
     * 是否为空结果
     * 
     * @return 是否为空
     */
    public boolean isEmpty() {
        return records == null || records.isEmpty();
    }

    /**
     * 是否有数据
     * 
     * @return 是否有数据
     */
    public boolean hasContent() {
        return !isEmpty();
    }

    /**
     * 获取记录数量
     * 
     * @return 记录数量
     */
    public int getNumberOfElements() {
        return records == null ? 0 : records.size();
    }

    // Getter and Setter methods
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
        this.pages = calculatePages(total, size);
        updatePageFlags();
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
        updatePageFlags();
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
        this.pages = calculatePages(total, size);
        updatePageFlags();
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Boolean getHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(Boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }

    public Boolean getHasNext() {
        return hasNext;
    }

    public void setHasNext(Boolean hasNext) {
        this.hasNext = hasNext;
    }

    public Boolean getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(Boolean isFirst) {
        this.isFirst = isFirst;
    }

    public Boolean getIsLast() {
        return isLast;
    }

    public void setIsLast(Boolean isLast) {
        this.isLast = isLast;
    }

    /**
     * 更新分页标志
     */
    private void updatePageFlags() {
        if (page != null && pages != null) {
            this.hasPrevious = page > 1;
            this.hasNext = page < pages;
            this.isFirst = page == 1;
            this.isLast = page.equals(pages);
        }
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "records=" + (records != null ? records.size() + " items" : "null") +
                ", total=" + total +
                ", page=" + page +
                ", size=" + size +
                ", pages=" + pages +
                ", hasPrevious=" + hasPrevious +
                ", hasNext=" + hasNext +
                ", isFirst=" + isFirst +
                ", isLast=" + isLast +
                '}';
    }
}