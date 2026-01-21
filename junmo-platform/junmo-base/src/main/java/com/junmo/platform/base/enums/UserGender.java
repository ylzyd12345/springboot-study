package com.junmo.platform.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户性别枚举.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Getter
@AllArgsConstructor
public enum UserGender {

    /**
     * 男性.
     */
    MALE(1, "男"),

    /**
     * 女性.
     */
    FEMALE(2, "女"),

    /**
     * 未知.
     */
    UNKNOWN(0, "未知");

    /**
     * 数据库存储值.
     */
    private final Integer value;

    /**
     * 性别描述.
     */
    private final String description;

    /**
     * 根据值获取枚举.
     *
     * @param value 性别值
     * @return 用户性别枚举
     */
    public static UserGender fromValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (UserGender gender : UserGender.values()) {
            if (gender.getValue().equals(value)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("无效的用户性别值: " + value);
    }

    /**
     * 根据描述获取枚举.
     *
     * @param description 性别描述
     * @return 用户性别枚举
     */
    public static UserGender fromDescription(String description) {
        if (description == null) {
            return null;
        }
        for (UserGender gender : UserGender.values()) {
            if (gender.getDescription().equals(description)) {
                return gender;
            }
        }
        throw new IllegalArgumentException("无效的用户性别描述: " + description);
    }
}