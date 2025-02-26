package com.hsbc.balance.service;

import com.hsbc.balance.entity.AccountBalance;
import com.hsbc.balance.param.AccountBalanceParam;
import com.hsbc.common.base.IRbcsBaseService;

public interface IAccountBalanceService extends IRbcsBaseService<AccountBalance> {

    Integer updateSrc(AccountBalanceParam accountBalanceParam);

    Integer updateDesc(AccountBalanceParam accountBalanceParam);

}
