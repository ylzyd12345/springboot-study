package com.junmo.platform.model.entity.neo4j;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.time.LocalDateTime;

/**
 * 用户节点实体
 *
 * <p>对应关系型数据库中的 sys_user 表，用于构建用户关系图。</p>
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Node("User")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNode {

    /**
     * 用户ID（对应 sys_user.id）
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 用户名（对应 sys_user.username）
     */
    @Property("username")
    private String username;

    /**
     * 邮箱（对应 sys_user.email）
     */
    @Property("email")
    private String email;

    /**
     * 手机号（对应 sys_user.phone）
     */
    @Property("phone")
    private String phone;

    /**
     * 真实姓名（对应 sys_user.real_name）
     */
    @Property("realName")
    private String realName;

    /**
     * 状态（对应 sys_user.status）
     */
    @Property("status")
    private Integer status;

    /**
     * 创建时间（对应 sys_user.created_at）
     */
    @Property("createdAt")
    private LocalDateTime createdAt;

    /**
     * 更新时间（对应 sys_user.updated_at）
     */
    @Property("updatedAt")
    private LocalDateTime updatedAt;

    /**
     * 最后登录时间（对应 sys_user.last_login_time）
     */
    @Property("lastLoginTime")
    private LocalDateTime lastLoginTime;

    /**
     * 登录次数（对应 sys_user.login_count）
     */
    @Property("loginCount")
    private Integer loginCount;

    // ==================== TODO: 待添加的关系属性 ====================
    // TODO: 当实现用户关系功能时，添加以下关系：
    // - 关注关系 (FOLLOWS): UserNode -> UserNode
    // - 好友关系 (FRIEND): UserNode -> UserNode
    // - 同事关系 (COLLEAGUE): UserNode -> UserNode
    // - 粉丝关系 (FAN): UserNode -> UserNode
}