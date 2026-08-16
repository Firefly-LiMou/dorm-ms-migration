<template>
  <div class="repair-submit">
    <el-card>
      <template #header>
        <span>提交报修</span>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" style="max-width: 560px">
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option v-for="item in repairTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="4"
            maxlength="500"
            show-word-limit
            placeholder="请描述故障情况（1-500字）"
          />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" maxlength="11" placeholder="请输入11位手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSubmit">提交报修</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { submitRepairApi } from '@/api/repair'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const repairTypeOptions = [
  { label: '水电故障', value: 0 },
  { label: '家具损坏', value: 1 },
  { label: '网络问题', value: 2 },
  { label: '其他', value: 3 }
]

const form = reactive({
  repairType: null,
  content: '',
  contactPhone: ''
})

const rules = {
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  content: [
    { required: true, message: '请输入报修内容', trigger: 'blur' },
    { min: 1, max: 500, message: '报修内容长度需在1-500字之间', trigger: 'blur' }
  ],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^\d{11}$/, message: '联系电话必须为11位数字', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch (e) {
    return
  }
  loading.value = true
  try {
    await submitRepairApi({ ...form })
    ElMessage.success('报修提交成功')
    router.push('/repair/my')
  } catch (e) {
    // 错误提示由请求拦截器统一处理
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  form.repairType = null
  form.content = ''
  form.contactPhone = ''
  formRef.value?.clearValidate()
}
</script>
