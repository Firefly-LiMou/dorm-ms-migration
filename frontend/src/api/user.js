import request from '@/utils/request'

/** 登录 */
export const loginApi = (data) => request.post('/auth/login', data)

/** 登出 */
export const logoutApi = () => request.post('/auth/logout')

/** 获取当前登录用户信息 */
export const getProfileApi = () => request.get('/user/profile')
