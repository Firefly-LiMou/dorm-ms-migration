/**
 * 报修管理模块路由：学生提交/查询本人报修，管理员全局管理
 */
export default [
  {
    path: 'repair/submit',
    name: 'RepairSubmit',
    component: () => import('@/views/repair/RepairSubmit.vue'),
    meta: { title: '提交报修', roles: ['student'] }
  },
  {
    path: 'repair/my',
    name: 'MyRepairList',
    component: () => import('@/views/repair/MyRepairList.vue'),
    meta: { title: '我的报修', roles: ['student'] }
  },
  {
    path: 'repair/manage',
    name: 'RepairManage',
    component: () => import('@/views/repair/RepairManage.vue'),
    meta: { title: '报修管理', roles: ['admin'] }
  }
]
