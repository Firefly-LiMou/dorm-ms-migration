package com.dorm.service;

import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.BuildingDTO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.query.BuildingPageQuery;

import java.util.List;

/**
 * 楼栋服务接口
 */
public interface BuildingService {

    /** 分页查询楼栋 */
    PageVO<DormBuildingDO> pageBuildings(BuildingPageQuery query);

    /** 查询全部楼栋下拉列表 */
    List<DormBuildingDO> listAll();

    /** 新增楼栋 */
    void addBuilding(BuildingDTO dto);

    /** 编辑楼栋 */
    void updateBuilding(Long buildingId, BuildingDTO dto);

    /** 删除楼栋（校验下级房间） */
    void deleteBuilding(Long buildingId);
}
