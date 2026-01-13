package com.kev1n.spring4demo.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应格式.
 *
 * <p>提供分页数据的统一响应格式，包含数据列表、分页信息等。</p>
 *
 * @param <T> 数据类型
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    
    /** 数据列表。 */
    private List<T> records;

    /** 总记录数。 */
    private Long total;

    /** 当前页码（从0开始）。 */
    private Integer current;

    /** 每页大小。 */
    private Integer size;

    /** 总页数。 */
    private Integer pages;

    /** 是否有下一页。 */
    private Boolean hasNext;

    /** 是否有上一页。 */
    private Boolean hasPrevious;

    /**
     * 创建分页响应.
     *
     * @param <T> 数据类型
     * @param records 数据列表
     * @param total 总记录数
     * @param current 当前页码
     * @param size 每页大小
     * @return 分页响应
     */
    public static <T> PageResponse<T> of(List<T> records, Long total, Integer current, Integer size) {
        // 参数校验
        if (current == null || current < 0) {
            current = 0;
        }
        if (size == null || size < 1) {
            size = 10;
        }
        if (size > 100) {
            size = 100; // 限制最大分页大小
        }
        
        int pages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        return PageResponse.<T>builder()
                .records(records)
                .total(total)
                .current(current)
                .size(size)
                .pages(pages)
                .hasNext(current < pages - 1)
                .hasPrevious(current > 0)
                .build();
    }

    /**
     * 创建分页响应.
     *
     * @param <T> 数据类型
     * @param page mybatis-plus的page
     * @return 分页响应
     */
    public static <T> PageResponse<T> of(Page<T> page) {
        int current = (int) page.getCurrent();
        int size = (int) page.getSize();
        long total = page.getTotal();
        int pages = size > 0 ? (int) Math.ceil((double) total / size) : 0;
        
        return PageResponse.<T>builder()
                .records(page.getRecords())
                .total(total)
                .current(current)
                .size(size)
                .pages(pages)
                .hasNext(current < pages - 1)
                .hasPrevious(current > 0)
                .build();
    }
}