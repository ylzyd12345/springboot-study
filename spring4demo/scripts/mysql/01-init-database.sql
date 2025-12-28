-- Spring4demo 数据库初始化脚本
-- 创建时间: 2025-12-24
-- 版本: 1.0.0

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `spring4demo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `zipkin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用spring4demo数据库
USE `spring4demo`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `email` varchar(100) NOT NULL COMMENT '邮箱',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `first_name` varchar(50) DEFAULT NULL COMMENT '名',
  `last_name` varchar(50) DEFAULT NULL COMMENT '姓',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` enum('ACTIVE','INACTIVE','LOCKED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `description` varchar(255) DEFAULT NULL COMMENT '角色描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_roles` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `fk_user_roles_role_id` (`role_id`),
  CONSTRAINT `fk_user_roles_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_roles_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 权限表
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `resource` varchar(100) NOT NULL COMMENT '资源',
  `action` varchar(50) NOT NULL COMMENT '操作',
  `description` varchar(255) DEFAULT NULL COMMENT '权限描述',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission` (`resource`,`action`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permissions` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `permission_id` bigint(20) NOT NULL COMMENT '权限ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permissions_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permissions_role_id` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permissions_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 文件表
CREATE TABLE IF NOT EXISTS `files` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '文件ID',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `storage_name` varchar(255) NOT NULL COMMENT '存储文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint(20) NOT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) NOT NULL COMMENT 'MIME类型',
  `file_hash` varchar(64) DEFAULT NULL COMMENT '文件哈希值',
  `uploaded_by` bigint(20) NOT NULL COMMENT '上传用户ID',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_uploaded_by` (`uploaded_by`),
  KEY `idx_file_hash` (`file_hash`),
  CONSTRAINT `fk_files_uploaded_by` FOREIGN KEY (`uploaded_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS `operation_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `operation` varchar(100) NOT NULL COMMENT '操作类型',
  `resource` varchar(255) NOT NULL COMMENT '操作资源',
  `method` varchar(10) NOT NULL COMMENT 'HTTP方法',
  `request_url` varchar(500) NOT NULL COMMENT '请求URL',
  `request_params` text COMMENT '请求参数',
  `response_result` text COMMENT '响应结果',
  `ip_address` varchar(50) NOT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `execution_time` int(11) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` varchar(20) NOT NULL COMMENT '状态',
  `error_message` text COMMENT '错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_operation` (`operation`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `fk_operation_logs_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 系统配置表
CREATE TABLE IF NOT EXISTS `system_configs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` text COMMENT '配置值',
  `description` varchar(255) DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(20) NOT NULL DEFAULT 'STRING' COMMENT '配置类型',
  `is_system` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否系统配置',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统配置表';

-- 初始化角色数据
INSERT INTO `roles` (`name`, `description`) VALUES
('ADMIN', '系统管理员'),
('USER', '普通用户'),
('MANAGER', '管理员');

-- 初始化权限数据
INSERT INTO `permissions` (`name`, `resource`, `action`, `description`) VALUES
('用户管理', 'user', 'create', '创建用户'),
('用户管理', 'user', 'read', '查看用户'),
('用户管理', 'user', 'update', '更新用户'),
('用户管理', 'user', 'delete', '删除用户'),
('角色管理', 'role', 'create', '创建角色'),
('角色管理', 'role', 'read', '查看角色'),
('角色管理', 'role', 'update', '更新角色'),
('角色管理', 'role', 'delete', '删除角色'),
('权限管理', 'permission', 'read', '查看权限'),
('系统配置', 'config', 'read', '查看系统配置'),
('系统配置', 'config', 'update', '更新系统配置'),
('文件管理', 'file', 'upload', '上传文件'),
('文件管理', 'file', 'download', '下载文件'),
('文件管理', 'file', 'delete', '删除文件'),
('日志查看', 'log', 'read', '查看操作日志');

-- 分配角色权限
-- 管理员拥有所有权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT r.id, p.id FROM `roles` r, `permissions` p WHERE r.name = 'ADMIN';

-- 普通用户只有基本权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'USER' AND p.name IN ('用户管理', 'user', 'read', '文件管理', 'file', 'download');

-- 管理员权限
INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT r.id, p.id FROM `roles` r, `permissions` p 
WHERE r.name = 'MANAGER' AND p.action IN ('read', 'update');

-- 初始化系统配置
INSERT INTO `system_configs` (`config_key`, `config_value`, `description`, `config_type`, `is_system`) VALUES
('system.name', 'Spring4demo', '系统名称', 'STRING', 1),
('system.version', '1.0.0', '系统版本', 'STRING', 1),
('system.author', 'Spring4demo Team', '系统作者', 'STRING', 1),
('file.upload.max.size', '104857600', '文件上传最大大小(字节)', 'LONG', 0),
('file.upload.allowed.types', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', '允许上传的文件类型', 'STRING', 0),
('satoken.timeout', '2592000', 'Sa-Token过期时间(秒)', 'LONG', 0),
('satoken.activity.timeout', '-1', 'Sa-Token临时有效期(秒)', 'LONG', 0),
('password.min.length', '6', '密码最小长度', 'INTEGER', 0),
('password.max.length', '20', '密码最大长度', 'INTEGER', 0);

-- 创建默认管理员用户
INSERT INTO `users` (`username`, `email`, `password`, `first_name`, `last_name`, `status`) VALUES
('admin', 'admin@spring4demo.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iYqiSfFDYZt/I5/BFnhkSLsVBDSC', 'Admin', 'User', 'ACTIVE');

-- 分配管理员角色给默认用户
INSERT INTO `user_roles` (`user_id`, `role_id`) 
SELECT u.id, r.id FROM `users` u, `roles` r 
WHERE u.username = 'admin' AND r.name = 'ADMIN';

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
  `local_endpoint_service_name` varchar(255) DEFAULT NULL,
  `local_endpoint_ipv4` varchar(45) DEFAULT NULL,
  `local_endpoint_ipv6` varchar(45) DEFAULT NULL,
  `local_endpoint_port` int(11) DEFAULT NULL,
  `remote_endpoint_service_name` varchar(255) DEFAULT NULL,
  `remote_endpoint_ipv4` varchar(45) DEFAULT NULL,
  `remote_endpoint_ipv6` varchar(45) DEFAULT NULL,
  `remote_endpoint_port` int(11) DEFAULT NULL,
  `annotations` mediumtext COMMENT '注解',
  `tags` mediumtext COMMENT '标签',
  `shared` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否共享',
  PRIMARY KEY (`trace_id_high`,`trace_id`,`id`),
  KEY `idx_trace_id` (`trace_id_high`,`trace_id`),
  KEY `idx_name` (`name`),
  KEY `idx_service_name` (`service_name`),
  KEY `idx_timestamp` (`timestamp`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 设置外键检查
SET FOREIGN_KEY_CHECKS = 1;