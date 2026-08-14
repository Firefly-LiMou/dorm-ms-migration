<template>
  <div class="operation-log">
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="操作人">
          <el-input
            v-model="queryParams.operatorName"
            placeholder="按操作人查询"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryParams.operationType" placeholder="全部" clearable style="width: 120px">
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 列表 -->
    <el-card>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="logId" label="日志ID" width="80" align="center" />
        <el-table-column prop="operatorName" label="操作人" min-width="100" />
        <el-table-column prop="operationType" label="操作类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="typeTagType(row.operationType)">{{ row.operationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="操作详情" min-width="320" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" min-width="120">
          <template #default="{ row }">{{ row.ipAddress || '-' }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" min-width="170" />
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogPageApi } from '@/api/log'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const timeRange = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  operatorName: '',
  operationType: ''
})

const typeTagType = (type) => {
  if (type === '新增') return 'success'
  if (type === '删除') return 'danger'
  return 'warning'
}

const buildParams = () => {
  const [startTime, endTime] = timeRange.value || []
  return {
    ...queryParams,
    startTime: startTime || null,
    endTime: endTime || null
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await getLogPageApi(buildParams())
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

/** 页码变化：保留当前页码直接加载，避免被重置回第 1 页 */
const handlePageChange = () => {
  loadData()
}

/** 每页条数变化：重置回第 1 页 */
const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.operatorName = ''
  queryParams.operationType = ''
  timeRange.value = []
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
</style>
