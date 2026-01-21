package com.junmo.platform.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 用户日志文档
 *
 * 用于记录用户操作日志，包括登录、创建、更新、删除等操作
 *
 * @author junmo-platform
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_logs")
@CompoundIndex(name = "user_id_created_at_idx", def = "{'userId': 1, 'createdAt': -1}")
public class UserLog {

    /**
     * 日志ID（MongoDB自动生成）
     */
    @Id
    private String id;

    /**
     * 用户ID（索引字段）
     */
    @Indexed
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 操作类型（LOGIN, CREATE, UPDATE, DELETE等）
     */
    private String action;

    /**
     * 操作详情（JSON格式）
     */
    private String details;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 用户代理（浏览器信息）
     */
    private String userAgent;

    /**
     * 创建时间（索引字段）
     */
    @Indexed
    private LocalDateTime createdAt;

    /**
     * 审计字段：创建时间（自动填充）
     */
    @CreatedDate
    private LocalDateTime createdDate;

    /**
     * 审计字段：最后修改时间（自动填充）
     */
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}