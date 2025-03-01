package com.hsbc.balance.manager;

import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.param.TradeParam;

public interface IAccountBalanceManager {

    Integer addTrade(TradeParam tradeParam);

    Integer modifyAccountBalance(Trade trade, String statusAfterFail);

}
