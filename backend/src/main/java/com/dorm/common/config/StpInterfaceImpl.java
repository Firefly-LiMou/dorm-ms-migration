package com.dorm.common.config;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限扩展：从登录时写入的 Session 中读取角色和权限
 * @SaCheckRole / @SaCheckPermission 依赖此接口
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        SaSession session = StpUtil.getSessionByLoginId(loginId);
        String role = session.getString("role");
        if (role != null) {
            List<String> list = new ArrayList<>();
            list.add(role);
            return list;
        }
        return Collections.emptyList();
    }
}
