package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    /** 学号模糊 */
    private String username;

    /** 姓名模糊 */
    private String realName;

    /** 状态：1-正常，0-禁用 */
    private Integer status;
}
