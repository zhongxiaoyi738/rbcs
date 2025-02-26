package com.hsbc.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SendStatusEnum {
    SEND_INTERRUPT(-1, "发送中止"),
    SEND_OK(1, "发送成功"),
    SEND_FAIL(2, "发送失败"),
    SEND_ERROR(3, "发送异常"),
    ;

    private final Integer code;

    private final String name;
}
