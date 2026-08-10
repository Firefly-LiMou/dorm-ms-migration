<template>
  <el-card>
    <h2>欢迎使用高校公寓管理系统</h2>
    <p>当前登录：{{ userStore.realName }}（{{ userStore.role === 'admin' ? '系统管理员' : '学生' }}）</p>
    <el-divider />
    <p>本项目为团队协作练手项目，核心模块：用户认证、宿舍入住管理、报修管理。</p>
  </el-card>
</template>

<script setup>
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
</script>
