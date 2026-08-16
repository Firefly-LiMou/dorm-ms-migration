package com.dorm.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.CheckinDTO;
import com.dorm.entity.dto.CheckoutDTO;
import com.dorm.entity.po.DormBedDO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.po.DormCheckinDO;
import com.dorm.entity.po.DormRoomDO;
import com.dorm.entity.po.SysUserDO;
import com.dorm.entity.query.CheckinPageQuery;
import com.dorm.entity.vo.CheckinVO;
import com.dorm.mapper.DormBedMapper;
import com.dorm.mapper.DormBuildingMapper;
import com.dorm.mapper.DormCheckinMapper;
import com.dorm.mapper.DormRoomMapper;
import com.dorm.mapper.SysUserMapper;
import com.dorm.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 入住管理服务实现
 */
@Slf4j
@Service
public class CheckinServiceImpl implements CheckinService {

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
    public void checkin(CheckinDTO dto) {
        // 校验学生账号存在且为 student 角色
        SysUserDO student = userMapper.selectById(dto.getUserId());
        if (student == null || !"student".equals(student.getRole())) {
            throw new BusinessException(3001, "学生账号不存在");
        }
        // 校验学生无有效入住记录
        Long activeCheckin = checkinMapper.selectCount(
                Wrappers.<DormCheckinDO>lambdaQuery()
                        .eq(DormCheckinDO::getUserId, dto.getUserId())
                        .eq(DormCheckinDO::getStatus, 1));
        if (activeCheckin > 0) {
            throw new BusinessException(3002, "学生已有有效入住记录");
        }
        // 锁定床位行，校验空闲
        DormBedDO bed = bedMapper.selectForUpdate(dto.getBedId());
        if (bed == null || bed.getStatus() != 0) {
            throw new BusinessException(3003, "床位不存在或已被占用");
        }
        // 更新床位为已入住
        DormBedDO bedUpdate = new DormBedDO();
        bedUpdate.setBedId(dto.getBedId());
        bedUpdate.setStatus(1);
        bedMapper.updateById(bedUpdate);
        // 插入入住记录
        Long operatorId = StpUtil.getLoginIdAsLong();
        DormCheckinDO checkin = new DormCheckinDO();
        checkin.setUserId(dto.getUserId());
        checkin.setBedId(dto.getBedId());
        checkin.setCheckinTime(LocalDateTime.now());
        checkin.setStatus(1);
        checkin.setOperatorId(operatorId);
        checkin.setRemark(dto.getRemark());
        checkinMapper.insert(checkin);
        log.info("入住分配成功 userId={} bedId={}", dto.getUserId(), dto.getBedId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkout(Long checkinId, CheckoutDTO dto) {
        DormCheckinDO checkin = checkinMapper.selectById(checkinId);
        if (checkin == null || checkin.getStatus() != 1) {
            throw new BusinessException(4003, "入住记录不存在或已退宿");
        }
        // 更新入住记录
        DormCheckinDO update = new DormCheckinDO();
        update.setCheckinId(checkinId);
        update.setStatus(2);
        update.setCheckoutTime(LocalDateTime.now());
        update.setRemark(dto.getRemark());
        checkinMapper.updateById(update);
        // 释放床位
        DormBedDO bedUpdate = new DormBedDO();
        bedUpdate.setBedId(checkin.getBedId());
        bedUpdate.setStatus(0);
        bedMapper.updateById(bedUpdate);
        log.info("退宿成功 checkinId={} bedId={}", checkinId, checkin.getBedId());
    }

    @Override
    public PageVO<CheckinVO> pageCheckins(CheckinPageQuery query) {
        // 处理学号过滤 → userId
        Long filterUserId = null;
        if (StrUtil.isNotBlank(query.getUsername())) {
            SysUserDO user = userMapper.selectOne(
                    Wrappers.<SysUserDO>lambdaQuery().eq(SysUserDO::getUsername, query.getUsername()));
            if (user == null) {
                return emptyPage();
            }
            filterUserId = user.getUserId();
        }
        // 处理楼栋过滤 → bedId 集合
        Set<Long> filterBedIds = null;
        if (query.getBuildingId() != null) {
            List<Long> roomIds = roomMapper.selectList(
                            Wrappers.<DormRoomDO>lambdaQuery()
                                    .eq(DormRoomDO::getBuildingId, query.getBuildingId()))
                    .stream().map(DormRoomDO::getRoomId).collect(Collectors.toList());
            if (roomIds.isEmpty()) {
                return emptyPage();
            }
            filterBedIds = bedMapper.selectList(
                            Wrappers.<DormBedDO>lambdaQuery().in(DormBedDO::getRoomId, roomIds))
                    .stream().map(DormBedDO::getBedId).collect(Collectors.toSet());
            if (filterBedIds.isEmpty()) {
                return emptyPage();
            }
        }
        LambdaQueryWrapper<DormCheckinDO> wrapper = Wrappers.<DormCheckinDO>lambdaQuery()
                .eq(query.getStatus() != null, DormCheckinDO::getStatus, query.getStatus())
                .eq(filterUserId != null, DormCheckinDO::getUserId, filterUserId)
                .in(filterBedIds != null && !filterBedIds.isEmpty(), DormCheckinDO::getBedId,
                        filterBedIds != null ? filterBedIds : Collections.emptyList())
                .orderByDesc(DormCheckinDO::getCreateTime);
        Page<DormCheckinDO> page = checkinMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()), wrapper);
        PageVO<CheckinVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toCheckinVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    public PageVO<CheckinVO> pageMyCheckins(Integer pageNum, Integer pageSize) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<DormCheckinDO> page = checkinMapper.selectPage(
                new Page<>(pageNum, pageSize),
                Wrappers.<DormCheckinDO>lambdaQuery()
                        .eq(DormCheckinDO::getUserId, userId)
                        .orderByDesc(DormCheckinDO::getCheckinTime));
        PageVO<CheckinVO> vo = new PageVO<>();
        vo.setTotal(page.getTotal());
        vo.setPages(page.getPages());
        vo.setList(page.getRecords().stream().map(this::toCheckinVO).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCheckin(Long checkinId) {
        DormCheckinDO checkin = checkinMapper.selectById(checkinId);
        if (checkin == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        if (checkin.getStatus() != 2) {
            throw new BusinessException(4004, "入住记录未退宿，禁止删除");
        }
        checkinMapper.deleteById(checkinId);
        log.info("删除入住记录成功 checkinId={}", checkinId);
    }

    private PageVO<CheckinVO> emptyPage() {
        PageVO<CheckinVO> vo = new PageVO<>();
        vo.setTotal(0L);
        vo.setPages(0L);
        vo.setList(Collections.emptyList());
        return vo;
    }

    private CheckinVO toCheckinVO(DormCheckinDO checkin) {
        CheckinVO vo = new CheckinVO();
        vo.setCheckinId(checkin.getCheckinId());
        vo.setUserId(checkin.getUserId());
        vo.setCheckinTime(checkin.getCheckinTime());
        vo.setCheckoutTime(checkin.getCheckoutTime());
        vo.setStatus(checkin.getStatus());
        vo.setRemark(checkin.getRemark());
        SysUserDO student = userMapper.selectById(checkin.getUserId());
        if (student != null) {
            vo.setUsername(student.getUsername());
            vo.setRealName(student.getRealName());
        }
        DormBedDO bed = bedMapper.selectById(checkin.getBedId());
        if (bed != null) {
            vo.setBedNo(bed.getBedNo());
            DormRoomDO room = roomMapper.selectById(bed.getRoomId());
            if (room != null) {
                vo.setRoomNo(room.getRoomNo());
                DormBuildingDO building = buildingMapper.selectById(room.getBuildingId());
                if (building != null) {
                    vo.setBuildingName(building.getBuildingName());
                }
            }
        }
        SysUserDO operator = userMapper.selectById(checkin.getOperatorId());
        if (operator != null) {
            vo.setOperatorName(operator.getRealName());
        }
        return vo;
    }
}
