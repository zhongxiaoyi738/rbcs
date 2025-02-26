package com.hsbc.balance.service;

import com.hsbc.balance.entity.Account;
import com.hsbc.balance.param.AccountParam;
import com.hsbc.balance.vo.AccountVo;
import com.hsbc.common.base.IRbcsBaseService;

public interface IAccountService extends IRbcsBaseService<Account> {

    AccountVo selectOne(AccountParam accountParam);
}
