<template>
  <el-dialog v-model="visible" :title="title" :width="width" :close-on-click-modal="false">
    <slot />
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  /** 弹窗可见性（v-model:visible） */
  visible: { type: Boolean, default: false },
  /** 标题 */
  title: { type: String, default: '提示' },
  /** 宽度 */
  width: { type: String, default: '500px' },
  /** 确认按钮加载状态 */
  loading: { type: Boolean, default: false }
})

const emit = defineEmits(['update:visible', 'confirm'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const handleConfirm = () => {
  emit('confirm')
}
</script>
