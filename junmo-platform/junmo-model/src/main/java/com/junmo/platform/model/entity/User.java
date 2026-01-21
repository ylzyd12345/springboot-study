package com.junmo.platform.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 *
 * 主键策略：使用雪花算法（ASSIGN_ID）生成分布式唯一ID
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String username;

    private String password;

    private String email;

    private String phone;

    @TableField("real_name")
    private String realName;

    private String avatar;

    /**
     * 用户状态（数据库存储INT，代码使用ENUM）
     * 1-启用，0-禁用
     */
    @TableField("status")
    private Integer status;

    @TableField("dept_id")
    private String deptId;

    private String createBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    private String updateBy;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableLogic
    private Integer deleted;

    @Version
    private Integer version;
}