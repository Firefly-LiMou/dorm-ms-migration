package com.dorm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
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
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 用户服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /** 新建学生账号默认初始密码 */
    private static final String DEFAULT_PASSWORD = "123456";

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
        // Sa-Token 登录，将会话角色/用户名/姓名写入
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
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(UpdateProfileDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUserDO user = new SysUserDO();
        user.setUserId(userId);
        user.setRealName(dto.getRealName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        userMapper.updateById(user);
        // 同步 Session 中的姓名
        StpUtil.getSession().set("realName", dto.getRealName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(ChangePasswordDTO dto) {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUserDO user = userMapper.selectById(userId);
        if (!ENCODER.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException(1004, "原密码错误");
        }
        SysUserDO update = new SysUserDO();
        update.setUserId(userId);
        update.setPassword(ENCODER.encode(dto.getNewPassword()));
        userMapper.updateById(update);
    }

    @Override
    public PageVO<UserInfoVO> pageUsers(UserPageQuery query) {
        LambdaQueryWrapper<SysUserDO> wrapper = Wrappers.<SysUserDO>lambdaQuery()
                .eq(SysUserDO::getRole, "student")
                .like(StrUtil.isNotBlank(query.getUsername()), SysUserDO::getUsername, query.getUsername())
                .like(StrUtil.isNotBlank(query.getRealName()), SysUserDO::getRealName, query.getRealName())
                .eq(query.getStatus() != null, SysUserDO::getStatus, query.getStatus())
                .orderByDesc(SysUserDO::getCreateTime);
        Page<SysUserDO> page = userMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        PageVO<UserInfoVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(UserInfoVO::from).collect(java.util.stream.Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUser(CreateUserDTO dto) {
        // 校验学号唯一性
        SysUserDO exist = getByUsername(dto.getUsername());
        if (exist != null) {
            throw new BusinessException(1003, "学号已存在");
        }
        SysUserDO user = new SysUserDO();
        user.setUsername(dto.getUsername());
        user.setPassword(ENCODER.encode(DEFAULT_PASSWORD));
        user.setRealName(dto.getRealName());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setRole("student");
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long userId, UpdateUserDTO dto) {
        SysUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        SysUserDO update = new SysUserDO();
        update.setUserId(userId);
        if (dto.getRealName() != null) {
            update.setRealName(dto.getRealName());
        }
        update.setGender(dto.getGender());
        update.setPhone(dto.getPhone());
        if (dto.getStatus() != null) {
            update.setStatus(dto.getStatus());
        }
        userMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        SysUserDO user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        SysUserDO update = new SysUserDO();
        update.setUserId(userId);
        update.setPassword(ENCODER.encode(DEFAULT_PASSWORD));
        userMapper.updateById(update);
    }

    @Override
    public SysUserDO getByUsername(String username) {
        return userMapper.selectOne(
                Wrappers.<SysUserDO>lambdaQuery().eq(SysUserDO::getUsername, username));
    }
}
