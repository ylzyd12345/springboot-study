package com.kev1n.spring4demo.api.dto;

import com.kev1n.spring4demo.api.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户视图对象.
 * 用于返回给前端的数据展示.
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户视图对象")
public class UserVO {

    /** 用户ID. */
    @Schema(description = "用户ID")
    private Long id;

    /** 用户名. */
    @Schema(description = "用户名")
    private String username;

    /** 邮箱. */
    @Schema(description = "邮箱")
    private String email;

    /** 手机号. */
    @Schema(description = "手机号")
    private String phone;

    /** 真实姓名. */
    @Schema(description = "真实姓名")
    private String realName;

    /** 头像URL. */
    @Schema(description = "头像URL")
    private String avatar;

    /** 用户状态. */
    @Schema(description = "用户状态")
    private UserStatus status;

    /** 部门ID. */
    @Schema(description = "部门ID")
    private String deptId;

    /** 创建人. */
    @Schema(description = "创建人")
    private String createBy;

    /** 创建时间. */
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 更新人. */
    @Schema(description = "更新人")
    private String updateBy;

    /** 更新时间. */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}