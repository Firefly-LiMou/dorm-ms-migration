<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-form inline @submit.prevent>
          <el-form-item label="楼栋编号">
            <el-input v-model="query.buildingNo" placeholder="模糊查询" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item label="所属区域">
            <el-input v-model="query.area" placeholder="精确查询" clearable style="width: 160px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
        <el-button type="primary" @click="openDialog()">新增楼栋</el-button>
      </div>

      <el-table v-loading="loading" :data="list" border stripe>
        <el-table-column prop="buildingId" label="ID" width="70" />
        <el-table-column prop="buildingNo" label="楼栋编号" width="110" />
        <el-table-column prop="buildingName" label="楼栋名称" />
        <el-table-column prop="floorCount" label="总楼层数" width="100" />
        <el-table-column prop="area" label="所属区域" width="110" />
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

    <el-dialog v-model="dialogVisible" :title="form.buildingId ? '编辑楼栋' : '新增楼栋'" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="如 1、2、东1" />
        </el-form-item>
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="如 一号学生公寓" />
        </el-form-item>
        <el-form-item label="总楼层数" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="所属区域" prop="area">
          <el-input v-model="form.area" placeholder="如 东区、西区" />
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
import { pageBuildingsApi, addBuildingApi, updateBuildingApi, deleteBuildingApi } from '@/api/building'
import { DEFAULT_PAGE_SIZE } from '@/utils/constants'

const loading = ref(false)
const submitting = ref(false)
const list = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ pageNum: 1, pageSize: DEFAULT_PAGE_SIZE, buildingNo: '', area: '' })
const form = reactive({ buildingId: null, buildingNo: '', buildingName: '', floorCount: 6, area: '' })

const rules = {
  buildingNo: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }],
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  floorCount: [{ required: true, message: '请输入总楼层数', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await pageBuildingsApi(query)
    list.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 请求拦截器已统一提示
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  query.pageNum = 1
  loadData()
}

const handleReset = () => {
  query.buildingNo = ''
  query.area = ''
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
    Object.assign(form, { buildingId: null, buildingNo: '', buildingName: '', floorCount: 6, area: '' })
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (form.buildingId) {
      await updateBuildingApi(form.buildingId, form)
    } else {
      await addBuildingApi(form)
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
  await ElMessageBox.confirm(`确定删除楼栋「${row.buildingName}」吗？`, '提示', { type: 'warning' })
  await deleteBuildingApi(row.buildingId)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
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
