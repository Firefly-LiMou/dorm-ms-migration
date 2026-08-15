<template>
  <div class="page-table">
    <el-table v-loading="loading" :data="data" :row-class-name="rowClassName" border stripe>
      <slot />
    </el-table>
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  /** 表格数据 */
  data: { type: Array, default: () => [] },
  /** 总条数 */
  total: { type: Number, default: 0 },
  /** 加载状态 */
  loading: { type: Boolean, default: false },
  /** 当前页码（v-model:page-num） */
  pageNum: { type: Number, default: 1 },
  /** 每页条数（v-model:page-size） */
  pageSize: { type: Number, default: 10 },
  /** 行样式函数（透传给 el-table 的 row-class-name） */
  rowClassName: { type: Function, default: null }
})

const emit = defineEmits(['update:pageNum', 'update:pageSize', 'page-change', 'size-change'])

const currentPage = computed({
  get: () => props.pageNum,
  set: (val) => emit('update:pageNum', val)
})

const pageSize = computed({
  get: () => props.pageSize,
  set: (val) => emit('update:pageSize', val)
})

const handleCurrentChange = (page) => {
  emit('page-change', page)
}

const handleSizeChange = (size) => {
  emit('size-change', size)
}
</script>

<style scoped>
.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
