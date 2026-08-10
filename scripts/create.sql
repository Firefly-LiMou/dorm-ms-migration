-- 数据库创建
CREATE DATABASE IF NOT EXISTS dorm_manage DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE dorm_manage;

-- 1. 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户主键ID',
    username VARCHAR(32) NOT NULL COMMENT '登录账号',
    password VARCHAR(128) NOT NULL COMMENT '登录密码（加密）',
    real_name VARCHAR(32) NOT NULL COMMENT '真实姓名',
    role CHAR(10) NOT NULL DEFAULT 'student' COMMENT '角色：student-学生，admin-管理员',
    gender CHAR(2) DEFAULT NULL COMMENT '性别',
    phone VARCHAR(11) DEFAULT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态：1-正常，0-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY idx_username (username),
    KEY idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 楼栋表
DROP TABLE IF EXISTS dorm_building;
CREATE TABLE dorm_building (
    building_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋主键ID',
    building_no VARCHAR(20) NOT NULL COMMENT '楼栋编号',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    floor_count INT NOT NULL COMMENT '总楼层数',
    area VARCHAR(20) DEFAULT NULL COMMENT '所属区域',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY idx_building_no (building_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋信息表';

-- 3. 房间表
DROP TABLE IF EXISTS dorm_room;
CREATE TABLE dorm_room (
    room_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '房间主键ID',
    building_id BIGINT NOT NULL COMMENT '所属楼栋ID',
    room_no VARCHAR(20) NOT NULL COMMENT '房间编号',
    floor INT NOT NULL COMMENT '所在楼层',
    bed_count INT NOT NULL DEFAULT 4 COMMENT '床位数',
    room_type VARCHAR(20) DEFAULT NULL COMMENT '房间类型',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_building_room (building_id, room_no),
    KEY idx_building_id (building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房间信息表';

-- 4. 床位表
DROP TABLE IF EXISTS dorm_bed;
CREATE TABLE dorm_bed (
    bed_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '床位主键ID',
    room_id BIGINT NOT NULL COMMENT '所属房间ID',
    bed_no VARCHAR(10) NOT NULL COMMENT '床位编号',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '入住状态：0-空闲，1-已入住',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_room_bed (room_id, bed_no),
    KEY idx_room_id (room_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位信息表';

-- 5. 入住记录表
DROP TABLE IF EXISTS dorm_checkin;
CREATE TABLE dorm_checkin (
    checkin_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '入住记录ID',
    user_id BIGINT NOT NULL COMMENT '学生用户ID',
    bed_id BIGINT NOT NULL COMMENT '入住床位ID',
    checkin_time DATETIME NOT NULL COMMENT '入住时间',
    checkout_time DATETIME DEFAULT NULL COMMENT '退宿时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '记录状态：1-入住中，2-已退宿',
    operator_id BIGINT NOT NULL COMMENT '办理人ID',
    remark VARCHAR(255) DEFAULT NULL COMMENT '备注',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_bed_id (bed_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住记录表';

-- 6. 报修表
DROP TABLE IF EXISTS dorm_repair;
CREATE TABLE dorm_repair (
    repair_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报修单ID',
    user_id BIGINT NOT NULL COMMENT '报修学生ID',
    room_id BIGINT NOT NULL COMMENT '报修房间ID',
    repair_type TINYINT NOT NULL DEFAULT 0 COMMENT '报修类型：0-水电故障，1-家具损坏，2-网络问题，3-其他',
    content TEXT NOT NULL COMMENT '报修内容',
    contact_phone VARCHAR(11) NOT NULL COMMENT '联系电话',
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理，1-处理中，2-已完成',
    handle_result TEXT DEFAULT NULL COMMENT '处理结果',
    handle_time DATETIME DEFAULT NULL COMMENT '处理完成时间',
    handler_id BIGINT DEFAULT NULL COMMENT '处理人ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_room_id (room_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修单表';

-- 7. 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
    log_id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '日志主键ID',
    operator_id BIGINT NOT NULL COMMENT '操作人ID',
    operator_name VARCHAR(32) NOT NULL COMMENT '操作人姓名',
    module VARCHAR(50) NOT NULL COMMENT '操作模块',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型',
    content VARCHAR(500) NOT NULL COMMENT '操作详情',
    ip_address VARCHAR(50) DEFAULT NULL COMMENT '操作IP',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_operator_id (operator_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';