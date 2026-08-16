<template>
  <div class="user-manage">
    <!-- 查询表单 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="学号">
          <el-input v-model="queryParams.username" placeholder="按学号查询" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="queryParams.realName" placeholder="按姓名查询" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="正常" :value="USER_STATUS.NORMAL" />
            <el-option label="禁用" :value="USER_STATUS.DISABLED" />
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
      <div class="table-toolbar">
        <el-button type="primary" @click="openCreateDialog">新增学生</el-button>
      </div>
      <el-table v-loading="loading" :data="tableData" border stripe>
        <el-table-column prop="username" label="学号" min-width="110" />
        <el-table-column prop="realName" label="姓名" min-width="100" />
        <el-table-column prop="gender" label="性别" width="70" align="center">
          <template #default="{ row }">{{ row.gender || '-' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120">
          <template #default="{ row }">{{ row.phone || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === USER_STATUS.NORMAL ? 'success' : 'danger'">
              {{ row.status === USER_STATUS.NORMAL ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column label="操作" width="250" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEditDialog(row)">编辑</el-button>
            <el-button
              link
              :type="row.status === USER_STATUS.NORMAL ? 'danger' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === USER_STATUS.NORMAL ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="warning" @click="handleResetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
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

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑学生账号' : '新增学生'"
      width="480px"
      :close-on-click-modal="false"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item v-if="!isEdit" label="学号" prop="username">
          <el-input v-model="form.username" placeholder="请输入学号" maxlength="32" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入姓名" maxlength="32" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择" clearable style="width: 100%">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="USER_STATUS.NORMAL">正常</el-radio>
            <el-radio :value="USER_STATUS.DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserPageApi, createUserApi, updateUserApi, resetUserPasswordApi } from '@/api/user'
import { USER_STATUS } from '@/utils/constants'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  username: '',
  realName: '',
  status: null
})

const defaultForm = () => ({
  userId: null,
  username: '',
  realName: '',
  gender: '',
  phone: '',
  status: USER_STATUS.NORMAL
})

const form = reactive(defaultForm())

const rules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { max: 32, message: '学号长度不能超过32位', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 32, message: '姓名长度不能超过32位', trigger: 'blur' }
  ],
  phone: [{ pattern: /^\d{11}$/, message: '手机号必须为11位数字', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await getUserPageApi({ ...queryParams })
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
  queryParams.username = ''
  queryParams.realName = ''
  queryParams.status = null
  handleSearch()
}

const openCreateDialog = () => {
  isEdit.value = false
  Object.assign(form, defaultForm())
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  Object.assign(form, {
    userId: row.userId,
    username: row.username,
    realName: row.realName,
    gender: row.gender || '',
    phone: row.phone || '',
    status: row.status
  })
  dialogVisible.value = true
}

const handleSave = async () => {
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit.value) {
      await updateUserApi(form.userId, {
        realName: form.realName,
        gender: form.gender,
        phone: form.phone,
        status: form.status
      })
      ElMessage.success('编辑成功')
    } else {
      await createUserApi({
        username: form.username,
        realName: form.realName,
        gender: form.gender,
        phone: form.phone
      })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    handleSearch()
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    saving.value = false
  }
}

const handleToggleStatus = async (row) => {
  const targetStatus = row.status === USER_STATUS.NORMAL ? USER_STATUS.DISABLED : USER_STATUS.NORMAL
  const action = targetStatus === USER_STATUS.DISABLED ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定${action}账号「${row.username}」吗？`, '提示', { type: 'warning' })
  try {
    await updateUserApi(row.userId, { status: targetStatus })
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  }
}

const handleResetPassword = async (row) => {
  await ElMessageBox.confirm(`确定将账号「${row.username}」的密码重置为初始密码 123456 吗？`, '提示', {
    type: 'warning'
  })
  try {
    await resetUserPasswordApi(row.userId)
    ElMessage.success('重置成功')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.table-toolbar {
  margin-bottom: 16px;
}
</style>
