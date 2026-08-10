-- 种子数据：需先执行 create.sql 建表后再执行本脚本
-- 所有账号初始密码均为 123456（BCrypt 加密存储，$2b$ 前缀，兼容 spring-security-crypto 的 BCryptPasswordEncoder）

USE dorm_manage;

-- 1. 用户：1 个管理员 + 2 个学生
INSERT INTO sys_user (username, password, real_name, role, gender, phone, status) VALUES
('admin', '$2b$10$9Ab5a58YPeUO6B9m2.AQneVJiIoitA7peeH5dnw1TmdTc3XFnsIGy', '系统管理员', 'admin', NULL, NULL, 1),
('20240001', '$2b$10$7x/nuG.QH2.opD7Rt79TLekt7odNPbWm5rAG2bVSkGrTygggxTEj6', '张伟', 'student', '男', '13800000001', 1),
('20240002', '$2b$10$9Ab5a58YPeUO6B9m2.AQneVJiIoitA7peeH5dnw1TmdTc3XFnsIGy', '李娜', 'student', '女', '13800000002', 1);

-- 2. 楼栋：一号学生公寓（东区，6 层）
INSERT INTO dorm_building (building_no, building_name, floor_count, area) VALUES
('1', '一号学生公寓', 6, '东区');

-- 3. 房间：1 号楼 101、102、201（各 4 人间）
INSERT INTO dorm_room (building_id, room_no, floor, bed_count, room_type) VALUES
(1, '101', 1, 4, '标准四人间'),
(1, '102', 1, 4, '标准四人间'),
(1, '201', 2, 4, '标准四人间');

-- 4. 床位：101、102 各初始化 4 个床位（全部空闲）
INSERT INTO dorm_bed (room_id, bed_no, status) VALUES
(1, '1', 0), (1, '2', 0), (1, '3', 0), (1, '4', 0),
(2, '1', 0), (2, '2', 0), (2, '3', 0), (2, '4', 0);

-- 5. 入住记录：20240001（张伟）入住 1 号楼 101 房 1 床，由 admin 办理
INSERT INTO dorm_checkin (user_id, bed_id, checkin_time, status, operator_id, remark) VALUES
(2, 1, NOW(), 1, 1, '新生入住');

-- 同步：101 房 1 床标记为已入住
UPDATE dorm_bed SET status = 1 WHERE bed_id = 1;

-- 6. 示例报修：20240001 提交一条待处理报修（演示超时高亮与状态流转）
INSERT INTO dorm_repair (user_id, room_id, repair_type, content, contact_phone, status) VALUES
(2, 1, 0, '宿舍灯管不亮，无法照明', '13800000001', 0);
