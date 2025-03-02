package com.hsbc.common.mq.producer.impl;

import com.hsbc.common.mq.callback.IProducerCallback;
import com.hsbc.common.mq.producer.AbstractProducer;
import com.hsbc.log.enums.SendStatusEnum;
import com.hsbc.log.idworker.IdWorkerUtils;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RFuture;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamTrimArgs;
import org.redisson.api.stream.StreamTrimParams;
import org.redisson.api.stream.StreamTrimStrategyArgs;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisStreamProducer extends AbstractProducer {

    @Value("${mq.redisson-stream.producer.trim.max-length:0}")
    private Integer maxLength;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    protected void doSend(String topic, String message, IProducerCallback callback) {
        RStream<String, String> stream = redissonClient.getStream(topic);
        StreamAddArgs<String, String> streamMsg = StreamAddArgs.entry(topic, message);
        RFuture<StreamMessageId> future = stream.addAsync(streamMsg);
        future.whenComplete((rtnId, ex) -> {
            if (ex != null) {
                log.error("消息发送失败，topic:{}，message:{}", topic, message, ex);
                if (callback != null) {
                    callback.onFail(message, SendStatusEnum.SEND_ERROR, ex);
                }
            } else {
                String msgId = rtnId.getId0() + "-" + rtnId.getId1();
                log.debug("消息发送成功，topic:{}，message:{}, strMsgId:{}", topic, message, msgId);
                if (callback != null) {
                    callback.onSuccess(msgId, message);
                }
            }
        });
//        if (maxLength > 0) {
//            RFuture<Long> trimFuture = stream.trimAsync(StreamTrimArgs.maxLen(maxLength).limit(maxLength));
//            trimFuture.whenCompleteAsync((rtnId, ex) -> {
//                if (ex != null) {
//                    log.error("redis修剪失败，topic:{}，maxLength:{}", topic, maxLength, ex);
//                } else {
//                    log.info("redis修剪成功，topic:{}，maxLength:{}，rtnId:{}", topic, maxLength, rtnId);
//                }
//            });
//        }
    }

}
