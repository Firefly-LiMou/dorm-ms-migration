<template>
  <div class="repair-manage">
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
            <el-option
              v-for="item in REPAIR_STATUS_OPTIONS"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="queryParams.buildingId" placeholder="全部" clearable style="width: 160px">
            <el-option
              v-for="building in buildingList"
              :key="building.buildingId"
              :label="building.buildingName"
              :value="building.buildingId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-date-picker
            v-model="timeRange"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 340px"
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
        <el-table-column label="学生" min-width="140">
          <template #default="{ row }">{{ row.realName }}（{{ row.username }}）</template>
        </el-table-column>
        <el-table-column label="楼栋-房间" min-width="140">
          <template #default="{ row }">{{ row.buildingName }} - {{ row.roomNo }}</template>
        </el-table-column>
        <el-table-column label="类型" min-width="90" align="center">
          <template #default="{ row }">{{ repairTypeText(row.repairType) }}</template>
        </el-table-column>
        <el-table-column prop="content" label="报修内容" min-width="200" show-overflow-tooltip />
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="repairStatusTagType(row.status)">{{ repairStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" min-width="170" />
        <el-table-column label="操作" width="80" align="center">
          <template #default="{ row }">
            <el-button link type="primary" :disabled="row.status === 2" @click="openHandleDialog(row)">处理</el-button>
          </template>
        </el-table-column>
      </PageTable>
    </el-card>

    <!-- 处理弹窗 -->
    <FormDialog
      v-model:visible="dialogVisible"
      title="处理报修"
      width="480px"
      :loading="saving"
      @confirm="handleConfirm"
    >
      <el-form ref="handleFormRef" :model="handleForm" :rules="handleRules" label-width="80px">
        <el-form-item label="当前状态">
          <el-tag :type="repairStatusTagType(currentRow?.status)">
            {{ repairStatusText(currentRow?.status) }}
          </el-tag>
        </el-form-item>
        <el-form-item label="目标状态" prop="status">
          <el-select v-model="handleForm.status" placeholder="请选择目标状态" style="width: 100%">
            <el-option v-for="item in targetOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="handleForm.status === 2" label="处理结果" prop="handleResult">
          <el-input
            v-model="handleForm.handleResult"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            placeholder="请填写处理结果（1-500字）"
          />
        </el-form-item>
      </el-form>
    </FormDialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import PageTable from '@/components/common/PageTable.vue'
import FormDialog from '@/components/common/FormDialog.vue'
import { getRepairPageApi, handleRepairApi } from '@/api/repair'
import { getBuildingAllApi } from '@/api/building'
import { REPAIR_STATUS_OPTIONS, repairStatusText, repairStatusTagType, repairTypeText } from './repairOptions'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const buildingList = ref([])
const timeRange = ref([])
const dialogVisible = ref(false)
const handleFormRef = ref(null)
const currentRow = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: null,
  buildingId: null
})

const handleForm = reactive({
  status: null,
  handleResult: ''
})

/** isOverdue 行高亮 */
const rowClassName = ({ row }) => (row.isOverdue ? 'overdue-row' : '')

/** 目标状态下拉：待处理仅可→处理中，处理中仅可→已完成 */
const targetOptions = computed(() => {
  if (currentRow.value?.status === 0) return [{ label: '处理中', value: 1 }]
  if (currentRow.value?.status === 1) return [{ label: '已完成', value: 2 }]
  return []
})

const handleRules = {
  status: [{ required: true, message: '请选择目标状态', trigger: 'change' }],
  handleResult: [
    {
      validator: (rule, value, callback) => {
        if (handleForm.status === 2 && !value) {
          callback(new Error('请填写处理结果'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
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
    const data = await getRepairPageApi(buildParams())
    tableData.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

const loadBuildings = async () => {
  try {
    buildingList.value = (await getBuildingAllApi()) || []
  } catch (e) {
    // 错误提示由请求拦截器统一处理
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
  queryParams.buildingId = null
  timeRange.value = []
  handleSearch()
}

const openHandleDialog = (row) => {
  currentRow.value = row
  handleForm.status = null
  handleForm.handleResult = ''
  handleFormRef.value?.clearValidate()
  dialogVisible.value = true
}

const handleConfirm = async () => {
  try {
    await handleFormRef.value.validate()
  } catch (e) {
    return
  }
  saving.value = true
  try {
    await handleRepairApi(currentRow.value.repairId, {
      status: handleForm.status,
      handleResult: handleForm.status === 2 ? handleForm.handleResult : undefined
    })
    ElMessage.success('处理成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadBuildings()
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
