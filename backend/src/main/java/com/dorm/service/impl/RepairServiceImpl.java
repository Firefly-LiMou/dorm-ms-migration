package com.dorm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.RepairHandleDTO;
import com.dorm.entity.dto.RepairSubmitDTO;
import com.dorm.entity.po.DormBedDO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.po.DormCheckinDO;
import com.dorm.entity.po.DormRepairDO;
import com.dorm.entity.po.DormRoomDO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.RepairPageQuery;
import com.dorm.entity.vo.RepairVO;
import com.dorm.mapper.DormBedMapper;
import com.dorm.mapper.DormBuildingMapper;
import com.dorm.mapper.DormCheckinMapper;
import com.dorm.mapper.DormRepairMapper;
import com.dorm.mapper.DormRoomMapper;
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.RepairService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报修管理服务实现
 */
@Slf4j
@Service
public class RepairServiceImpl implements RepairService {

    @Resource
    private DormRepairMapper repairMapper;

    @Resource
    private DormCheckinMapper checkinMapper;

    @Resource
    private DormBedMapper bedMapper;

    @Resource
    private DormRoomMapper roomMapper;

    @Resource
    private DormBuildingMapper buildingMapper;

    @Resource
    private SysUserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitRepair(RepairSubmitDTO dto) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        // 校验学生有有效入住记录
        Long activeCheckin = checkinMapper.selectCount(
                Wrappers.<DormCheckinDO>lambdaQuery()
                        .eq(DormCheckinDO::getUserId, userId)
                        .eq(DormCheckinDO::getStatus, 1));
        if (activeCheckin == 0) {
            throw new BusinessException(4001, "无有效入住记录，禁止提交报修");
        }
        // 获取当前入住记录的房间ID
        DormCheckinDO checkin = checkinMapper.selectOne(
                Wrappers.<DormCheckinDO>lambdaQuery()
                        .eq(DormCheckinDO::getUserId, userId)
                        .eq(DormCheckinDO::getStatus, 1));
        Long roomId = null;
        if (checkin != null) {
            DormBedDO bed = bedMapper.selectById(checkin.getBedId());
            if (bed != null) {
                roomId = bed.getRoomId();
            }
        }
        DormRepairDO repair = new DormRepairDO();
        repair.setUserId(userId);
        repair.setRoomId(roomId);
        repair.setRepairType(dto.getRepairType());
        repair.setContent(dto.getContent());
        repair.setContactPhone(dto.getContactPhone());
        repair.setStatus(0);
        repairMapper.insert(repair);
        log.info("报修提交成功 userId={} repairId={}", userId, repair.getRepairId());
    }

    @Override
    public PageVO<RepairVO> pageRepairs(RepairPageQuery query) {
        // 处理 buildingId 过滤 → 获取该楼栋下所有 roomId
        List<Long> filterRoomIds = null;
        if (query.getBuildingId() != null) {
            filterRoomIds = roomMapper.selectList(
                            Wrappers.<DormRoomDO>lambdaQuery()
                                    .eq(DormRoomDO::getBuildingId, query.getBuildingId()))
                    .stream().map(DormRoomDO::getRoomId).collect(Collectors.toList());
            if (filterRoomIds.isEmpty()) {
                PageVO<RepairVO> empty = new PageVO<>();
                empty.setTotal(0L);
                empty.setPages(0L);
                empty.setList(Collections.emptyList());
                return empty;
            }
        }
        LambdaQueryWrapper<DormRepairDO> wrapper = Wrappers.<DormRepairDO>lambdaQuery()
                .eq(query.getStatus() != null, DormRepairDO::getStatus, query.getStatus())
                .in(filterRoomIds != null, DormRepairDO::getRoomId,
                        filterRoomIds != null ? filterRoomIds : Collections.emptyList())
                .ge(StrUtil.isNotBlank(query.getStartTime()), DormRepairDO::getCreateTime, query.getStartTime())
                .le(StrUtil.isNotBlank(query.getEndTime()), DormRepairDO::getCreateTime, query.getEndTime())
                .orderByDesc(DormRepairDO::getCreateTime);
        Page<DormRepairDO> page = repairMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        PageVO<RepairVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toRepairVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public PageVO<RepairVO> pageMyRepairs(Integer pageNum, Integer pageSize, Integer status) {
        Long userId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        LambdaQueryWrapper<DormRepairDO> wrapper = Wrappers.<DormRepairDO>lambdaQuery()
                .eq(DormRepairDO::getUserId, userId)
                .eq(status != null, DormRepairDO::getStatus, status)
                .orderByDesc(DormRepairDO::getCreateTime);
        Page<DormRepairDO> page = repairMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        PageVO<RepairVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toRepairVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRepair(Long repairId, RepairHandleDTO dto) {
        DormRepairDO repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException(4003, "报修单不存在或当前状态不允许该操作");
        }
        int newStatus = dto.getStatus();
        int currentStatus = repair.getStatus();
        // 状态流转校验：仅允许 0→1 和 1→2
        if (newStatus == 1 && currentStatus != 0) {
            throw new BusinessException(4002, "报修状态非法流转");
        }
        if (newStatus == 2 && currentStatus != 1) {
            throw new BusinessException(4002, "报修状态非法流转");
        }
        // 标记已完成时必须填写处理结果
        if (newStatus == 2 && StrUtil.isBlank(dto.getHandleResult())) {
            throw new BusinessException(400, "标记已完成时必须填写处理结果");
        }
        Long handlerId = cn.dev33.satoken.stp.StpUtil.getLoginIdAsLong();
        DormRepairDO update = new DormRepairDO();
        update.setRepairId(repairId);
        update.setStatus(newStatus);
        if (newStatus == 2) {
            update.setHandleResult(dto.getHandleResult());
            update.setHandleTime(LocalDateTime.now());
            update.setHandlerId(handlerId);
        }
        repairMapper.updateById(update);
        log.info("报修处理成功 repairId={} status={}", repairId, newStatus);
    }

    /**
     * 将报修 DO 转为 VO，含冗余联查字段 + isOverdue 计算
     */
    private RepairVO toRepairVO(DormRepairDO repair) {
        RepairVO vo = new RepairVO();
        vo.setRepairId(repair.getRepairId());
        vo.setUserId(repair.getUserId());
        vo.setRepairType(repair.getRepairType());
        vo.setContent(repair.getContent());
        vo.setContactPhone(repair.getContactPhone());
        vo.setStatus(repair.getStatus());
        vo.setHandleResult(repair.getHandleResult());
        vo.setHandleTime(repair.getHandleTime());
        vo.setCreateTime(repair.getCreateTime());
        // 超时标识：待处理且提交超过 24 小时
        if (repair.getStatus() == 0 && repair.getCreateTime() != null) {
            vo.setIsOverdue(ChronoUnit.HOURS.between(repair.getCreateTime(), LocalDateTime.now()) > 24);
        } else {
            vo.setIsOverdue(false);
        }
        // 学生信息
        SysUserDO student = userMapper.selectById(repair.getUserId());
        if (student != null) {
            vo.setUsername(student.getUsername());
            vo.setRealName(student.getRealName());
        }
        // 房间 + 楼栋信息
        if (repair.getRoomId() != null) {
            DormRoomDO room = roomMapper.selectById(repair.getRoomId());
            if (room != null) {
                vo.setRoomNo(room.getRoomNo());
                DormBuildingDO building = buildingMapper.selectById(room.getBuildingId());
                if (building != null) {
                    vo.setBuildingName(building.getBuildingName());
                }
            }
        }
        // 处理人姓名
        if (repair.getHandlerId() != null) {
            SysUserDO handler = userMapper.selectById(repair.getHandlerId());
            if (handler != null) {
                vo.setHandlerName(handler.getRealName());
            }
        }
        return vo;
    }
}
