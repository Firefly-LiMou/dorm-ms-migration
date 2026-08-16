package com.dorm.entity.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 房间新增/编辑请求参数
 */
@Data
public class RoomDTO {

    @NotNull(message = "所属楼栋不能为空")
    private Long buildingId;

    @NotBlank(message = "房间编号不能为空")
    @Size(min = 1, max = 20, message = "房间编号长度为1-20位")
    private String roomNo;

    @NotNull(message = "所在楼层不能为空")
    @Min(value = 1, message = "楼层最小为1")
    @Max(value = 50, message = "楼层最大为50")
    private Integer floor;

    @NotNull(message = "床位数不能为空")
    @Min(value = 1, message = "床位数最小为1")
    @Max(value = 12, message = "床位数最大为12")
    private Integer bedCount;

    @Size(max = 20, message = "房间类型长度不超过20位")
    private String roomType;
}
