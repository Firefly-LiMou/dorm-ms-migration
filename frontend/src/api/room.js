import request from '@/utils/request'

/** 分页查询房间 */
export const pageRoomsApi = (params) => request.get('/room/page', { params })

/** 查询指定楼栋房间下拉列表 */
export const listRoomsApi = (buildingId) => request.get('/room/list', { params: { buildingId } })

/** 新增房间 */
export const addRoomApi = (data) => request.post('/room', data)

/** 编辑房间 */
export const updateRoomApi = (id, data) => request.put(`/room/${id}`, data)

/** 删除房间 */
export const deleteRoomApi = (id) => request.delete(`/room/${id}`)
