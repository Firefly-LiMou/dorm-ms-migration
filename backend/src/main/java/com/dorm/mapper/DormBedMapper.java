package com.dorm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dorm.entity.po.DormBedDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 床位信息 Mapper
 */
public interface DormBedMapper extends BaseMapper<DormBedDO> {

    /** 行级锁查询床位，防止并发重复分配 */
    @Select("SELECT * FROM dorm_bed WHERE bed_id = #{bedId} FOR UPDATE")
    DormBedDO selectForUpdate(@Param("bedId") Long bedId);
}
