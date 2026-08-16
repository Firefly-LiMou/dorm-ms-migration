import request from '@/utils/request'

/** 管理员分页查询学生账号（入住分配选人） */
export const pageStudentsApi = (params) => request.get('/user/page', { params })

/** 入住分配 */
export const checkinApi = (data) => request.post('/checkin', data)

/** 办理退宿 */
export const checkoutApi = (checkinId, data) => request.post(`/checkin/${checkinId}/checkout`, data)

/** 管理员分页查询入住记录 */
export const pageCheckinsApi = (params) => request.get('/checkin/page', { params })

/** 学生查询本人入住记录 */
export const myCheckinsApi = (params) => request.get('/checkin/my', { params })

/** 删除入住记录（仅已退宿） */
export const deleteCheckinApi = (id) => request.delete(`/checkin/${id}`)
