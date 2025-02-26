package com.hsbc.log.idworker;

public class IdWorkerUtils {
    public IdWorkerUtils(IdWorker idWorker) {
        IdWorkerUtils.idWorker = idWorker;
    }

    /**
     * Snowflake算法的核心ID生成器实例。
     */
    private static IdWorker idWorker;

    static void clean() {
        idWorker = null;
    }

    /**
     * 默认的序列号值，用于nextId()方法中，当不指定序列号时使用。
     */
    private static final int DEFAULT_SEQ_NO = 1;

    public static long nextId(int seqNo) {
        return idWorker.nextId(seqNo);
    }

    public static long nextId() {
        return idWorker.nextId(DEFAULT_SEQ_NO);
    }

    public static long getTraceId() {
        return IdWorkerUtils.nextId(2);
    }
}
