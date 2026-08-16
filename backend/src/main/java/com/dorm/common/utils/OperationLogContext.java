package com.dorm.common.utils;

/**
 * 操作日志变更上下文：业务层在执行写操作时，将「数据变更描述（旧值→新值）」放入当前线程，
 * 由 OperationLogAspect 记录操作日志时读取并拼接进操作详情，读取后自动清理。
 */
public final class OperationLogContext {

    private static final ThreadLocal<String> CHANGE_HOLDER = new ThreadLocal<>();

    private OperationLogContext() {
    }

    /**
     * 写入数据变更描述（如：姓名: 张伟→改名, 状态: 正常→禁用）
     *
     * @param change 变更描述，空串视为无变更
     */
    public static void setChange(String change) {
        if (change != null && !change.isEmpty()) {
            CHANGE_HOLDER.set(change);
        }
    }

    /**
     * 读取并清除数据变更描述，确保请求结束后不残留线程变量
     *
     * @return 变更描述，无则返回 null
     */
    public static String getAndClear() {
        String change = CHANGE_HOLDER.get();
        CHANGE_HOLDER.remove();
        return change;
    }

    /** 兜底清理，防止异常场景残留 */
    public static void clear() {
        CHANGE_HOLDER.remove();
    }
}
