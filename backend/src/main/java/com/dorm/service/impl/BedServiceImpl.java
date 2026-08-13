package com.dorm.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.BedBatchDTO;
import com.dorm.entity.dto.BedStatusDTO;
import com.dorm.entity.po.DormBedDO;
import com.dorm.entity.query.BedPageQuery;
import com.dorm.mapper.DormBedMapper;
import com.dorm.service.BedService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 床位服务实现
 */
@Service
public class BedServiceImpl implements BedService {

    @Resource
    private DormBedMapper bedMapper;

    @Override
    public PageVO<DormBedDO> pageBeds(BedPageQuery query) {
        Page<DormBedDO> page = bedMapper.selectPage(
                new Page<>(query.getPageNum(), query.getPageSize()),
                Wrappers.<DormBedDO>lambdaQuery()
                        .eq(query.getRoomId() != null, DormBedDO::getRoomId, query.getRoomId())
                        .eq(query.getStatus() != null, DormBedDO::getStatus, query.getStatus())
                        .orderByAsc(DormBedDO::getRoomId, DormBedDO::getBedNo));
        return PageVO.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreate(BedBatchDTO dto) {
        Long existCount = bedMapper.selectCount(
                Wrappers.<DormBedDO>lambdaQuery().eq(DormBedDO::getRoomId, dto.getRoomId()));
        if (existCount > 0) {
            throw new BusinessException(3004, "房间已存在床位，禁止重复初始化");
        }
        for (int i = 1; i <= dto.getCount(); i++) {
            DormBedDO bed = new DormBedDO();
            bed.setRoomId(dto.getRoomId());
            bed.setBedNo(String.valueOf(i));
            bed.setStatus(0);
            bedMapper.insert(bed);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long bedId, BedStatusDTO dto) {
        DormBedDO bed = bedMapper.selectById(bedId);
        if (bed == null) {
            throw new BusinessException(5001, "数据不存在");
        }
        DormBedDO update = new DormBedDO();
        update.setBedId(bedId);
        update.setStatus(dto.getStatus());
        bedMapper.updateById(update);
    }
}
