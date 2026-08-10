package com.dorm.common.page;

import lombok.Data;

/**
 * 分页查询入参基类：所有分页查询 Query 必须继承
 */
@Data
public class PageQuery {

    /** 页码，从 1 开始 */
    private Integer pageNum = 1;

    /** 每页条数，默认 10 */
    private Integer pageSize = 10;
}
