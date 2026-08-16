<template>
  <div class="dashboard">
    <el-card class="welcome">
      <h2>欢迎使用高校公寓管理系统</h2>
      <p>当前登录：{{ userStore.realName }}（{{ userStore.role === 'admin' ? '系统管理员' : '学生' }}）</p>
    </el-card>

    <el-divider content-position="left">快捷入口</el-divider>

    <el-row :gutter="16">
      <el-col v-for="item in navItems" :key="item.path" :span="6">
        <el-card class="nav-card" shadow="hover" @click="go(item.path)">
          <div class="nav-title">{{ item.title }}</div>
          <div class="nav-desc">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

// 按角色动态生成导航卡片，点击跳转对应页面
const navItems = computed(() => {
  if (userStore.role === 'admin') {
    return [
      { path: '/dorm/building', title: '楼栋管理', desc: '维护楼栋基础信息' },
      { path: '/dorm/room', title: '房间管理', desc: '维护房间与楼层信息' },
      { path: '/dorm/bed', title: '床位管理', desc: '批量初始化与床位维护' },
      { path: '/dorm/checkin', title: '入住管理', desc: '入住分配与退宿办理' }
    ]
  }
  return [
    { path: '/dorm/my-checkin', title: '我的住宿', desc: '查看当前住宿与历史记录' }
  ]
})

const go = (path) => {
  router.push(path)
}
</script>

<style scoped>
.welcome {
  margin-bottom: 8px;
}

.nav-card {
  cursor: pointer;
  text-align: center;
  padding: 12px 0;
}

.nav-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 8px;
}

.nav-desc {
  color: #909399;
  font-size: 13px;
}
</style>
