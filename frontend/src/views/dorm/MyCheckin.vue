<template>
  <div class="page">
    <el-card>
      <div class="current" v-if="current">
        <h3>当前住宿</h3>
        <el-descriptions :column="3" border>
          <el-descriptions-item label="楼栋">{{ current.buildingName }}</el-descriptions-item>
          <el-descriptions-item label="房间">{{ current.roomNo }}</el-descriptions-item>
          <el-descriptions-item label="床位">{{ current.bedNo }}号床</el-descriptions-item>
          <el-descriptions-item label="入住时间">{{ current.checkinTime }}</el-descriptions-item>
          <el-descriptions-item label="办理人">{{ current.operatorName }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ current.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-empty v-else-if="!loading" description="当前无住宿记录" />
    </el-card>

    <el-card class="history">
      <template #header>历史入住记录</template>
      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="buildingName" label="楼栋" width="140" />
        <el-table-column prop="roomNo" label="房间" width="90" />
        <el-table-column prop="bedNo" label="床位" width="80" />
        <el-table-column prop="checkinTime" label="入住时间" width="170" />
        <el-table-column prop="checkoutTime" label="退宿时间" width="170" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '入住中' : '已退宿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="办理人" width="100" />
        <template #empty>
          <el-empty description="暂无记录" />
        </template>
      </el-table>
      <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="pageSize"
        :current-page="pageNum"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { myCheckinsApi } from '@/api/checkin'
import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(DEFAULT_PAGE_SIZE)

const current = computed(() => list.value.find((item) => item.status === 1) || null)

const loadData = async () => {
  loading.value = true
  try {
    const data = await myCheckinsApi({ pageNum: pageNum.value, pageSize: pageSize.value })
    list.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 已统一提示
  } finally {
    loading.value = false
  }
}

const handlePageChange = (page) => {
  pageNum.value = page
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.current {
  margin-bottom: 8px;
}

.history {
  margin-top: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
