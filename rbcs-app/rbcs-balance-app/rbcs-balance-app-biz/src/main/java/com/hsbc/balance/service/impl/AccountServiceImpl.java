package com.hsbc.balance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hsbc.balance.entity.Account;
import com.hsbc.balance.enums.AccountStatusEnum;
import com.hsbc.balance.mapper.IAccountMapper;
import com.hsbc.balance.mapstruct.AccountMapStruct;
import com.hsbc.balance.mapstruct.TradeMapStruct;
import com.hsbc.balance.param.AccountParam;
import com.hsbc.balance.service.IAccountService;
import com.hsbc.balance.vo.AccountVo;
import com.hsbc.common.base.RbcsBaseServiceImpl;
import com.hsbc.common.base.RbcsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AccountServiceImpl extends RbcsBaseServiceImpl<IAccountMapper, Account> implements IAccountService {

    @Autowired
    private AccountMapStruct accountMapStruct;

    @Override
    public AccountVo selectOne(AccountParam accountParam) {
        List<Account> dbList = super.list(new LambdaQueryWrapper<Account>()
                .eq(Account::getUuno, accountParam.getUuno())
                .eq(Account::getStatus, AccountStatusEnum.NORMAL.getCode()));
        if (CollectionUtils.isEmpty(dbList)) {
            throw RbcsException.of(String.format("[账号%s]不存在或状态不正常", accountParam.getUuno()));
        }
        if (dbList.size() > 1) {
            throw RbcsException.of(String.format("[账号%s]存在%s条", accountParam.getUuno(), dbList.size()));
        }
        return accountMapStruct.entityToVo(dbList.get(0));
    }
}
