package com.hsbc.common.base;

public interface ErrorCode {
    int getPort();

    String getMenuOrder();

    String getErrorOrder();

    default long getErrCode() {
        return Long.parseLong(getPort() + getMenuOrder() + getErrorOrder());
    }

    String getErrMsg();
}
