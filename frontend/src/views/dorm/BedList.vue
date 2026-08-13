<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-form inline @submit.prevent>
          <el-form-item label="所属楼栋">
            <el-select v-model="buildingId" placeholder="请选择" clearable style="width: 180px" @change="handleBuildingChange">
              <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
            </el-select>
          </el-form-item>
          <el-form-item label="所属房间">
            <el-select v-model="query.roomId" placeholder="请选择" clearable style="width: 180px" @change="handleSearch">
              <el-option v-for="r in rooms" :key="r.roomId" :label="r.roomNo" :value="r.roomId" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px" @change="handleSearch">
              <el-option label="空闲" :value="0" />
              <el-option label="已入住" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openBatchDialog">批量初始化床位</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="bedId" label="床位ID" width="90" />
        <el-table-column prop="roomId" label="房间ID" width="90" />
        <el-table-column prop="bedNo" label="床位号" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已入住' : '空闲' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleToggleStatus(row)">改为{{ row.status === 1 ? '空闲' : '已入住' }}</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据，请先选择房间" />
        </template>
      </el-table>

      <el-pagination
        class="pagination"
        background
        layout="total, prev, pager, next"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.pageNum"
        @current-change="handlePageChange"
      />
    </el-card>

    <el-dialog v-model="batchVisible" title="批量初始化床位" width="480px">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="batchForm.buildingId" placeholder="请选择楼栋" style="width: 100%" @change="handleBatchBuildingChange">
            <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属房间" prop="roomId">
          <el-select v-model="batchForm.roomId" placeholder="请选择房间" style="width: 100%">
            <el-option v-for="r in batchRooms" :key="r.roomId" :label="r.roomNo" :value="r.roomId" />
          </el-select>
        </el-form-item>
        <el-form-item label="床位数量" prop="count">
          <el-input-number v-model="batchForm.count" :min="1" :max="12" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleBatchSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { pageBedsApi, batchCreateBedsApi, updateBedStatusApi } from '@/api/bed'
import { listBuildingsApi } from '@/api/building'
import { listRoomsApi } from '@/api/room'
import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const buildings = ref([])
const rooms = ref([])
const batchRooms = ref([])
const buildingId = ref(null)
const batchVisible = ref(false)
const batchFormRef = ref()

const query = reactive({ pageNum: 1, pageSize: DEFAULT_PAGE_SIZE, roomId: null, status: null })
const batchForm = reactive({ buildingId: null, roomId: null, count: 4 })

const batchRules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  count: [{ required: true, message: '请输入床位数量', trigger: 'blur' }]
}

const loadBuildings = async () => {
  buildings.value = await listBuildingsApi()
}

const handleBuildingChange = async (val) => {
  query.roomId = null
  rooms.value = val ? await listRoomsApi(val) : []
  loadData()
}

const handleBatchBuildingChange = async (val) => {
  batchForm.roomId = null
  batchRooms.value = val ? await listRoomsApi(val) : []
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await pageBedsApi(query)
    list.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 已统一提示
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const handleReset = () => {
  buildingId.value = null
  query.roomId = null
  query.status = null
  rooms.value = []
  handleSearch()
}

const handlePageChange = (page) => {
  query.pageNum = page
  loadData()
}

const handleToggleStatus = async (row) => {
  const target = row.status === 1 ? 0 : 1
  await updateBedStatusApi(row.bedId, { status: target })
  ElMessage.success('状态更新成功')
  loadData()
}

const openBatchDialog = () => {
  Object.assign(batchForm, { buildingId: null, roomId: null, count: 4 })
  batchVisible.value = true
}

const handleBatchSubmit = async () => {
  await batchFormRef.value.validate()
  submitting.value = true
  try {
    await batchCreateBedsApi({ roomId: batchForm.roomId, count: batchForm.count })
    ElMessage.success('初始化成功')
    batchVisible.value = false
    loadData()
  } catch (e) {
    // 已统一提示
  } finally {
    submitting.value = false
  }
}

onMounted(loadBuildings)
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
