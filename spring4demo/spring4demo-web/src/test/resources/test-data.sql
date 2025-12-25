-- 测试数据脚本
-- 用于集成测试的初始数据

-- 插入测试用户数据
INSERT INTO sys_user (id, username, password, email, phone, real_name, avatar, status, dept_id, create_by, create_time, update_by, update_time, deleted, version) VALUES 
('user-001', 'testuser1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test1@example.com', '13800138001', '测试用户1', 'https://example.com/avatar1.jpg', 1, 'dept-001', 'system', NOW(), 'system', NOW(), 0, 1),
('user-002', 'testuser2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'test2@example.com', '13800138002', '测试用户2', 'https://example.com/avatar2.jpg', 1, 'dept-001', 'system', NOW(), 'system', NOW(), 0, 1),
('user-003', 'inactive-user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'inactive@example.com', '13800138003', '非活跃用户', 'https://example.com/avatar3.jpg', 0, 'dept-002', 'system', NOW(), 'system', NOW(), 0, 1),
('user-004', 'admin-user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', 'admin@example.com', '13800138004', '管理员', 'https://example.com/avatar4.jpg', 1, 'dept-003', 'system', NOW(), 'system', NOW(), 0, 1);

-- 插入部门数据（如果存在部门表）
-- INSERT INTO sys_dept (id, dept_name, parent_id, sort_order, status, create_by, create_time, update_by, update_time, deleted, version) VALUES 
-- ('dept-001', '技术部', NULL, 1, 1, 'system', NOW(), 'system', NOW(), 0, 1),
-- ('dept-002', '市场部', NULL, 2, 1, 'system', NOW(), 'system', NOW(), 0, 1),
-- ('dept-003', '行政部', NULL, 3, 1, 'system', NOW(), 'system', NOW(), 0, 1);