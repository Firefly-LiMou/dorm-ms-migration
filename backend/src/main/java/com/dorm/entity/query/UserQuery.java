package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生账号分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQuery extends PageQuery {

    /** 学号（模糊查询） */
    private String username;

    /** 姓名（模糊查询） */
    private String realName;

    /** 账号状态：1-正常，0-禁用 */
    private Integer status;
}
