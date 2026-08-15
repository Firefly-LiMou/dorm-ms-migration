<template>
  <div class="my-repair-list">
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option
              v-for="item in REPAIR_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card>
      <PageTable
        v-model:page-num="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :data="tableData"
        :total="total"
        :loading="loading"
        :row-class-name="rowClassName"
        @page-change="handlePageChange"
        @size-change="handleSizeChange"
      >
        <el-table-column label="类型" min-width="90" align="center">
          <template #default="{ row }">{{ repairTypeText(row.repairType) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="报修内容" min-width="240" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" min-width="120" />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="repairStatusTagType(row.status)">{{ repairStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="170" />
      </PageTable>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import PageTable from '@/components/common/PageTable.vue'
import { getMyRepairsApi } from '@/api/repair'
import { REPAIR_STATUS_OPTIONS, repairStatusText, repairStatusTagType, repairTypeText } from './repairOptions'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null
})

/** isOverdue 行高亮 */
const rowClassName = ({ row }) => (row.isOverdue ? 'overdue-row' : '')

const loadData = async () => {
  loading.value = true
  try {
    const data = await getMyRepairsApi({ ...queryParams })
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handlePageChange = () => {
  loadData()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.status = null
  handleSearch()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

:deep(.el-table .overdue-row td.el-table__cell) {
  background: #fef0f0;
  box-shadow: inset 3px 0 0 #f56c6c;
}
</style>
