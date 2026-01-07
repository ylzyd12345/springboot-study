package com.kev1n.spring4demo.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 
 * 主键策略：使用雪花算法（ASSIGN_ID）生成分布式唯一ID
 * 
 * @author spring4demo
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
    
    private Integer status;
    
    @TableField("dept_id")
    private String deptId;
    
    private String createBy;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    private String updateBy;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
    @Version
    private Integer version;
}
