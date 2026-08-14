import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

/**
 * 路由配置：meta.public 为公开路由（免登录）；meta.roles 标注可访问角色
 * 业务路由由各模块开发者在 children 中追加，自动生成菜单与权限拦截
 */
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/views/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页', roles: ['student', 'admin'] }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/user/UserManage.vue'),
        meta: { title: '学生账号管理', roles: ['admin'] }
      },
      {
        path: 'log',
        name: 'OperationLog',
        component: () => import('@/views/log/OperationLog.vue'),
        meta: { title: '操作日志', roles: ['admin'] }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue'),
    meta: { public: true, title: '无权限' }
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { public: true, title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫：白名单校验 → 登录态校验 → 角色权限校验
router.beforeEach((to) => {
  const userStore = useUserStore()

  // 公开路由直接放行
  if (to.meta.public) {
    return true
  }

  // 未登录强制跳转登录页，携带原始路径便于登录后回跳
  if (!userStore.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }

  // 角色不在允许范围内跳转 403
  if (to.meta.roles && !to.meta.roles.includes(userStore.role)) {
    return { path: '/403' }
  }

  return true
})

router.afterEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} - 高校公寓管理系统` : '高校公寓管理系统'
})

export default router
