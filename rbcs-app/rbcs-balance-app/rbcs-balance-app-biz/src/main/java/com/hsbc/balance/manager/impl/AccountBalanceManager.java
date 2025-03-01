package com.hsbc.balance.manager.impl;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hsbc.balance.constant.Filename;
import com.hsbc.balance.constant.Topic;
import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.enums.SubjectEnum;
import com.hsbc.balance.enums.TradeStatusEnum;
import com.hsbc.balance.manager.IAccountBalanceManager;
import com.hsbc.balance.param.AccountBalanceParam;
import com.hsbc.balance.param.AccountParam;
import com.hsbc.balance.param.TradeParam;
import com.hsbc.balance.service.IAccountBalanceService;
import com.hsbc.balance.service.IAccountService;
import com.hsbc.balance.service.IFileService;
import com.hsbc.balance.service.ITradeService;
import com.hsbc.balance.vo.AccountVo;
import com.hsbc.common.base.RbcsException;
import com.hsbc.common.mq.callback.IProducerCallback;
import com.hsbc.common.mq.producer.AbstractProducer;
import com.hsbc.common.utils.JsonUtils;
import com.hsbc.log.enums.SendStatusEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
@RefreshScope
public class AccountBalanceManager implements IAccountBalanceManager {
    @Resource
    private RedissonClient redissonClient;

    @Autowired
    private ITradeService tradeService;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private IAccountBalanceService accountBalanceService;

    @Autowired
    private AbstractProducer redisStreamProducer;

    @Autowired
    private IFileService fileService;

    @Override
    public Integer addTrade(TradeParam tradeParam) {
        Trade trade = tradeService.insert(tradeParam);
        if (ObjectUtils.isEmpty(trade)) {
            fileService.errFile(Filename.INSERT_DB, trade);
        }
        String topic = Topic.TRADE_ZERO;
        redisStreamProducer.send(topic, trade, new IProducerCallback() {
            @Override
            public void onSuccess(String msgId, String message) {
                fileService.bakFile(topic, JsonUtils.toJson(new HashMap<String, String>(){{
                    put("msgId", msgId);
                    put("message", message);
                }}));
                trade.setMsgId(msgId);
                tradeService.update(trade);
            }

            @Override
            public void onFail(String message, SendStatusEnum statusEnum, Throwable e) {
                fileService.errFile(topic, JsonUtils.toJson(new HashMap<String, String>(){{
                    put("message", message);
                    put("SendStatus", statusEnum.getName());
                    put("SendErrMessage", e.getMessage());
                }}));
            }
        });
        fileService.bakFile(Filename.INSERT_DB, trade);
        return trade.getId();
    }

    @Override
    public Integer modifyAccountBalance(Trade trade, String statusAfterFail) {
        // 加redis锁: 源账号, 目标账号
        RLock srcLock = null, descLock = null;
        if (StringUtils.isNotEmpty(trade.getSrcAccountUuno())) {
            srcLock = redissonClient.getLock("lockKey-" + trade.getSrcAccountUuno());
        }
        if (StringUtils.isNotEmpty(trade.getDescAccountUuno())) {
            descLock = redissonClient.getLock("lockKey-" + trade.getDescAccountUuno());
        }
        try {
            if (srcLock != null) {
                boolean acquired = srcLock.tryLock(0, 30, TimeUnit.SECONDS);
                if (!acquired) {
                    throw RbcsException.of(String.format("[源账号%s]有处理中的交易", trade.getSrcAccountUuno()));
                }
            }
            if (descLock != null) {
                boolean acquired = descLock.tryLock(0, 30, TimeUnit.SECONDS);
                if (!acquired) {
                    throw RbcsException.of(String.format("[目标账号%s]有处理中的交易", trade.getDescAccountUuno()));
                }
            }
            return calculation(trade, statusAfterFail);
        } catch (InterruptedException e) {
            throw RbcsException.of("加锁异常", e);
        }
        finally {
            if (srcLock != null && srcLock.isLocked() && srcLock.isHeldByCurrentThread()) {
                srcLock.forceUnlock();
            }
            if (descLock != null && descLock.isLocked() && descLock.isHeldByCurrentThread()) {
                descLock.forceUnlock();
            }
        }
    }

    private Integer calculation(Trade trade, String statusAfterFail) {
        // 加db锁: 交易
        trade.setStatus(TradeStatusEnum.WAITING.getCode());
        Integer lockDb = tradeService.updateStatus(trade, TradeStatusEnum.DOING.getCode());
        if (lockDb < 1) {
            return 0;
        }
        String statusEnd = statusAfterFail;
        try {
            int srcRtn = 1;
            int descRtn = 1;
            if (StringUtils.isNotEmpty(trade.getSrcAccountUuno())) {
                AccountVo srcAccountVo = accountService.selectOne(AccountParam.builder().uuno(trade.getSrcAccountUuno()).build());
                srcRtn = accountBalanceService.updateSrc(AccountBalanceParam.builder()
                        .accountUuid(srcAccountVo.getUuid())
                        .accountUuno(srcAccountVo.getUuno())    // 异常信息
                        .subject(SubjectEnum.CNY.getCode())
                        .amount(trade.getAmount()).build());
            }
            if (StringUtils.isNotEmpty(trade.getDescAccountUuno())) {
                AccountVo descAccountVo = accountService.selectOne(AccountParam.builder().uuno(trade.getDescAccountUuno()).build());
                descRtn = accountBalanceService.updateDesc(AccountBalanceParam.builder()
                        .accountUuid(descAccountVo.getUuid())
                        .accountUuno(descAccountVo.getUuno())
                        .subject(SubjectEnum.CNY.getCode())
                        .amount(trade.getAmount()).build());
            }
            trade.setStatus(null);
            if (srcRtn >= 1 && descRtn >= 1) {
                statusEnd = TradeStatusEnum.SUCCESS.getCode();
            }
        }
        catch (Exception e) {
            trade.setStatus(TradeStatusEnum.DOING.getCode());
            trade.setFailReason(ExceptionUtils.getStackTrace(e));
            throw e;
        }
        finally {
            tradeService.updateStatus(trade, statusEnd);
        }
        return lockDb;
    }
}
