package com.kev1n.spring4demo.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户更新请求DTO
 * 
 * <p>用于更新用户信息的请求参数，所有字段都是可选的。
 * 只更新提供的字段，未提供的字段保持原值不变。</p>
 * 
 * @author spring4demo
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Schema(description = "用户更新请求")
public class UserUpdateRequest {

    /**
     * 邮箱
     * 
     * <p>用户邮箱地址，可选字段。如果提供，会验证唯一性。</p>
     */
    @Email(message = "邮箱格式不正确")
    @Schema(description = "邮箱地址", example = "john@example.com")
    private String email;

    /**
     * 手机号
     * 
     * <p>用户手机号码，可选字段。</p>
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    /**
     * 真实姓名
     * 
     * <p>用户的真实姓名，可选字段。</p>
     */
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50字符之间")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    /**
     * 用户状态
     * 
     * <p>用户账户状态，可选字段。</p>
     * <ul>
     *   <li>1 - 正常</li>
     *   <li>0 - 禁用</li>
     * </ul>
     */
    @Schema(description = "用户状态：1-正常，0-禁用", example = "1", allowableValues = {"0", "1"})
    private Integer status;
}