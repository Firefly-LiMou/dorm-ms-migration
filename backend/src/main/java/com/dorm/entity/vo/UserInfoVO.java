package com.dorm.entity.vo;

import com.dorm.entity.po.SysUserDO;
import lombok.Data;

/**
 * 用户信息视图对象
 */
@Data
public class UserInfoVO {

    private Long userId;

    private String username;

    private String realName;

    private String role;

    private String gender;

    private String phone;

    public static UserInfoVO from(SysUserDO user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setRole(user.getRole());
        vo.setGender(user.getGender());
        vo.setPhone(user.getPhone());
        return vo;
    }
}
