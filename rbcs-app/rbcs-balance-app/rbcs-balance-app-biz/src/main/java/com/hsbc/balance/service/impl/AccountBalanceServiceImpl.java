package com.hsbc.balance.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hsbc.balance.entity.AccountBalance;
import com.hsbc.balance.mapper.IAccountBalanceMapper;
import com.hsbc.balance.mapstruct.AccountBalanceMapStruct;
import com.hsbc.balance.param.AccountBalanceParam;
import com.hsbc.balance.service.IAccountBalanceService;
import com.hsbc.common.base.RbcsBaseServiceImpl;
import com.hsbc.common.base.RbcsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AccountBalanceServiceImpl extends RbcsBaseServiceImpl<IAccountBalanceMapper, AccountBalance> implements IAccountBalanceService {

    @Autowired
    private AccountBalanceMapStruct accountBalanceMapStruct;

    @Override
    public Integer updateSrc(AccountBalanceParam accountBalanceParam) {
        int retVal = super.updateWrapper(accountBalanceMapStruct.paramToEntity(accountBalanceParam),
                new LambdaUpdateWrapper<AccountBalance>()
                        .eq(AccountBalance::getAccountUuid, accountBalanceParam.getAccountUuid())
                        .eq(AccountBalance::getSubject, accountBalanceParam.getSubject())
                        .ge(AccountBalance::getBalance, accountBalanceParam.getAmount())
                        .setDecrBy(AccountBalance::getBalance, accountBalanceParam.getAmount()));
        if (retVal <= 0) {
            throw RbcsException.of(String.format("[源账号:{} 扣款:{}]失败", accountBalanceParam.getAccountUuno(), accountBalanceParam.getAmount()));
        }
        return retVal;
    }

    @Override
    public Integer updateDesc(AccountBalanceParam accountBalanceParam) {
        int retVal = super.updateWrapper(accountBalanceMapStruct.paramToEntity(accountBalanceParam),
                new LambdaUpdateWrapper<AccountBalance>()
                        .eq(AccountBalance::getAccountUuid, accountBalanceParam.getAccountUuid())
                        .eq(AccountBalance::getSubject, accountBalanceParam.getSubject())
                        .setIncrBy(AccountBalance::getBalance, accountBalanceParam.getAmount()));
        if (retVal <= 0) {
            throw RbcsException.of(String.format("[目标账号:{} 入账:{}]失败", accountBalanceParam.getAccountUuno(), accountBalanceParam.getAmount()));
        }
        return retVal;
    }
}
