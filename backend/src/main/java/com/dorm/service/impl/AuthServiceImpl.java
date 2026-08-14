package com.dorm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorm.common.enums.UserStatus;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.utils.LoginUserUtil;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.vo.LoginVO;
import com.dorm.entity.vo.UserVO;
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImpl(SysUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUserDO user = userMapper.selectOne(
                Wrappers.<SysUserDO>lambdaQuery().eq(SysUserDO::getUsername, dto.getUsername()));
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            log.info("登录失败: username={}", dto.getUsername());
            throw new BusinessException(1001, "账号或密码错误");
        }
        if (UserStatus.DISABLED.getValue().equals(user.getStatus())) {
            log.info("登录失败，账号已禁用: username={}", dto.getUsername());
            throw new BusinessException(1002, "账号已禁用");
        }
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set("role", user.getRole());
        StpUtil.getSession().set("username", user.getUsername());
        StpUtil.getSession().set("realName", user.getRealName());
        log.info("登录成功: userId={} username={} role={}", user.getUserId(), user.getUsername(), user.getRole());

        LoginVO vo = new LoginVO();
        vo.setToken(StpUtil.getTokenValue());
        vo.setUserInfo(convert(user));
        return vo;
    }

    @Override
    public void logout() {
        log.info("登出: userId={}", LoginUserUtil.getUserId());
        StpUtil.logout();
    }

    @Override
    public UserVO profile() {
        SysUserDO user = userMapper.selectById(LoginUserUtil.getUserId());
        if (user == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        return convert(user);
    }

    private UserVO convert(SysUserDO user) {
        UserVO vo = new UserVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setGender(user.getGender());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}
