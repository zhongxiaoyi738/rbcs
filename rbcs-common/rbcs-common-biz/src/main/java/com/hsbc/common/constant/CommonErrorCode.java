package com.hsbc.common.constant;

import com.hsbc.common.base.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    OK(200, "", "", "api调用成功"),

    SERVICE_ERROR(9000, "000", "001", "内部服务异常"),
    PARAM_INVALID_ERROR(9000, "000", "002", "参数非法"),
    SERVICE_BUSY(9000, "000", "003", "服务器繁忙"),
    REQUIRED_PARAM_NULL(9000, "001", "004", "空指针"),

    GATEWAY_REQUEST_ERROR(9000, "002", "001", "请通过网关访问资源"),

    INIT_ERROR(9000, "003", "001", "初始化消息队列异常"),
    ;


    final int port;

    final String menuOrder;

    final String errorOrder;

    final String errMsg;
}
