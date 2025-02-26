package com.hsbc.common.mq.producer;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hsbc.common.base.RbcsException;
import com.hsbc.common.constant.CommonErrorCode;
import com.hsbc.common.mq.callback.IProducerCallback;
import com.hsbc.common.utils.JsonUtils;
import com.hsbc.log.enums.SendStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Slf4j
public abstract class AbstractProducer {

    public <T> void send(String topic, T message, IProducerCallback callback) {
        if (ObjectUtils.isEmpty(message)) {
            log.debug("消息发送中止，消息体配置为空! topic:{}", topic);
            callback.onFail("", SendStatusEnum.SEND_INTERRUPT, RbcsException.of(CommonErrorCode.INIT_ERROR, "消息发送中止，消息体为空!"));
            return;
        }
        String strMessage = JsonUtils.toJson(message);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCompletion(int status) {
                    if (status != TransactionSynchronization.STATUS_COMMITTED) {
                        log.warn("消息发送中止，事务状态异常! topic:{}, message:{}, transactionStatus:{}",
                                topic, strMessage, status);
                        if (callback != null) {
                            callback.onFail(strMessage, SendStatusEnum.SEND_INTERRUPT, RbcsException.of(CommonErrorCode.INIT_ERROR, "消息发送中止，事务状态异常!"));
                        }
                        return;
                    }
                    doSend(topic, strMessage, callback);
                }
            });
        } else {
            doSend(topic, strMessage, callback);
        }
    }

    protected abstract void doSend(String topic, String message, IProducerCallback callback);
}
