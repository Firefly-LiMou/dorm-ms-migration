// 全局通用常量：跨模块共享项集中在此，业务状态枚举就近定义在对应模块

/** 默认分页大小 */
export const DEFAULT_PAGE_SIZE = 10

/** 时间展示格式 */
export const TIME_FORMAT = 'YYYY-MM-DD HH:mm:ss'

/** 请求超时时间（ms） */
export const REQUEST_TIMEOUT = 10000

/** 用户角色 */
export const ROLE = {
  ADMIN: 'admin',
  STUDENT: 'student'
}

/** 账号状态 */
export const USER_STATUS = {
  NORMAL: 1,
  DISABLED: 0
}

/** 床位状态 */
export const BED_STATUS = {
  FREE: 0,
  OCCUPIED: 1
}

/** 入住记录状态 */
export const CHECKIN_STATUS = {
  CHECKED_IN: 1,
  CHECKED_OUT: 2
}
