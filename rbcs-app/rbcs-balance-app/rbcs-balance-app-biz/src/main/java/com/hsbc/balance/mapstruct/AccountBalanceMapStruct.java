package com.hsbc.balance.mapstruct;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hsbc.balance.entity.AccountBalance;
import com.hsbc.balance.param.AccountBalanceParam;
import com.hsbc.balance.vo.AccountBalanceVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountBalanceMapStruct {

    Page<AccountBalanceVo> pageEntityToVo(IPage<AccountBalance> accountBalance);

    AccountBalance paramToEntity(AccountBalanceParam accountBalanceParam);
}
