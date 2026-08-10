import { defineStore } from 'pinia'
import { loginApi, logoutApi, getProfileApi } from '@/api/user'

/**
 * 用户状态：Token 与用户信息，开启持久化（刷新页面不丢失）
 */
export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null
  }),

  getters: {
    role: (state) => state.userInfo?.role || '',
    realName: (state) => state.userInfo?.realName || ''
  },

  actions: {
    /** 登录：调用接口，保存 Token 与用户信息 */
    async login(loginForm) {
      const data = await loginApi(loginForm)
      this.token = data.token
      this.userInfo = data.userInfo
    },

    /** 登出：调用接口并清空本地登录态 */
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.reset()
      }
    },

    /** 重新拉取当前用户信息 */
    async fetchProfile() {
      this.userInfo = await getProfileApi()
    },

    /** 清空登录态 */
    reset() {
      this.token = ''
      this.userInfo = null
    }
  },

  persist: {
    key: 'dorm-user'
  }
})
