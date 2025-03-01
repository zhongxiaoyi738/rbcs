package com.hsbc.balance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.enums.TradeStatusEnum;
import com.hsbc.balance.mapper.ITradeMapper;
import com.hsbc.balance.mapstruct.TradeMapStruct;
import com.hsbc.balance.param.TradeParam;
import com.hsbc.balance.service.ITradeService;
import com.hsbc.common.base.RbcsBaseServiceImpl;
import com.hsbc.common.base.RbcsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TradeServiceImpl extends RbcsBaseServiceImpl<ITradeMapper, Trade> implements ITradeService {

    @Autowired
    private TradeMapStruct tradeMapStruct;

    @Transactional
    @Override
    public Trade insert(TradeParam tradeParam) {
        if (!checkTradeParam(tradeParam)) {
            return null;
        }
        Trade trade = tradeMapStruct.paramToEntity(tradeParam)
                .setStatus(TradeStatusEnum.WAITING.getCode());
        int ret = insert(trade);
        if (ret < 0) {
            return null;
        }
        return trade;
    }

    @Override
    public Integer updateStatus(Trade trade, String newStatus) {
        return super.updateWrapper(trade, new LambdaUpdateWrapper<Trade>()
                .eq(Trade::getUuid, trade.getUuid())
                .eq(StringUtils.isNotBlank(trade.getStatus()), Trade::getStatus, trade.getStatus())
                .set(Trade::getStatus, newStatus)
                .set(StringUtils.isNotBlank(trade.getFailReason()), Trade::getFailReason, trade.getFailReason()));
    }

    private Boolean checkTradeParam(TradeParam tradeParam) {
        if (StringUtils.isEmpty(tradeParam.getSrcAccountUuno()) && StringUtils.isEmpty(tradeParam.getDescAccountUuno())) {
            throw RbcsException.of("[源账号]、[目标账号]不能同时为空");
        }
        List<Trade> dbList = super.list(new LambdaQueryWrapper<Trade>()
                .eq(Trade::getUuid, tradeParam.getUuid()));
        if (CollectionUtils.isNotEmpty(dbList)) {
            throw RbcsException.of(String.format("[交易id:%s]已存在", tradeParam.getUuid()));
        }
        return true;
    }
}
