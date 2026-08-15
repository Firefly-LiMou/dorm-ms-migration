package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.enums.RepairStatusEnum;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.common.utils.OperationLogContext;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 报修管理服务实现
 */
@Slf4j
@Service
public class RepairServiceImpl implements RepairService {

    /** 入住记录状态：入住中 */
    private static final int CHECKIN_STATUS_ACTIVE = 1;

    /** 报修超时阈值（小时）：待处理超过该时长视为超时 */
    private static final int OVERDUE_HOURS = 24;

    private final DormRepairMapper repairMapper;
    private final DormCheckinMapper checkinMapper;
    private final DormBedMapper bedMapper;
    private final DormRoomMapper roomMapper;
    private final DormBuildingMapper buildingMapper;
    private final SysUserMapper userMapper;

    public RepairServiceImpl(DormRepairMapper repairMapper, DormCheckinMapper checkinMapper,
                             DormBedMapper bedMapper, DormRoomMapper roomMapper,
                             DormBuildingMapper buildingMapper, SysUserMapper userMapper) {
        this.repairMapper = repairMapper;
        this.checkinMapper = checkinMapper;
        this.bedMapper = bedMapper;
        this.roomMapper = roomMapper;
        this.buildingMapper = buildingMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitRepair(RepairSubmitDTO dto, Long userId) {
        DormCheckinDO checkin = checkinMapper.selectOne(
                Wrappers.<DormCheckinDO>lambdaQuery()
                        .eq(DormCheckinDO::getUserId, userId)
                        .eq(DormCheckinDO::getStatus, CHECKIN_STATUS_ACTIVE)
                        .last("LIMIT 1"));
        if (checkin == null) {
            throw new BusinessException(4001, "无有效入住记录，禁止提交报修");
        }
        DormBedDO bed = bedMapper.selectById(checkin.getBedId());
        if (bed == null) {
            throw new BusinessException(4001, "无有效入住记录，禁止提交报修");
        }
        DormRepairDO repair = new DormRepairDO();
        repair.setUserId(userId);
        repair.setRoomId(bed.getRoomId());
        repair.setRepairType(dto.getRepairType());
        repair.setContent(dto.getContent().trim());
        repair.setContactPhone(dto.getContactPhone());
        repair.setStatus(RepairStatusEnum.PENDING.getValue());
        repairMapper.insert(repair);
        log.info("学生提交报修: repairId={} userId={}", repair.getRepairId(), userId);
        return repair.getRepairId();
    }

    @Override
    public PageVO<RepairVO> pageQueryForStudent(Long userId, RepairPageQuery query) {
        LambdaQueryWrapper<DormRepairDO> wrapper = baseWrapper(query)
                .eq(DormRepairDO::getUserId, userId);
        return pageRepairs(wrapper, query);
    }

    @Override
    public PageVO<RepairVO> pageQueryForAdmin(RepairPageQuery query) {
        return pageRepairs(baseWrapper(query), query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleRepair(Long repairId, Integer targetStatus, String handleResult, Long handlerId) {
        DormRepairDO repair = repairMapper.selectById(repairId);
        if (repair == null) {
            throw new BusinessException(4003, "报修单不存在或当前状态不允许该操作");
        }
        Integer currentStatus = repair.getStatus();
        if (RepairStatusEnum.PENDING.getValue().equals(currentStatus)) {
            // 待处理：仅允许流转为处理中
            if (!RepairStatusEnum.PROCESSING.getValue().equals(targetStatus)) {
                throw new BusinessException(4002, "状态非法流转");
            }
        } else if (RepairStatusEnum.PROCESSING.getValue().equals(currentStatus)) {
            // 处理中：仅允许流转为已完成
            if (!RepairStatusEnum.COMPLETED.getValue().equals(targetStatus)) {
                throw new BusinessException(4002, "状态非法流转");
            }
            if (!StringUtils.hasText(handleResult)) {
                throw new BusinessException(400, "处理结果不能为空");
            }
        } else {
            // 已完成：终态，禁止再次流转
            throw new BusinessException(4003, "报修单不存在或当前状态不允许该操作");
        }

        DormRepairDO update = new DormRepairDO();
        update.setRepairId(repairId);
        update.setStatus(targetStatus);
        if (RepairStatusEnum.COMPLETED.getValue().equals(targetStatus)) {
            update.setHandleResult(handleResult.trim());
            update.setHandleTime(LocalDateTime.now());
            update.setHandlerId(handlerId);
        }
        repairMapper.updateById(update);
        OperationLogContext.setChange(buildHandleChange(currentStatus, targetStatus, handleResult));
        log.info("管理员处理报修: repairId={} {}→{}", repairId, currentStatus, targetStatus);
    }

    /** 构造基础查询条件：状态/时间筛选，楼栋筛选通过房间ID集合间接过滤 */
    private LambdaQueryWrapper<DormRepairDO> baseWrapper(RepairPageQuery query) {
        LambdaQueryWrapper<DormRepairDO> wrapper = Wrappers.<DormRepairDO>lambdaQuery()
                .eq(query.getStatus() != null, DormRepairDO::getStatus, query.getStatus())
                .ge(query.getStartTime() != null, DormRepairDO::getCreateTime, query.getStartTime())
                .le(query.getEndTime() != null, DormRepairDO::getCreateTime, query.getEndTime())
                .orderByDesc(DormRepairDO::getCreateTime);
        if (query.getBuildingId() != null) {
            List<Long> roomIds = roomMapper.selectList(
                            Wrappers.<DormRoomDO>lambdaQuery().eq(DormRoomDO::getBuildingId, query.getBuildingId()))
                    .stream().map(DormRoomDO::getRoomId).collect(Collectors.toList());
            if (roomIds.isEmpty()) {
                // 该楼栋下无房间，返回空结果
                wrapper.apply("1 = 0");
            } else {
                wrapper.in(DormRepairDO::getRoomId, roomIds);
            }
        }
        return wrapper;
    }

    private PageVO<RepairVO> pageRepairs(LambdaQueryWrapper<DormRepairDO> wrapper, RepairPageQuery query) {
        Page<DormRepairDO> page = repairMapper.selectPage(new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        List<RepairVO> list = buildVOList(page.getRecords());
        PageVO<RepairVO> result = new PageVO<>();
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setList(list);
        return result;
    }

    /** Service 层组装联查结果：批量查询用户/房间/楼栋/处理人，内存拼装，避免多表复杂 SQL */
    private List<RepairVO> buildVOList(List<DormRepairDO> records) {
        if (records.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> userIds = records.stream().map(DormRepairDO::getUserId).collect(Collectors.toSet());
        Set<Long> roomIds = records.stream().map(DormRepairDO::getRoomId).collect(Collectors.toSet());
        Set<Long> handlerIds = records.stream().map(DormRepairDO::getHandlerId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        Map<Long, SysUserDO> userMap = userIds.isEmpty() ? Collections.emptyMap()
                : toMap(userMapper.selectBatchIds(userIds), SysUserDO::getUserId);
        Map<Long, DormRoomDO> roomMap = roomIds.isEmpty() ? Collections.emptyMap()
                : toMap(roomMapper.selectBatchIds(roomIds), DormRoomDO::getRoomId);
        Set<Long> buildingIds = roomMap.values().stream().map(DormRoomDO::getBuildingId).collect(Collectors.toSet());
        Map<Long, DormBuildingDO> buildingMap = buildingIds.isEmpty() ? Collections.emptyMap()
                : toMap(buildingMapper.selectBatchIds(buildingIds), DormBuildingDO::getBuildingId);
        Map<Long, SysUserDO> handlerMap = handlerIds.isEmpty() ? Collections.emptyMap()
                : toMap(userMapper.selectBatchIds(handlerIds), SysUserDO::getUserId);

        return records.stream()
                .map(repair -> convert(repair, userMap, roomMap, buildingMap, handlerMap))
                .collect(Collectors.toList());
    }

    private <T> Map<Long, T> toMap(List<T> list, Function<T, Long> keyGetter) {
        return list.stream().collect(Collectors.toMap(keyGetter, Function.identity()));
    }

    private RepairVO convert(DormRepairDO repair, Map<Long, SysUserDO> userMap,
                             Map<Long, DormRoomDO> roomMap, Map<Long, DormBuildingDO> buildingMap,
                             Map<Long, SysUserDO> handlerMap) {
        RepairVO vo = new RepairVO();
        vo.setRepairId(repair.getRepairId());
        vo.setUserId(repair.getUserId());
        SysUserDO user = userMap.get(repair.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setRealName(user.getRealName());
        }
        DormRoomDO room = roomMap.get(repair.getRoomId());
        if (room != null) {
            vo.setRoomNo(room.getRoomNo());
            DormBuildingDO building = buildingMap.get(room.getBuildingId());
            if (building != null) {
                vo.setBuildingName(building.getBuildingName());
            }
        }
        vo.setRepairType(repair.getRepairType());
        vo.setContent(repair.getContent());
        vo.setContactPhone(repair.getContactPhone());
        vo.setStatus(repair.getStatus());
        vo.setIsOverdue(calcOverdue(repair));
        vo.setHandleResult(repair.getHandleResult());
        vo.setHandleTime(repair.getHandleTime());
        SysUserDO handler = handlerMap.get(repair.getHandlerId());
        if (handler != null) {
            vo.setHandlerName(handler.getRealName());
        }
        vo.setCreateTime(repair.getCreateTime());
        return vo;
    }

    /** 计算超时标识：status=0 且提交时间超过 24 小时为 true */
    private boolean calcOverdue(DormRepairDO repair) {
        return RepairStatusEnum.PENDING.getValue().equals(repair.getStatus())
                && repair.getCreateTime() != null
                && repair.getCreateTime().isBefore(LocalDateTime.now().minusHours(OVERDUE_HOURS));
    }

    /** 构造处理报修的变更描述（状态流转 + 处理结果） */
    private String buildHandleChange(Integer from, Integer to, String handleResult) {
        StringBuilder sb = new StringBuilder();
        sb.append("状态: ").append(RepairStatusEnum.descOf(from))
                .append("→").append(RepairStatusEnum.descOf(to));
        if (StringUtils.hasText(handleResult)) {
            sb.append(", 处理结果: ").append(handleResult.trim());
        }
        return sb.toString();
    }
}
