package com.hsbc.common.mq.callback;

import com.hsbc.log.enums.SendStatusEnum;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * 消息队列，生产者回调函数
 *
 * @Author 钟凤娟
 * @Date 2025/02/23 13:30
 */
public interface IProducerCallback {

    void onSuccess(String msgId, String message);

    void onFail(String message, @NonNull final SendStatusEnum statusEnum, @Nullable final Throwable e);
}
