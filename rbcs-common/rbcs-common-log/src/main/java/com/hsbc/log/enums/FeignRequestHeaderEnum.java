package com.hsbc.log.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeignRequestHeaderEnum {
    TRACE_ID("traceId", "全局日志id, 生成规则见IdWorker"),
    USER_ACCOUNT_NAME("userAccountName", "用户账号"),
    CLIENT_IP("clientIp", "客户端id"),
    ;

    private final String code;

    private final String name;
}
