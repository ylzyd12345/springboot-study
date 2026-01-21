package com.junmo.platform.api.dto;

import com.junmo.platform.api.enums.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户更新DTO.
 * 用于接收更新用户请求的数据.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户更新DTO")
public class UserUpdateDTO {

    /** 用户ID. */
    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /** 用户名. */
    @Schema(description = "用户名")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /** 邮箱. */
    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    /** 手机号. */
    @Schema(description = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /** 真实姓名. */
    @Schema(description = "真实姓名")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    /** 头像URL. */
    @Schema(description = "头像URL")
    @Size(max = 500, message = "头像URL长度不能超过500个字符")
    private String avatar;

    /** 用户状态. */
    @Schema(description = "用户状态")
    private UserStatus status;

    /** 部门ID. */
    @Schema(description = "部门ID")
    @Size(max = 50, message = "部门ID长度不能超过50个字符")
    private String deptId;
}
