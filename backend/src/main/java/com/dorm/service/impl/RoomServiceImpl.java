package com.dorm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.RoomDTO;
import com.dorm.entity.po.DormBedDO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.po.DormRoomDO;
import com.dorm.entity.query.RoomPageQuery;
import com.dorm.mapper.DormBedMapper;
import com.dorm.mapper.DormBuildingMapper;
import com.dorm.mapper.DormRoomMapper;
import com.dorm.service.RoomService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 房间服务实现
 */
@Service
public class RoomServiceImpl implements RoomService {

    @Resource
    private DormRoomMapper roomMapper;

    @Resource
    private DormBuildingMapper buildingMapper;

    @Resource
    private DormBedMapper bedMapper;

    @Override
    public PageVO<DormRoomDO> pageRooms(RoomPageQuery query) {
        LambdaQueryWrapper<DormRoomDO> wrapper = Wrappers.<DormRoomDO>lambdaQuery()
                .eq(query.getBuildingId() != null, DormRoomDO::getBuildingId, query.getBuildingId())
                .like(StrUtil.isNotBlank(query.getRoomNo()), DormRoomDO::getRoomNo, query.getRoomNo())
                .orderByAsc(DormRoomDO::getBuildingId, DormRoomDO::getRoomNo);
        Page<DormRoomDO> page = roomMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        return PageVO.of(page);
    }

    @Override
    public List<DormRoomDO> listByBuilding(Long buildingId) {
        return roomMapper.selectList(
                Wrappers.<DormRoomDO>lambdaQuery()
                        .eq(DormRoomDO::getBuildingId, buildingId)
                        .orderByAsc(DormRoomDO::getRoomNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addRoom(RoomDTO dto) {
        DormBuildingDO building = buildingMapper.selectById(dto.getBuildingId());
        if (building == null) {
            throw new BusinessException(5001, "楼栋不存在");
        }
        if (dto.getFloor() > building.getFloorCount()) {
            throw new BusinessException(2005, "房间楼层超过楼栋总楼层");
        }
        Long count = roomMapper.selectCount(Wrappers.<DormRoomDO>lambdaQuery()
                .eq(DormRoomDO::getBuildingId, dto.getBuildingId())
                .eq(DormRoomDO::getRoomNo, dto.getRoomNo()));
        if (count > 0) {
            throw new BusinessException(2003, "房间编号已存在");
        }
        DormRoomDO room = new DormRoomDO();
        room.setBuildingId(dto.getBuildingId());
        room.setRoomNo(dto.getRoomNo());
        room.setFloor(dto.getFloor());
        room.setBedCount(dto.getBedCount() != null ? dto.getBedCount() : 4);
        room.setRoomType(dto.getRoomType());
        roomMapper.insert(room);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateRoom(Long roomId, RoomDTO dto) {
        DormRoomDO room = roomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        DormBuildingDO building = buildingMapper.selectById(dto.getBuildingId());
        if (building == null) {
            throw new BusinessException(5001, "楼栋不存在");
        }
        if (dto.getFloor() > building.getFloorCount()) {
            throw new BusinessException(2005, "房间楼层超过楼栋总楼层");
        }
        if (!room.getBuildingId().equals(dto.getBuildingId()) || !room.getRoomNo().equals(dto.getRoomNo())) {
            Long count = roomMapper.selectCount(Wrappers.<DormRoomDO>lambdaQuery()
                    .eq(DormRoomDO::getBuildingId, dto.getBuildingId())
                    .eq(DormRoomDO::getRoomNo, dto.getRoomNo()));
            if (count > 0) {
                throw new BusinessException(2003, "房间编号已存在");
            }
        }
        DormRoomDO update = new DormRoomDO();
        update.setRoomId(roomId);
        update.setBuildingId(dto.getBuildingId());
        update.setRoomNo(dto.getRoomNo());
        update.setFloor(dto.getFloor());
        update.setBedCount(dto.getBedCount());
        update.setRoomType(dto.getRoomType());
        roomMapper.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoom(Long roomId) {
        Long bedCount = bedMapper.selectCount(
                Wrappers.<DormBedDO>lambdaQuery().eq(DormBedDO::getRoomId, roomId));
        if (bedCount > 0) {
            throw new BusinessException(2004, "房间下存在床位，禁止删除");
        }
        roomMapper.deleteById(roomId);
    }
}
