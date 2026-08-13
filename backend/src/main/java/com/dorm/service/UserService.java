package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.UserPageQuery;
import com.dorm.entity.vo.LoginVO;
import com.dorm.entity.vo.UserInfoVO;

/**
 * 用户服务接口（认证 + 学生列表最小集）
 */
public interface UserService {

    /** 登录 */
    LoginVO login(LoginDTO dto);

    /** 登出 */
    void logout();

    /** 获取当前登录用户信息 */
    UserInfoVO getProfile();

    /** 管理员分页查询学生账号（入住分配选人用） */
    PageVO<UserInfoVO> pageStudents(UserPageQuery query);

    /** 按用户名查询用户 */
    SysUserDO getByUsername(String username);
}
