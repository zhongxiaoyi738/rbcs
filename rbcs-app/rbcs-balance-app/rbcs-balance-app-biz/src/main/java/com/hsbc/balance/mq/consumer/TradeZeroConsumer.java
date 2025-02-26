package com.hsbc.balance.mq.consumer;

import com.hsbc.balance.constant.Topic;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class TradeZeroConsumer {

    @Autowired
    private TradeConsumer tradeConsumer;

    private static final String TOPIC = Topic.TRADE_ZERO;

    @PostConstruct
    public void initConsumer() {
        tradeConsumer.initConsumerGroup(TOPIC);
        tradeConsumer.startConsuming(TOPIC, Topic.TRADE_RETRY_ONE, 1);
    }

}
