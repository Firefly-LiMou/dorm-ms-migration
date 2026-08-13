package com.dorm.service;

import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.CheckinDTO;
import com.dorm.entity.dto.CheckoutDTO;
import com.dorm.entity.query.CheckinPageQuery;
import com.dorm.entity.vo.CheckinVO;

/**
 * 入住管理服务接口
 */
public interface CheckinService {

    /** 入住分配 */
    void checkin(CheckinDTO dto);

    /** 办理退宿 */
    void checkout(Long checkinId, CheckoutDTO dto);

    /** 管理员分页查询入住记录 */
    PageVO<CheckinVO> pageCheckins(CheckinPageQuery query);

    /** 学生查询本人入住记录 */
    PageVO<CheckinVO> pageMyCheckins(Integer pageNum, Integer pageSize);

    /** 删除入住记录（仅已退宿记录可删除） */
    void deleteCheckin(Long checkinId);
}
