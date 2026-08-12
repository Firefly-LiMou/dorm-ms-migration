package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * 楼栋新增/编辑请求参数
 */
@Data
public class BuildingDTO {

    @NotBlank(message = "楼栋编号不能为空")
    @Size(min = 1, max = 20, message = "楼栋编号长度为1-20位")
    private String buildingNo;

    @NotBlank(message = "楼栋名称不能为空")
    @Size(min = 1, max = 50, message = "楼栋名称长度为1-50位")
    private String buildingName;

    @NotNull(message = "总楼层数不能为空")
    @Min(value = 1, message = "总楼层数最小为1")
    @Max(value = 50, message = "总楼层数最大为50")
    private Integer floorCount;

    @Size(max = 20, message = "所属区域长度不超过20位")
    private String area;
}
