package com.hsbc.balance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradeStatusEnum {
    WAITING("WAITING", "待处理"),
    DOING("DOING", "处理中"),
    SUCCESS("SUCCESS", "成功"),
    FAIL("FAIL", "失败"),
    ;

    private final String code;

    private final String name;
}
