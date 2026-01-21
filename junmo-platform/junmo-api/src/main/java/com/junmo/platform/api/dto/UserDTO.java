package com.junmo.platform.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 用户 DTO.
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
public class UserDTO {
    /**
     * 用户ID.
     */
    private String id;

    /**
     * 用户名.
     */
    private String username;

    /**
     * 邮箱.
     */
    private String email;

    /**
     * 真实姓名.
     */
    private String realName;

    /**
     * 头像.
     */
    private String avatar;

    /**
     * 部门ID.
     */
    private String deptId;

    /**
     * 状态.
     */
    private Integer status;
}