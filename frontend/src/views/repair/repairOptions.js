/** 报修状态选项（0-待处理，1-处理中，2-已完成） */
export const REPAIR_STATUS_OPTIONS = [
  { label: '待处理', value: 0 },
  { label: '处理中', value: 1 },
  { label: '已完成', value: 2 }
]

/** 报修类型选项（0-水电故障，1-家具损坏，2-网络问题，3-其他） */
export const REPAIR_TYPE_OPTIONS = [
  { label: '水电故障', value: 0 },
  { label: '家具损坏', value: 1 },
  { label: '网络问题', value: 2 },
  { label: '其他', value: 3 }
]

/** 状态中文文案 */
export const repairStatusText = (status) => {
  const item = REPAIR_STATUS_OPTIONS.find((o) => o.value === status)
  return item ? item.label : status
}

/** 类型中文文案 */
export const repairTypeText = (type) => {
  const item = REPAIR_TYPE_OPTIONS.find((o) => o.value === type)
  return item ? item.label : type
}

/** 状态标签类型（Element Plus tag type） */
export const repairStatusTagType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1) return 'primary'
  return 'success'
}
