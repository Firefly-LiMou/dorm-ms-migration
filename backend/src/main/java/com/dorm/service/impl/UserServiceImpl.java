package com.dorm.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.enums.UserRole;
import com.dorm.common.enums.UserStatus;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.common.utils.OperationLogContext;
import com.dorm.entity.dto.UserCreateDTO;
import com.dorm.entity.dto.UserUpdateDTO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.UserQuery;
import com.dorm.entity.vo.UserVO;
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 学生账号管理服务实现
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    /** 初始密码：创建学生账号与重置密码统一使用 */
    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(SysUserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageVO<UserVO> page(UserQuery query) {
        Page<SysUserDO> page = userMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()),
                Wrappers.<SysUserDO>lambdaQuery()
                        .eq(SysUserDO::getRole, UserRole.STUDENT.getValue())
                        .like(StringUtils.hasText(query.getUsername()), SysUserDO::getUsername, query.getUsername())
                        .like(StringUtils.hasText(query.getRealName()), SysUserDO::getRealName, query.getRealName())
                        .eq(query.getStatus() != null, SysUserDO::getStatus, query.getStatus())
                        .orderByDesc(SysUserDO::getCreateTime));
        List<UserVO> list = page.getRecords().stream()
                .map(user -> BeanUtil.copyProperties(user, UserVO.class))
                .collect(Collectors.toList());
        PageVO<UserVO> result = new PageVO<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setList(list);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserCreateDTO dto) {
        String username = dto.getUsername().trim();
        Long count = userMapper.selectCount(
                Wrappers.<SysUserDO>lambdaQuery().eq(SysUserDO::getUsername, username));
        if (count > 0) {
            throw new BusinessException(1003, "学号已存在");
        }
        SysUserDO user = new SysUserDO();
        user.setUsername(username);
        user.setRealName(dto.getRealName().trim());
        user.setGender(blankToNull(dto.getGender()));
        user.setPhone(blankToNull(dto.getPhone()));
        user.setRole(UserRole.STUDENT.getValue());
        user.setStatus(UserStatus.NORMAL.getValue());
        user.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userMapper.insert(user);
        OperationLogContext.setChange(buildCreateChange(user));
        log.info("创建学生账号成功: userId={} username={}", user.getUserId(), username);
        return user.getUserId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, UserUpdateDTO dto) {
        SysUserDO exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        SysUserDO update = new SysUserDO();
        update.setUserId(userId);
        if (StringUtils.hasText(dto.getRealName())) {
            update.setRealName(dto.getRealName().trim());
        }
        if (StringUtils.hasText(dto.getGender())) {
            update.setGender(dto.getGender());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            update.setPhone(dto.getPhone());
        }
        if (dto.getStatus() != null) {
            validateStatus(dto.getStatus());
            update.setStatus(dto.getStatus());
        }
        userMapper.updateById(update);
        OperationLogContext.setChange(buildUpdateChange(exist, dto));
        log.info("编辑学生账号成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(Long userId) {
        SysUserDO exist = userMapper.selectById(userId);
        if (exist == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        SysUserDO update = new SysUserDO();
        update.setUserId(userId);
        update.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userMapper.updateById(update);
        OperationLogContext.setChange("密码: 重置为初始密码123456");
        log.info("重置学生密码成功: userId={}", userId);
    }

    private void validateStatus(Integer status) {
        if (!UserStatus.NORMAL.getValue().equals(status) && !UserStatus.DISABLED.getValue().equals(status)) {
            throw new BusinessException(400, "账号状态不合法");
        }
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    /** 构造创建学生账号的变更描述（新数据） */
    private String buildCreateChange(SysUserDO user) {
        StringBuilder sb = new StringBuilder();
        sb.append("学号: ").append(user.getUsername());
        sb.append(", 姓名: ").append(user.getRealName());
        if (user.getGender() != null) {
            sb.append(", 性别: ").append(user.getGender());
        }
        if (user.getPhone() != null) {
            sb.append(", 手机号: ").append(user.getPhone());
        }
        return sb.toString();
    }

    /** 构造编辑学生账号的变更描述（旧值→新值） */
    private String buildUpdateChange(SysUserDO old, UserUpdateDTO dto) {
        List<String> parts = new ArrayList<>();
        if (StringUtils.hasText(dto.getRealName()) && !old.getRealName().equals(dto.getRealName().trim())) {
            parts.add("姓名: " + old.getRealName() + "→" + dto.getRealName().trim());
        }
        if (StringUtils.hasText(dto.getGender()) && !Objects.equals(old.getGender(), dto.getGender())) {
            parts.add("性别: " + display(old.getGender()) + "→" + dto.getGender());
        }
        if (StringUtils.hasText(dto.getPhone()) && !Objects.equals(old.getPhone(), dto.getPhone())) {
            parts.add("手机号: " + display(old.getPhone()) + "→" + dto.getPhone());
        }
        if (dto.getStatus() != null && !Objects.equals(old.getStatus(), dto.getStatus())) {
            parts.add("状态: " + statusText(old.getStatus()) + "→" + statusText(dto.getStatus()));
        }
        return String.join(", ", parts);
    }

    /** 空值展示为「空」 */
    private String display(String value) {
        return value == null ? "空" : value;
    }

    /** 账号状态中文文案 */
    private String statusText(Integer status) {
        return UserStatus.NORMAL.getValue().equals(status) ? "正常" : "禁用";
    }
}
