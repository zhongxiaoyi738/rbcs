package com.hsbc.balance.mapstruct;

import com.hsbc.balance.entity.Account;
import com.hsbc.balance.vo.AccountVo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapStruct {
    AccountVo entityToVo(Account account);
}
