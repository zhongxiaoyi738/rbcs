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
public class TradeRetryOneConsumer {

    @Autowired
    private TradeConsumer tradeConsumer;

    private static final String TOPIC = Topic.TRADE_RETRY_ONE;

    @PostConstruct
    public void initConsumer() {
        tradeConsumer.initConsumerGroup(TOPIC);
        ExecutorService tradeRetryOneExecutor = Executors.newFixedThreadPool(1);
        tradeRetryOneExecutor.submit(() -> {
            tradeConsumer.startConsuming(TOPIC, Topic.TRADE_RETRY_TWO, 2);
        });
    }

}
