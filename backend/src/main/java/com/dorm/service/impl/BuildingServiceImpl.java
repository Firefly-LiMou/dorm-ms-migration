package com.dorm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.BuildingDTO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.query.BuildingPageQuery;
import com.dorm.mapper.DormBuildingMapper;
import com.dorm.mapper.DormRoomMapper;
import com.dorm.service.BuildingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 楼栋服务实现
 */
@Service
public class BuildingServiceImpl implements BuildingService {

    @Resource
    private DormBuildingMapper buildingMapper;

    @Resource
    private DormRoomMapper roomMapper;

    @Override
    public PageVO<DormBuildingDO> pageBuildings(BuildingPageQuery query) {
        LambdaQueryWrapper<DormBuildingDO> wrapper = Wrappers.<DormBuildingDO>lambdaQuery()
                .like(StrUtil.isNotBlank(query.getBuildingNo()), DormBuildingDO::getBuildingNo, query.getBuildingNo())
                .eq(StrUtil.isNotBlank(query.getArea()), DormBuildingDO::getArea, query.getArea())
                .orderByAsc(DormBuildingDO::getBuildingNo);
        Page<DormBuildingDO> page = buildingMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageVO.of(page);
    }

    @Override
    public List<DormBuildingDO> listAll() {
        return buildingMapper.selectList(
                Wrappers.<DormBuildingDO>lambdaQuery().orderByAsc(DormBuildingDO::getBuildingNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addBuilding(BuildingDTO dto) {
        // 校验编号唯一性
        Long count = buildingMapper.selectCount(
                Wrappers.<DormBuildingDO>lambdaQuery().eq(DormBuildingDO::getBuildingNo, dto.getBuildingNo()));
        if (count > 0) {
            throw new BusinessException(2001, "楼栋编号已存在");
        }
        DormBuildingDO building = new DormBuildingDO();
        building.setBuildingNo(dto.getBuildingNo());
        building.setBuildingName(dto.getBuildingName());
        building.setFloorCount(dto.getFloorCount());
        building.setArea(dto.getArea());
        buildingMapper.insert(building);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBuilding(Long buildingId, BuildingDTO dto) {
        DormBuildingDO building = buildingMapper.selectById(buildingId);
        if (building == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        // 编号变更时校验唯一性
        if (!building.getBuildingNo().equals(dto.getBuildingNo())) {
            Long count = buildingMapper.selectCount(
                    Wrappers.<DormBuildingDO>lambdaQuery().eq(DormBuildingDO::getBuildingNo, dto.getBuildingNo()));
            if (count > 0) {
                throw new BusinessException(2001, "楼栋编号已存在");
            }
        }
        DormBuildingDO update = new DormBuildingDO();
        update.setBuildingId(buildingId);
        update.setBuildingNo(dto.getBuildingNo());
        update.setBuildingName(dto.getBuildingName());
        update.setFloorCount(dto.getFloorCount());
        update.setArea(dto.getArea());
        buildingMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBuilding(Long buildingId) {
        // 校验楼下是否有房间
        Long roomCount = roomMapper.selectCount(
                Wrappers.<com.dorm.entity.po.DormRoomDO>lambdaQuery().eq(com.dorm.entity.po.DormRoomDO::getBuildingId, buildingId));
        if (roomCount > 0) {
            throw new BusinessException(2002, "楼栋下存在房间，禁止删除");
        }
        buildingMapper.deleteById(buildingId);
    }
}
