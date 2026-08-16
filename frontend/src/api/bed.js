import request from '@/utils/request'

/** 分页查询床位 */
export const pageBedsApi = (params) => request.get('/bed/page', { params })

/** 批量初始化房间床位 */
export const batchCreateBedsApi = (data) => request.post('/bed/batch', data)

/** 手动更新床位状态 */
export const updateBedStatusApi = (id, data) => request.put(`/bed/${id}/status`, data)

/** 删除床位（仅空闲） */
export const deleteBedApi = (id) => request.delete(`/bed/${id}`)
