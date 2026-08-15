import request from '@/utils/request'

/** 查询楼栋下拉列表（管理员） */
export const getBuildingAllApi = () => request.get('/building/all')
