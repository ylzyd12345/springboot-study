-- Junmo Platform 数据库初始化脚本
-- 创建时间: 2025-12-24
-- 更新时间: 2025-12-29
-- 版本: 1.1.0
-- 说明: Docker MySQL 容器启动时自动执行此脚本

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库（注意：Docker 环境变量已创建 spring4demo 数据库，这里确保 zipkin 数据库存在）
CREATE DATABASE IF NOT EXISTS `zipkin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 授予 spring4demo 用户权限
-- 注意：Docker 会自动创建 spring4demo 用户，这里授予必要权限
GRANT ALL PRIVILEGES ON `spring4demo`.* TO 'spring4demo'@'%';
GRANT ALL PRIVILEGES ON `zipkin`.* TO 'spring4demo'@'%';
FLUSH PRIVILEGES;

-- 使用spring4demo数据库
USE `spring4demo`;

-- 用户表（对应 User 实体类）
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '电话',
  `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `status` INT NOT NULL DEFAULT '1' COMMENT '状态（1-启用，0-禁用）',
  `dept_id` BIGINT DEFAULT NULL COMMENT '部门ID',
  `create_by` BIGINT DEFAULT NULL COMMENT '创建人',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` BIGINT DEFAULT NULL COMMENT '更新人',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` INT NOT NULL DEFAULT '0' COMMENT '逻辑删除标志（0-未删除，1-已删除）',
  `version` INT NOT NULL DEFAULT '0' COMMENT '乐观锁版本号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 创建默认管理员用户（密码: admin123，BCrypt加密）
-- 注意：实际ID由雪花算法生成，此处使用固定值用于初始化
INSERT INTO `sys_user` (`id`, `username`, `password`, `email`, `real_name`, `status`, `create_by`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', 'admin@junmo-platform.com', '系统管理员', 1, 1)
ON DUPLICATE KEY UPDATE `update_time` = CURRENT_TIMESTAMP;

-- 使用zipkin数据库
USE `zipkin`;

-- 创建zipkin相关表（简化版本，实际生产环境建议使用官方脚本）
CREATE TABLE IF NOT EXISTS `zipkin_spans` (
  `trace_id_high` bigint(20) NOT NULL DEFAULT '0' COMMENT 'Trace ID高位',
  `trace_id` bigint(20) NOT NULL COMMENT 'Trace ID',
  `id` bigint(20) NOT NULL COMMENT 'Span ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父Span ID',
  `debug` bit(1) NOT NULL DEFAULT b'0' COMMENT '调试标志',
  `name` varchar(255) NOT NULL COMMENT 'Span名称',
  `service_name` varchar(255) NOT NULL COMMENT '服务名称',
  `kind` varchar(10) DEFAULT NULL COMMENT 'Span类型',
  `timestamp` bigint(20) NOT NULL COMMENT '时间戳',
  `duration` bigint(20) DEFAULT NULL COMMENT '持续时间',
  `local_endpoint_service_name` varchar(255) DEFAULT NULL COMMENT '本地端点服务名',
  `local_endpoint_ipv4` varchar(45) DEFAULT NULL COMMENT '本地端点IPv4',
  `local_endpoint_ipv6` varchar(45) DEFAULT NULL COMMENT '本地端点IPv6',
  `local_endpoint_port` int(11) DEFAULT NULL COMMENT '本地端点端口',
  `remote_endpoint_service_name` varchar(255) DEFAULT NULL COMMENT '远程端点服务名',
  `remote_endpoint_ipv4` varchar(45) DEFAULT NULL COMMENT '远程端点IPv4',
  `remote_endpoint_ipv6` varchar(45) DEFAULT NULL COMMENT '远程端点IPv6',
  `remote_endpoint_port` int(11) DEFAULT NULL COMMENT '远程端点端口',
  `annotations` mediumtext COMMENT '注解',
  `tags` mediumtext COMMENT '标签',
  `shared` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否共享',
  PRIMARY KEY (`trace_id_high`,`trace_id`,`id`),
  KEY `idx_trace_id` (`trace_id_high`,`trace_id`),
  KEY `idx_name` (`name`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Zipkin链路追踪表';

-- 授予 zipkin 数据库权限给 spring4demo 用户
GRANT ALL PRIVILEGES ON `zipkin`.* TO 'spring4demo'@'%';
FLUSH PRIVILEGES;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 初始化完成
SELECT 'MySQL 数据库初始化完成!' AS message;