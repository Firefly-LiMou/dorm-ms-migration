import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 全局唯一 axios 实例，所有接口必须通过本实例发起
const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器：统一携带 Token（请求头 satoken，与后端 Sa-Token 约定一致）
request.interceptors.request.use((config) => {
  const userStore = useUserStore()
  if (userStore.token) {
    config.headers['satoken'] = userStore.token
  }
  return config
})

// 响应拦截器：统一解析后端 Result 结构，成功直接返回 data，通用错误码集中处理
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.msg || '请求失败')
    return Promise.reject(new Error(res.msg || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      // Token 失效：清空登录态并跳转登录页
      const userStore = useUserStore()
      userStore.reset()
      ElMessage.error('登录已过期，请重新登录')
      router.push({ path: '/login', query: { redirect: router.currentRoute.value.fullPath } })
    } else if (status === 403) {
      ElMessage.error('无权限访问')
    } else {
      ElMessage.error('网络异常，请稍后重试')
    }
    return Promise.reject(error)
  }
)

export default request
