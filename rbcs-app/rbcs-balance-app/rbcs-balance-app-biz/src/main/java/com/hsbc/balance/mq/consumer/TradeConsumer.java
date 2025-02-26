package com.hsbc.balance.mq.consumer;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.manager.IAccountBalanceManager;
import com.hsbc.common.mq.producer.AbstractProducer;
import com.hsbc.common.utils.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RFuture;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamGroup;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class TradeConsumer {

    @Value("${mq.redisson-stream.consumer.batch-size:1}")
    private Integer batchSize;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private IAccountBalanceManager accountBalanceManager;

    @Autowired
    private AbstractProducer redisStreamProducer;

    @Bean(name="tradeExecutor", destroyMethod = "shutdown")
    public ExecutorService executor() {
        return Executors.newFixedThreadPool(1);
    }

    @Autowired
    @Qualifier("tradeExecutor")
    private ExecutorService tradeExecutor;

    public void initConsumerGroup(String topic) {
        RStream<String, String> stream = redissonClient.getStream(topic);
        if (!stream.isExists()) {
            // 动态创建stream
            stream.add(StreamAddArgs.entry("init", ""));
        }
        List<StreamGroup> streamGroups = stream.listGroups();
        if (streamGroups.stream().noneMatch(group -> group.getName().equals(topic))) {
            stream.createGroup(StreamCreateGroupArgs.name(topic));
        }
    }

    public void startConsuming(String topic, String producerTopic, Integer consumerTime) {
        String consumerName = topic + "-" + System.getProperty("os.name");
        tradeExecutor.submit(() -> {
            try {
                RStream<String, String> stream = redissonClient.getStream(topic);
                while (!Thread.currentThread().isInterrupted() && !redissonClient.isShutdown()) {
                    RFuture<Map<StreamMessageId, Map<String, String>>> future = stream.readGroupAsync(topic, consumerName, StreamReadGroupArgs.neverDelivered()
                            .count(batchSize)  // 批量读取数量
                            .timeout(Duration.ofSeconds(30)));
                    future.whenComplete((msgMap, throwable) -> {
                        if (throwable != null) {
                            log.error("实时余额计算第{}次消费失败", consumerTime, throwable);
                            return;
                        }
                        if (!msgMap.isEmpty()) {
                            onConsumer(topic, producerTopic, consumerTime, stream, msgMap);
                        }
                    });
                }
            } catch (Exception e) {
                log.error("实时余额计算第{}次消费失败", consumerTime, e);
            }
        });
    }

    private void onConsumer(String topic, String producerTopic, Integer consumerTime, RStream<String, String> stream, Map<StreamMessageId, Map<String, String>> msgMap) {
        for(Map.Entry<StreamMessageId, Map<String, String>> entryMap : msgMap.entrySet()) {
            String message = entryMap.getValue().get(topic);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            Trade trade = JsonUtils.fromJson(message, Trade.class);
            try {
                Integer rtn = accountBalanceManager.modifyAccountBalance(trade);
                if (rtn >= 0) {
                    stream.ack(topic, entryMap.getKey());
                }
                else {
                    redisStreamProducer.send(producerTopic, message, null);
                }
            }
            catch (Exception e) {
                log.error("实时余额计算第{}次消费失败", consumerTime, e);
                redisStreamProducer.send(producerTopic, message, null);
            }
        }
    }
}
