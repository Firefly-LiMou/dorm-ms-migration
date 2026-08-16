package com.dorm.service;

import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.RoomDTO;
import com.dorm.entity.po.DormRoomDO;
import com.dorm.entity.query.RoomPageQuery;

import java.util.List;

/**
 * 房间服务接口
 */
public interface RoomService {

    /** 分页查询房间 */
    PageVO<DormRoomDO> pageRooms(RoomPageQuery query);

    /** 查询指定楼栋全部房间下拉列表 */
    List<DormRoomDO> listByBuilding(Long buildingId);

    /** 新增房间 */
    void addRoom(RoomDTO dto);

    /** 编辑房间 */
    void updateRoom(Long roomId, RoomDTO dto);

    /** 删除房间（校验下级床位） */
    void deleteRoom(Long roomId);
}
