package com.junmo.platform.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户创建请求DTO
 *
 * <p>用于创建新用户的请求参数，包含基本的用户信息验证。
 * 所有字段都经过验证，确保数据的完整性和安全性。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Schema(description = "用户创建请求")
public class UserCreateRequest {

    /**
     * 用户名
     *
     * <p>用户登录的唯一标识，长度3-50字符，只能包含字母、数字、下划线。</p>
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    @Schema(description = "用户名", example = "john_doe", required = true)
    private String username;

    /**
     * 密码
     *
     * <p>用户登录密码，长度8-100字符，必须包含大小写字母、数字和特殊字符。</p>
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 100, message = "密码长度必须在8-100字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "密码必须包含大小写字母、数字和特殊字符")
    @Schema(description = "密码", example = "Password123!", required = true)
    private String password;

    /**
     * 邮箱
     *
     * <p>用户邮箱地址，用于找回密码和通知，可选字段。</p>
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", example = "john@example.com")
    private String email;

    /**
     * 手机号
     *
     * <p>用户手机号码，可选字段，支持中国大陆手机号格式。</p>
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 真实姓名
     *
     * <p>用户的真实姓名，可选字段，长度2-50字符。</p>
     */
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50字符之间")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 用户状态
     *
     * <p>用户账户状态，默认为1（正常）。</p>
     * <ul>
     *   <li>1 - 正常</li>
     *   <li>0 - 禁用</li>
     * </ul>
     */
    @Schema(description = "用户状态：1-正常，0-禁用", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}
