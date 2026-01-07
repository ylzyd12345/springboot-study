package com.kev1n.spring4demo.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户账户实体
 *
 * 用于存储用户账户余额信息，支持分布式事务
 *
 * @author spring4demo
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_account")
public class UserAccount {

    /**
     * 账户ID（使用雪花算法生成）
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    @TableField("frozen_amount")
    private BigDecimal frozenAmount;

    /**
     * 可用余额（余额 - 冻结金额）
     */
    @TableField("available_balance")
    private BigDecimal availableBalance;

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