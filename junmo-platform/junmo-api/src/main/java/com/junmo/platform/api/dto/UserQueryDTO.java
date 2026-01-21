package com.junmo.platform.api.dto;

import com.junmo.platform.api.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户查询DTO.
 * 用于接收用户查询请求的数据.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户查询DTO")
public class UserQueryDTO {

    /** 用户ID. */
    @Schema(description = "用户ID")
    private Long id;

    /** 用户名（模糊查询）. */
    @Schema(description = "用户名（模糊查询）")
    private String username;

    /** 邮箱（模糊查询）. */
    @Schema(description = "邮箱（模糊查询）")
    private String email;

    /** 手机号（精确查询）. */
    @Schema(description = "手机号（精确查询）")
    private String phone;

    /** 真实姓名（模糊查询）. */
    @Schema(description = "真实姓名（模糊查询）")
    private String realName;

    /** 用户状态. */
    @Schema(description = "用户状态")
    private UserStatus status;

    /** 部门ID. */
    @Schema(description = "部门ID")
    private String deptId;

    /** 创建人. */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间-开始. */
    @Schema(description = "创建时间-开始")
    private LocalDateTime createTimeStart;

    /** 创建时间-结束. */
    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;

    /** 排序字段. */
    @Schema(description = "排序字段")
    private String orderBy;

    /** 排序方式（ASC/DESC）. */
    @Schema(description = "排序方式（ASC/DESC）")
    private String orderDirection;

    /** 页码. */
    @Schema(description = "页码", example = "1")
    private Integer pageNum;

    /** 每页大小. */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;
}