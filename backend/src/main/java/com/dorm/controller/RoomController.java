package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.enums.OperationType;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.RoomDTO;
import com.dorm.entity.po.DormRoomDO;
import com.dorm.entity.query.RoomPageQuery;
import com.dorm.service.RoomService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 房间管理控制器（admin）
 */
@SaCheckRole("admin")
@Validated
@RestController
@RequestMapping("/room")
public class RoomController {

    @Resource
    private RoomService roomService;

    /** 分页查询房间 */
    @GetMapping("/page")
    public Result<?> pageRooms(@Valid RoomPageQuery query) {
        return Result.success(roomService.pageRooms(query));
    }

    /** 查询房间下拉列表 */
    @GetMapping("/list")
    public Result<List<DormRoomDO>> listRooms(@RequestParam @NotNull(message = "楼栋ID不能为空") Long buildingId) {
        return Result.success(roomService.listByBuilding(buildingId));
    }

    /** 新增房间 */
    @OperationLog(module = "房间管理", type = OperationType.ADD, desc = "新增房间")
    @PostMapping
    public Result<Void> addRoom(@Valid @RequestBody RoomDTO dto) {
        roomService.addRoom(dto);
        return Result.success();
    }

    /** 编辑房间 */
    @OperationLog(module = "房间管理", type = OperationType.UPDATE, desc = "编辑房间")
    @PutMapping("/{roomId}")
    public Result<Void> updateRoom(@PathVariable Long roomId, @Valid @RequestBody RoomDTO dto) {
        roomService.updateRoom(roomId, dto);
        return Result.success();
    }

    /** 删除房间 */
    @OperationLog(module = "房间管理", type = OperationType.DELETE, desc = "删除房间")
    @DeleteMapping("/{roomId}")
    public Result<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return Result.success();
    }
}
