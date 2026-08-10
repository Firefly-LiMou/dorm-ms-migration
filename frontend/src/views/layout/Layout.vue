<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="layout-aside">
      <div class="logo">公寓管理系统</div>
      <el-menu :default-active="activeMenu" router background-color="#304156" text-color="#bfcbd9" active-text-color="#409eff">
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <span>{{ item.meta?.title || item.path }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <span class="header-title">{{ route.meta.title }}</span>
        <el-dropdown @command="handleCommand">
          <span class="user-info">
            {{ userStore.realName || userStore.userInfo?.username }}
            <el-tag size="small" :type="userStore.role === 'admin' ? 'danger' : 'primary'" class="role-tag">
              {{ userStore.role === 'admin' ? '管理员' : '学生' }}
            </el-tag>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 菜单由路由表生成：按当前角色过滤 meta.roles，业务路由追加后菜单自动出现
const menuItems = computed(() => {
  const layoutRoute = router.options.routes.find((r) => r.path === '/')
  const children = layoutRoute?.children || []
  return children.filter((item) => !item.meta?.hidden && item.meta?.roles?.includes(userStore.role))
})

const activeMenu = computed(() => route.path)

const handleCommand = async (command) => {
  if (command === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    await userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.layout-aside {
  background-color: #304156;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: bold;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6e6e6;
}

.user-info {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
}

.role-tag {
  margin-left: 4px;
}

.layout-main {
  background-color: #f0f2f5;
}
</style>
