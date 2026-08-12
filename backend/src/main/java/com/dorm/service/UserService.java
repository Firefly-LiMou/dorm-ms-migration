package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.ChangePasswordDTO;
import com.dorm.entity.dto.CreateUserDTO;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.dto.UpdateProfileDTO;
import com.dorm.entity.dto.UpdateUserDTO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.UserPageQuery;
import com.dorm.entity.vo.LoginVO;
import com.dorm.entity.vo.UserInfoVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /** 登录 */
    LoginVO login(LoginDTO dto);

    /** 登出 */
    void logout();

    /** 获取当前登录用户信息 */
    UserInfoVO getProfile();

    /** 修改个人信息 */
    void updateProfile(UpdateProfileDTO dto);

    /** 修改登录密码 */
    void changePassword(ChangePasswordDTO dto);

    /** 管理员分页查询学生账号 */
    PageVO<UserInfoVO> pageUsers(UserPageQuery query);

    /** 管理员创建学生账号 */
    void createUser(CreateUserDTO dto);

    /** 管理员编辑学生账号 */
    void updateUser(Long userId, UpdateUserDTO dto);

    /** 管理员重置学生密码 */
    void resetPassword(Long userId);

    /** 按用户名查询用户 */
    SysUserDO getByUsername(String username);
}
