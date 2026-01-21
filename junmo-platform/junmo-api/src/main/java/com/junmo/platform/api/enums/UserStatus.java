package com.junmo.platform.api.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举.
 *
 * 数据库存储：INT 类型（1-启用，0-禁用）
 * 代码使用：ENUM 类型
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
@Schema(description = "用户状态枚举")
public enum UserStatus {

    /**
     * 启用状态.
     */
    ACTIVE(1, "启用"),

    /**
     * 禁用状态.
     */
    INACTIVE(0, "禁用");

    /**
     * 状态值.
     */
    @Schema(description = "状态值")
    private final Integer value;

    /**
     * 状态描述.
     */
    @Schema(description = "状态描述")
    private final String description;

    /**
     * 根据值获取枚举.
     *
     * @param value 状态值
     * @return 用户状态枚举
     */
    public static UserStatus fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserStatus status : UserStatus.values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的用户状态值: " + value);
    }

    /**
     * 根据描述获取枚举.
     *
     * @param description 状态描述
     * @return 用户状态枚举
     */
    public static UserStatus fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (UserStatus status : UserStatus.values()) {
            if (status.getDescription().equals(description)) {
                return status;
            }
        }
        throw new IllegalArgumentException("无效的用户状态描述: " + description);
    }
}