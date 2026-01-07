package com.kev1n.spring4demo.api.dto;

import com.kev1n.spring4demo.api.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户查询DTO
 * 用于接收用户查询请求的数据
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户查询DTO")
public class UserQueryDTO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名（模糊查询）")
    private String username;

    @Schema(description = "邮箱（模糊查询）")
    private String email;

    @Schema(description = "手机号（精确查询）")
    private String phone;

    @Schema(description = "真实姓名（模糊查询）")
    private String realName;

    @Schema(description = "用户状态")
    private UserStatus status;

    @Schema(description = "部门ID")
    private String deptId;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间-开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间-结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方式（ASC/DESC）")
    private String orderDirection;

    @Schema(description = "页码", example = "1")
    private Integer pageNum;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize;
}