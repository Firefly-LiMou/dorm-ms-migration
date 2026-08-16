package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.annotation.OperationLog;
import com.dorm.common.result.Result;
import com.dorm.entity.dto.BuildingDTO;
import com.dorm.entity.po.DormBuildingDO;
import com.dorm.entity.query.BuildingPageQuery;
import com.dorm.service.BuildingService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * 楼栋管理控制器（admin）
 */
@SaCheckRole("admin")
@Validated
@RestController
@RequestMapping("/building")
public class BuildingController {

    @Resource
    private BuildingService buildingService;

    /** 分页查询楼栋 */
    @GetMapping("/page")
    public Result<?> pageBuildings(@Valid BuildingPageQuery query) {
        return Result.success(buildingService.pageBuildings(query));
    }

    /** 查询楼栋下拉列表 */
    @GetMapping("/all")
    public Result<List<DormBuildingDO>> listAll() {
        return Result.success(buildingService.listAll());
    }

    /** 新增楼栋 */
    @OperationLog(module = "楼栋管理", type = "新增", desc = "新增楼栋")
    @PostMapping
    public Result<Void> addBuilding(@Valid @RequestBody BuildingDTO dto) {
        buildingService.addBuilding(dto);
        return Result.success();
    }

    /** 编辑楼栋 */
    @OperationLog(module = "楼栋管理", type = "修改", desc = "编辑楼栋")
    @PutMapping("/{buildingId}")
    public Result<Void> updateBuilding(@PathVariable Long buildingId, @Valid @RequestBody BuildingDTO dto) {
        buildingService.updateBuilding(buildingId, dto);
        return Result.success();
    }

    /** 删除楼栋 */
    @OperationLog(module = "楼栋管理", type = "删除", desc = "删除楼栋")
    @DeleteMapping("/{buildingId}")
    public Result<Void> deleteBuilding(@PathVariable Long buildingId) {
        buildingService.deleteBuilding(buildingId);
        return Result.success();
    }
}
