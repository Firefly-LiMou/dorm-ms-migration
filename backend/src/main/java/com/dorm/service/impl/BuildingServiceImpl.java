package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.vo.BuildingVO;
import com.dorm.mapper.DormBuildingMapper;
import com.dorm.service.BuildingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 楼栋服务实现
 */
@Service
public class BuildingServiceImpl implements BuildingService {

    private final DormBuildingMapper buildingMapper;

    public BuildingServiceImpl(DormBuildingMapper buildingMapper) {
        this.buildingMapper = buildingMapper;
    }

    @Override
    public List<BuildingVO> listAll() {
        List<DormBuildingDO> list = buildingMapper.selectList(
                Wrappers.<DormBuildingDO>lambdaQuery().orderByAsc(DormBuildingDO::getBuildingId));
        return list.stream().map(this::convert).collect(Collectors.toList());
    }

    private BuildingVO convert(DormBuildingDO building) {
        BuildingVO vo = new BuildingVO();
        vo.setBuildingId(building.getBuildingId());
        vo.setBuildingNo(building.getBuildingNo());
        vo.setBuildingName(building.getBuildingName());
        return vo;
    }
}
