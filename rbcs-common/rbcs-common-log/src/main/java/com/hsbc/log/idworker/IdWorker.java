package com.hsbc.log.idworker;

import lombok.Setter;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IdWorker {
    /**
     * 时间种子编码位数补全
     */
    private static final String TIME_TEMPLATE = "00000000000";

    /**
     * 机器编码位数补全
     */
    private static final String WORKER_ID_TEMPLATE = "000";

    /**
     * 自增序列位数补全
     */
    private static final String SEQUENCE_TEMPLATE = "0000";

    /**
     * 最大机器码
     */
    private static final long MAX_WORKER_ID = 999L;

    /**
     * 最大序列号
     */
    private static final long MAX_SEQUENCE = 9999L;

    /**
     * 初始化序列号
     */
    private long sequence = 0L;

    /**
     * 上一次时间
     */
    @Setter
    private long lastTime = -1;

    private final long workerId;

    public IdWorker(long workerId) {
        if (workerId > MAX_WORKER_ID) {
            workerId = workerId % MAX_WORKER_ID;
        }
        this.workerId = workerId;
    }

    public synchronized long nextId(int seqNo) {
        long currentTime = timeGen();
        // 检查时间是否回退，如果是，则抛出异常
        if (currentTime < lastTime) {
            throw new RuntimeException("指针回退异常");
        }

        // 如果当前时间和上一次生成ID的时间相同，递增序列号
        if (lastTime == currentTime) {
            sequence = (sequence + 1) % MAX_SEQUENCE;
            // 如果序列号达到最大值，等待下一个时间戳
            if (sequence == 0) {
                currentTime = tilNextTime(lastTime);
            }
        } else {
            // 如果当前时间大于上一次生成ID的时间，重置序列号
            sequence = 0;
        }
        lastTime = currentTime;

        // 组合序列号、时间戳、工作机器ID和序列号，形成最终的ID
        return Long.parseLong(seqNo +
                new DecimalFormat(TIME_TEMPLATE).format(currentTime) +
                new DecimalFormat(WORKER_ID_TEMPLATE).format(workerId) +
                new DecimalFormat(SEQUENCE_TEMPLATE).format(sequence));
    }

    protected long tilNextTime(long lastTime) {
        long currentTime = timeGen();
        // 循环直到获取到当前时间大于lastTime的时间
        while (currentTime <= lastTime) {
            currentTime = timeGen();
        }
        return currentTime;
    }

    protected long timeGen() {
        // 使用SimpleDateFormat将系统当前时间转换为指定格式的字符串，然后解析为长整型时间戳
        return Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).substring(3));
    }
}
