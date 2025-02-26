package com.hsbc.balance.constant;

public class Topic {

    /**
     * 实时余额计算
     */
    public static final String TRADE_ZERO = "trade_zero";

    /**
     * 实时余额计算：第一次重试     @todo 延时队列
     */
    public static final String TRADE_RETRY_ONE = "trade_retry_one";

    /**
     * 实时余额计算：第二次重试     @todo 延时队列
     */
    public static final String TRADE_RETRY_TWO = "trade_retry_two";

    /**
     * 实时余额计算：死信队列
     */
    public static final String TRADE_DLQ = "trade_dlq";

}
