package com.dorm.service;

import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.UserCreateDTO;
import com.dorm.entity.dto.UserUpdateDTO;
import com.dorm.entity.query.UserQuery;
import com.dorm.entity.vo.UserVO;

/**
 * 学生账号管理服务（管理员）
 */
public interface UserService {

    /**
     * 分页查询学生账号
     *
     * @param query 查询条件（学号模糊 / 姓名模糊 / 状态精确）
     * @return 分页结果
     */
    PageVO<UserVO> page(UserQuery query);

    /**
     * 创建学生账号：默认密码 123456，校验学号唯一
     *
     * @param dto 创建入参
     * @return 新用户ID
     * @throws BusinessException 1003 学号已存在
     */
    Long create(UserCreateDTO dto);

    /**
     * 编辑学生账号：仅更新非空字段，可用于修改信息或禁用/启用
     *
     * @param userId 用户ID
     * @param dto    编辑入参
     * @throws BusinessException 5001 数据不存在
     */
    void update(Long userId, UserUpdateDTO dto);

    /**
     * 重置学生密码为初始密码 123456
     *
     * @param userId 用户ID
     * @throws BusinessException 5001 数据不存在
     */
    void resetPassword(Long userId);
}
