import request from '@/utils/request'

/** 学生提交报修 */
export const submitRepairApi = (data) => request.post('/repair', data)

/** 学生分页查询本人报修 */
export const getMyRepairsApi = (params) => request.get('/repair/my', { params })

/** 管理员分页查询全部报修 */
export const getRepairPageApi = (params) => request.get('/repair/page', { params })

/** 管理员处理报修 */
export const handleRepairApi = (repairId, data) => request.put(`/repair/${repairId}/handle`, data)
