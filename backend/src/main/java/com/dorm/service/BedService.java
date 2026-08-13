package com.dorm.service;

import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.BedBatchDTO;
import com.dorm.entity.dto.BedStatusDTO;
import com.dorm.entity.po.DormBedDO;
import com.dorm.entity.query.BedPageQuery;

/**
 * 床位服务接口
 */
public interface BedService {

    /** 分页查询床位 */
    PageVO<DormBedDO> pageBeds(BedPageQuery query);

    /** 批量初始化房间床位 */
    void batchCreate(BedBatchDTO dto);

    /** 手动更新床位状态（仅数据纠错） */
    void updateStatus(Long bedId, BedStatusDTO dto);
}
