package com.hsbc.balance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsbc.balance.entity.AccountBalance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IAccountBalanceMapper extends BaseMapper<AccountBalance> {
}
