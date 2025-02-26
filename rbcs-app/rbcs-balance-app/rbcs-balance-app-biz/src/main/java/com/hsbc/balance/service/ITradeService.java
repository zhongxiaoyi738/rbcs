package com.hsbc.balance.service;

import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.param.TradeParam;
import com.hsbc.common.base.IRbcsBaseService;

public interface ITradeService extends IRbcsBaseService<Trade> {

    Trade insert(TradeParam tradeParam);

    Integer updateStatus(Trade trade, String newStatus);
}
