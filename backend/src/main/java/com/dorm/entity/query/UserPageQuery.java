package com.dorm.entity.query;

import com.dorm.common.page.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 学生账号分页查询参数（入住分配选人用）
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserPageQuery extends PageQuery {

    /** 学号模糊 */
    private String username;

    /** 姓名模糊 */
    private String realName;
}
