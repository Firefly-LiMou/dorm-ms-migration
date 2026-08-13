<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-form inline @submit.prevent>
          <el-form-item label="所属楼栋">
            <el-select v-model="query.buildingId" placeholder="全部" clearable style="width: 180px">
              <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
            </el-select>
          </el-form-item>
          <el-form-item label="学号">
            <el-input v-model="query.username" placeholder="精确查询" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
              <el-option label="入住中" :value="1" />
              <el-option label="已退宿" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openCheckinDialog">入住分配</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="checkinId" label="记录ID" width="80" />
        <el-table-column prop="username" label="学号" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="buildingName" label="楼栋" width="140" />
        <el-table-column prop="roomNo" label="房间" width="90" />
        <el-table-column prop="bedNo" label="床位" width="70" />
        <el-table-column prop="checkinTime" label="入住时间" width="160" />
        <el-table-column prop="checkoutTime" label="退宿时间" width="160" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '入住中' : '已退宿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="办理人" width="100" />
        <el-table-column label="操作" width="110" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" size="small" type="danger" @click="handleCheckout(row)">退宿</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无数据" />
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

    <el-dialog v-model="dialogVisible" title="入住分配" width="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学生" prop="userId">
          <el-select v-model="form.userId" placeholder="选择学生" filterable style="width: 100%">
            <el-option v-for="s in students" :key="s.userId" :label="`${s.username} - ${s.realName}`" :value="s.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="空闲床位" prop="bedId">
          <el-select v-model="form.bedId" placeholder="先选楼栋和房间" style="width: 100%">
            <el-option v-for="b in freeBeds" :key="b.bedId" :label="`${b.bedNo}号床`" :value="b.bedId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select v-model="checkinBuildingId" placeholder="选择楼栋" style="width: 100%" @change="handleCheckinBuildingChange">
            <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属房间">
          <el-select v-model="checkinRoomId" placeholder="选择房间" style="width: 100%" @change="handleCheckinRoomChange">
            <el-option v-for="r in checkinRooms" :key="r.roomId" :label="r.roomNo" :value="r.roomId" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定分配</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageCheckinsApi, checkinApi, checkoutApi, pageStudentsApi } from '@/api/checkin'
import { listBuildingsApi } from '@/api/building'
import { listRoomsApi } from '@/api/room'
import { pageBedsApi } from '@/api/bed'
import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const buildings = ref([])
const students = ref([])
const freeBeds = ref([])
const checkinRooms = ref([])
const checkinBuildingId = ref(null)
const checkinRoomId = ref(null)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: DEFAULT_PAGE_SIZE, buildingId: null, username: '', status: null })
const form = reactive({ userId: null, bedId: null, remark: '' })

const rules = {
  userId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  bedId: [{ required: true, message: '请选择空闲床位', trigger: 'change' }]
}

const loadBuildings = async () => {
  buildings.value = await listBuildingsApi()
}

const loadStudents = async () => {
  const data = await pageStudentsApi({ pageNum: 1, pageSize: 100 })
  students.value = data.list || []
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await pageCheckinsApi(query)
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
  query.buildingId = null
  query.username = ''
  query.status = null
  handleSearch()
}

const handlePageChange = (page) => {
  query.pageNum = page
  loadData()
}

const openCheckinDialog = () => {
  Object.assign(form, { userId: null, bedId: null, remark: '' })
  checkinBuildingId.value = null
  checkinRoomId.value = null
  freeBeds.value = []
  loadStudents()
  dialogVisible.value = true
}

const handleCheckinBuildingChange = async (val) => {
  checkinRoomId.value = null
  freeBeds.value = []
  checkinRooms.value = val ? await listRoomsApi(val) : []
}

const handleCheckinRoomChange = async (val) => {
  form.bedId = null
  if (val) {
    const data = await pageBedsApi({ pageNum: 1, pageSize: 100, roomId: val, status: 0 })
    freeBeds.value = data.list || []
  } else {
    freeBeds.value = []
  }
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    await checkinApi({ userId: form.userId, bedId: form.bedId, remark: form.remark })
    ElMessage.success('入住分配成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 已统一提示
  } finally {
    submitting.value = false
  }
}

const handleCheckout = async (row) => {
  await ElMessageBox.confirm(`确定为「${row.realName}」办理退宿吗？`, '提示', { type: 'warning' })
  await checkoutApi(row.checkinId, {})
  ElMessage.success('退宿成功')
  loadData()
}

onMounted(() => {
  loadBuildings()
  loadData()
})
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
