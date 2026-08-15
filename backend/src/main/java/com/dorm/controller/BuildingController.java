package com.dorm.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.dorm.common.result.Result;
import com.dorm.entity.vo.BuildingVO;
import com.dorm.service.BuildingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 楼栋管理模块：下拉列表（管理员）
 */
@RestController
@RequestMapping("/building")
public class BuildingController {

    private final BuildingService buildingService;

    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    /**
     * 查询楼栋下拉列表（管理员）：供报修筛选、入住分配等场景下拉选择
     */
    @SaCheckRole("admin")
    @GetMapping("/all")
    public Result<List<BuildingVO>> listAll() {
        return Result.success(buildingService.listAll());
    }
}
