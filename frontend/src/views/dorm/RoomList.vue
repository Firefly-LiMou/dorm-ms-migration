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
          <el-form-item label="房间编号">
            <el-input v-model="query.roomNo" placeholder="模糊查询" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openDialog()">新增房间</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="roomId" label="ID" width="70" />
        <el-table-column prop="buildingId" label="楼栋ID" width="90" />
        <el-table-column prop="roomNo" label="房间编号" width="110" />
        <el-table-column prop="floor" label="楼层" width="80" />
        <el-table-column prop="bedCount" label="床位数" width="90" />
        <el-table-column prop="roomType" label="房间类型" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.roomId ? '编辑房间' : '新增房间'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="b in buildings" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间编号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="如 101、202" />
        </el-form-item>
        <el-form-item label="所在楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="床位数" prop="bedCount">
          <el-input-number v-model="form.bedCount" :min="1" :max="12" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-input v-model="form.roomType" placeholder="如 标准四人间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageRoomsApi, addRoomApi, updateRoomApi, deleteRoomApi } from '@/api/room'
import { listBuildingsApi } from '@/api/building'
import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const buildings = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: DEFAULT_PAGE_SIZE, buildingId: null, roomNo: '' })
const form = reactive({ roomId: null, buildingId: null, roomNo: '', floor: 1, bedCount: 4, roomType: '' })

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间编号', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  bedCount: [{ required: true, message: '请输入床位数', trigger: 'blur' }]
}

const loadBuildings = async () => {
  buildings.value = await listBuildingsApi()
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await pageRoomsApi(query)
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
  query.roomNo = ''
  handleSearch()
}

const handlePageChange = (page) => {
  query.pageNum = page
  loadData()
}

const openDialog = (row) => {
  if (row) {
    Object.assign(form, row)
  } else {
    Object.assign(form, { roomId: null, buildingId: null, roomNo: '', floor: 1, bedCount: 4, roomType: '' })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.roomId) {
      await updateRoomApi(form.roomId, form)
    } else {
      await addRoomApi(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {
    // 已统一提示
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除房间「${row.roomNo}」吗？`, '提示', { type: 'warning' })
  await deleteRoomApi(row.roomId)
  ElMessage.success('删除成功')
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
