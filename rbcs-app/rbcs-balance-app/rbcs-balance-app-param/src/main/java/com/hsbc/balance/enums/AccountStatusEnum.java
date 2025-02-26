package com.hsbc.balance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountStatusEnum {
    NORMAL("NORMAL", "正常"),
    FREEZE("FREEZE", "冻结"),
    ;

    private final String code;

    private final String name;


}
