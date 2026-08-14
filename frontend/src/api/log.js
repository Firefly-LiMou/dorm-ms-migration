import request from '@/utils/request'

/** 分页查询操作日志（管理员） */
export const getLogPageApi = (params) => request.get('/log/page', { params })
