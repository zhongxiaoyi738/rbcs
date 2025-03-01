package com.hsbc.balance.mq.consumer;

import com.hsbc.balance.constant.Topic;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@ConditionalOnBean(TradeConsumer.class)
public class TradeZeroConsumer {

    @Autowired
    private TradeConsumer tradeConsumer;

    private static final String TOPIC = Topic.TRADE_ZERO;

    @PostConstruct
    public void initConsumer() {
        tradeConsumer.initConsumerGroup(TOPIC);
        ExecutorService tradeZeroExecutor = Executors.newFixedThreadPool(1);
        tradeZeroExecutor.submit(() -> {
            tradeConsumer.startConsuming(TOPIC, Topic.TRADE_RETRY_ONE, 1);
        });
    }

}
