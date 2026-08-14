import request from '@/utils/request'

/** 登录 */
export const loginApi = (data) => request.post('/auth/login', data)

/** 登出 */
export const logoutApi = () => request.post('/auth/logout')

/** 获取当前登录用户信息 */
export const getProfileApi = () => request.get('/user/profile')

/** 分页查询学生账号（管理员） */
export const getUserPageApi = (params) => request.get('/user/page', { params })

/** 创建学生账号（管理员） */
export const createUserApi = (data) => request.post('/user', data)

/** 编辑学生账号（管理员） */
export const updateUserApi = (userId, data) => request.put(`/user/${userId}`, data)

/** 重置学生密码（管理员） */
export const resetUserPasswordApi = (userId) => request.put(`/user/${userId}/password/reset`)
