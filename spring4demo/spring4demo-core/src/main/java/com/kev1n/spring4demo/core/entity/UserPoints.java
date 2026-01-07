package com.kev1n.spring4demo.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户积分实体
 *
 * 用于存储用户积分信息，支持分布式事务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_points")
public class UserPoints {

    /**
     * 积分ID（使用雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 当前积分
     */
    private Integer points;

    /**
     * 累计获得积分
     */
    @TableField("total_earned")
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    @TableField("total_consumed")
    private Integer totalConsumed;

    /**
     * 积分等级
     */
    private Integer level;

    /**
     * 账户状态：0-正常 1-冻结 2-注销
     */
    private Integer status;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记
     */
    @TableLogic
    private Integer deleted;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;
}