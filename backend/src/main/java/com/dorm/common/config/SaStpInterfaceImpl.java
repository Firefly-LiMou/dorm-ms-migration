package com.dorm.common.config;

import cn.dev33.satoken.stp.StpInterface;
import com.dorm.entity.po.SysUserDO;
import com.dorm.mapper.SysUserMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限接口实现：角色从 sys_user 表按登录用户实时查询，支撑 @SaCheckRole 注解鉴权
 */
@Component
public class SaStpInterfaceImpl implements StpInterface {

    private final SysUserMapper userMapper;

    public SaStpInterfaceImpl(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 本项目按角色鉴权，不涉及按钮级权限
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SysUserDO user = userMapper.selectById(Long.valueOf(loginId.toString()));
        return user == null ? Collections.emptyList() : Collections.singletonList(user.getRole());
    }
}
