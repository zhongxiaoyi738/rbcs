package com.hsbc.balance.mq.consumer;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hsbc.balance.constant.Topic;
import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.enums.TradeStatusEnum;
import com.hsbc.balance.manager.IAccountBalanceManager;
import com.hsbc.common.mq.producer.AbstractProducer;
import com.hsbc.common.utils.JsonUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RStream;
import org.redisson.api.RedissonClient;
import org.redisson.api.StreamGroup;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamAddArgs;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
//@ConditionalOnBean(RedissonClient.class)
public class TradeConsumer {

    @Value("${mq.redisson-stream.consumer.batch-size:1}")
    private Integer batchSize;

    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private IAccountBalanceManager accountBalanceManager;

    @Autowired
    private AbstractProducer redisStreamProducer;

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
        log.debug("startConsuming topic:{}, producerTopic:{}", topic, producerTopic);
        String consumerName = topic + "-" + System.getProperty("os.name");
        RStream<String, String> stream = redissonClient.getStream(topic);
        while (true) {
            Map<StreamMessageId, Map<String, String>> msgMap;
            try {
                msgMap = stream.readGroup(topic, consumerName, StreamReadGroupArgs.neverDelivered().count(batchSize).noAck().timeout(Duration.ofSeconds(10)));
            } catch (Exception e) {
                log.error("实时余额计算第{}次消费失败", consumerTime, e);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    log.error("实时余额计算第{}次消费失败", consumerTime, ex);
                }
                continue;
            }
            if (!msgMap.isEmpty()) {
                onConsumer(topic, producerTopic, consumerTime, stream, msgMap);
            }
        }
    }

    private void onConsumer(String topic, String producerTopic, Integer consumerTime, RStream<String, String> stream, Map<StreamMessageId, Map<String, String>> msgMap) {
        for(Map.Entry<StreamMessageId, Map<String, String>> entryMap : msgMap.entrySet()) {
            String message = entryMap.getValue().get(topic);
            if (StringUtils.isEmpty(message)) {
                return;
            }
            log.debug("onConsumer topic:{}, message:{}", topic, message);
            try {
                Trade trade = JsonUtils.fromJson(message, Trade.class);
                Integer rtn = accountBalanceManager.modifyAccountBalance(trade, producerTopic.equals(Topic.TRADE_DLQ)?TradeStatusEnum.FAIL.getCode(): TradeStatusEnum.WAITING.getCode());
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
