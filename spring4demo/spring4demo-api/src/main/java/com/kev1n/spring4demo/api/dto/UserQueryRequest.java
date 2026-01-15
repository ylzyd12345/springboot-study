package com.kev1n.spring4demo.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 用户查询请求DTO
 * 
 * <p>用于用户列表查询的请求参数，支持分页、排序和条件筛选。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Schema(description = "用户查询请求")
public class UserQueryRequest {

    /**
     * 当前页码
     * 
     * <p>分页查询的当前页码，从1开始。</p>
     */
    @Min(value = 1, message = "当前页码必须大于等于1")
    @Schema(description = "当前页码，从1开始", example = "1", defaultValue = "1")
    private Integer current = 1;

    /**
     * 每页大小
     * 
     * <p>每页显示的记录数，默认10，最大100。</p>
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    @Max(value = 100, message = "每页大小不能超过100")
    @Schema(description = "每页大小，最大100", example = "10", defaultValue = "10")
    private Integer size = 10;

    /**
     * 关键字搜索
     * 
     * <p>搜索关键字，会在用户名、邮箱、真实姓名中搜索。</p>
     */
    @Schema(description = "搜索关键字", example = "john")
    private String keyword;

    /**
     * 用户状态筛选
     * 
     * <p>按用户状态筛选，1-正常，0-禁用。</p>
     */
    @Schema(description = "用户状态筛选：1-正常，0-禁用", example = "1", allowableValues = {"0", "1"})
    private Integer status;

    /**
     * 排序字段
     * 
     * <p>排序字段名，默认按创建时间排序。</p>
     */
    @Schema(description = "排序字段", example = "create_time", defaultValue = "create_time")
    private String sortBy = "create_time";

    /**
     * 是否升序
     * 
     * <p>排序方向，true-升序，false-降序，默认降序。</p>
     */
    @Schema(description = "是否升序：true-升序，false-降序", example = "false", defaultValue = "false")
    private Boolean asc = false;
}