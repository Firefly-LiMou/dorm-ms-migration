import request from '@/utils/request'

/** 分页查询楼栋 */
export const pageBuildingsApi = (params) => request.get('/building/page', { params })

/** 查询全部楼栋下拉列表 */
export const listBuildingsApi = () => request.get('/building/all')

/** 新增楼栋 */
export const addBuildingApi = (data) => request.post('/building', data)

/** 编辑楼栋 */
export const updateBuildingApi = (id, data) => request.put(`/building/${id}`, data)

/** 删除楼栋 */
export const deleteBuildingApi = (id) => request.delete(`/building/${id}`)
