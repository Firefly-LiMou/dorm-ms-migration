package com.dorm.service;

import com.dorm.entity.vo.BuildingVO;

import java.util.List;

/**
 * 楼栋服务
 */
public interface BuildingService {

    /**
     * 查询楼栋下拉列表（供房间管理、入住分配、报修筛选下拉选择）
     *
     * @return 全部楼栋（不分页）
     */
    List<BuildingVO> listAll();
}
