package com.hsbc.balance.mq.consumer;

import com.hsbc.balance.constant.Topic;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Component
@ConditionalOnBean(TradeConsumer.class)
public class TradeRetryTwoConsumer {
    @Autowired
    private TradeConsumer tradeConsumer;

    private static final String TOPIC = Topic.TRADE_RETRY_TWO;

    @PostConstruct
    public void initConsumer() {
        tradeConsumer.initConsumerGroup(TOPIC);
        ExecutorService tradeRetryTwoExecutor = Executors.newFixedThreadPool(1);
        tradeRetryTwoExecutor.submit(() -> {
            tradeConsumer.startConsuming(TOPIC, Topic.TRADE_DLQ, 3);
        });
    }

}
