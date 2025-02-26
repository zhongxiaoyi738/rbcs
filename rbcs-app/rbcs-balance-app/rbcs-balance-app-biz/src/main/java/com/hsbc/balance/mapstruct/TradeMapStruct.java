package com.hsbc.balance.mapstruct;

import com.hsbc.balance.entity.Trade;
import com.hsbc.balance.param.TradeParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TradeMapStruct {

    Trade paramToEntity(TradeParam tradeParam);

}
