package com.dorm.service;

import com.dorm.common.exception.BusinessException;
import com.dorm.common.page.PageVO;
import com.dorm.entity.dto.RepairSubmitDTO;
import com.dorm.entity.query.RepairPageQuery;
import com.dorm.entity.vo.RepairVO;

/**
 * 报修管理服务
 */
public interface RepairService {

    /**
     * 学生提交报修：校验有效入住记录 → 自动关联房间 → 保存报修单
     *
     * @param dto    提交入参
     * @param userId 当前学生ID
     * @return 报修单ID
     * @throws BusinessException 4001 无有效入住记录，禁止提交报修
     */
    Long submitRepair(RepairSubmitDTO dto, Long userId);

    /**
     * 学生分页查询本人报修
     *
     * @param userId 当前学生ID
     * @param query  查询条件
     * @return 分页结果（含 isOverdue 超时标识）
     */
    PageVO<RepairVO> pageQueryForStudent(Long userId, RepairPageQuery query);

    /**
     * 管理员分页查询全部报修
     *
     * @param query 查询条件
     * @return 分页结果（含 isOverdue 超时标识）
     */
    PageVO<RepairVO> pageQueryForAdmin(RepairPageQuery query);

    /**
     * 管理员处理报修：状态流转校验 + 回填处理人/处理时间
     *
     * @param repairId     报修单ID
     * @param targetStatus 流转目标状态（1-处理中 / 2-已完成）
     * @param handleResult 处理结果（流转为已完成时必填）
     * @param handlerId    当前管理员ID
     * @throws BusinessException 4002 状态非法流转 / 4003 报修单不存在或当前状态不允许该操作
     */
    void handleRepair(Long repairId, Integer targetStatus, String handleResult, Long handlerId);
}
