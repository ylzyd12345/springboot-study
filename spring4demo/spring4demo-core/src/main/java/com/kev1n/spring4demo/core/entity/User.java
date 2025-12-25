package com.kev1n.spring4demo.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User {
    
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
    
    @TableField("real_name")
    private String realName;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("status")
    private Integer status;
    
    @TableField("dept_id")
    private String deptId;
    
    @TableField("create_by")
    private String createBy;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField("update_by")
    private String updateBy;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableField("deleted")
    @TableLogic
    private Integer deleted;
    
    @TableField("version")
    @Version
    private Integer version;
}