package com.dorm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.RepairHandleDTO;
import com.dorm.entity.dto.RepairSubmitDTO;
import com.dorm.entity.query.RepairPageQuery;
import com.dorm.entity.vo.RepairVO;

/**
 * 报修管理服务接口
 */
public interface RepairService {

    /** 学生提交报修 */
    void submitRepair(RepairSubmitDTO dto);

    /** 管理员分页查询报修单 */
    PageVO<RepairVO> pageRepairs(RepairPageQuery query);

    /** 学生分页查询本人报修 */
    PageVO<RepairVO> pageMyRepairs(Integer pageNum, Integer pageSize, Integer status);

    /** 管理员处理报修 */
    void handleRepair(Long repairId, RepairHandleDTO dto);
}
