package com.dorm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.LoginDTO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.UserPageQuery;
import com.dorm.entity.vo.LoginVO;
import com.dorm.entity.vo.UserInfoVO;
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Resource
    private SysUserMapper userMapper;

    @Override
    public LoginVO login(LoginDTO dto) {
        SysUserDO user = getByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException(1001, "账号或密码错误");
        }
        if (!ENCODER.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(1001, "账号或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(1002, "账号已禁用");
        }
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set("role", user.getRole());
        StpUtil.getSession().set("username", user.getUsername());
        StpUtil.getSession().set("realName", user.getRealName());
        String token = StpUtil.getTokenValue();
        log.info("用户登录成功 userId={} role={}", user.getUserId(), user.getRole());
        return LoginVO.of(token, UserInfoVO.from(user));
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public UserInfoVO getProfile() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(5001, "用户不存在");
        }
        return UserInfoVO.from(user);
    }

    @Override
    public PageVO<UserInfoVO> pageStudents(UserPageQuery query) {
        LambdaQueryWrapper<SysUserDO> wrapper = Wrappers.<SysUserDO>lambdaQuery()
                .eq(SysUserDO::getRole, "student")
                .like(StrUtil.isNotBlank(query.getUsername()), SysUserDO::getUsername, query.getUsername())
                .like(StrUtil.isNotBlank(query.getRealName()), SysUserDO::getRealName, query.getRealName())
                .orderByDesc(SysUserDO::getCreateTime);
        Page<SysUserDO> page = userMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        PageVO<UserInfoVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(UserInfoVO::from).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public SysUserDO getByUsername(String username) {
        return userMapper.selectOne(
                Wrappers.<SysUserDO>lambdaQuery().eq(SysUserDO::getUsername, username));
    }
}
