package com.hsbc.balance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hsbc.balance.entity.Trade;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ITradeMapper extends BaseMapper<Trade> {
}
