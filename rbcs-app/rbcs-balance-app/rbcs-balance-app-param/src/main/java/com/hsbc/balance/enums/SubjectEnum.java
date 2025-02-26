package com.hsbc.balance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SubjectEnum {
    CNY("CNY", "人民币"),
    ;

    private final String code;

    private final String name;

}
