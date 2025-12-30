-- 创建用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255),
    email VARCHAR(100),
    phone VARCHAR(20),
    real_name VARCHAR(50),
    avatar VARCHAR(255),
    status INT DEFAULT 1,
    dept_id VARCHAR(64),
    create_by VARCHAR(64),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_by VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted INT DEFAULT 0,
    version INT DEFAULT 0
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_username ON sys_user(username);
CREATE INDEX IF NOT EXISTS idx_email ON sys_user(email);
CREATE INDEX IF NOT EXISTS idx_status ON sys_user(status);
CREATE INDEX IF NOT EXISTS idx_deleted ON sys_user(deleted);