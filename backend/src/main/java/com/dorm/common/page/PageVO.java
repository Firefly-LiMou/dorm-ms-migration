package com.dorm.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页响应封装
 *
 * @param <T> 列表元素类型
 */
@Data
public class PageVO<T> {

    /** 总条数 */
    private Long total;

    /** 总页数 */
    private Long pages;

    /** 当前页数据 */
    private List<T> list;

    /**
     * 由 MyBatis-Plus 分页对象转换
     */
    public static <T> PageVO<T> of(IPage<T> page) {
        PageVO<T> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords());
        return vo;
    }
}
