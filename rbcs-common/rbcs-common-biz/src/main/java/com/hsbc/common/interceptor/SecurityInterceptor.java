package com.hsbc.common.interceptor;

import com.hsbc.common.base.RbcsException;
import com.hsbc.common.constant.CommonErrorCode;
import com.hsbc.log.enums.FeignRequestHeaderEnum;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // 从请求头中获取TRACE_ID
        String traceId = request.getHeader(FeignRequestHeaderEnum.TRACE_ID.getCode());
        if (StringUtils.isNotEmpty(traceId)) {
            MDC.put(FeignRequestHeaderEnum.TRACE_ID.getCode(), traceId);
            String userAccountName = request.getHeader(FeignRequestHeaderEnum.USER_ACCOUNT_NAME.getCode());
            if (StringUtils.isNotEmpty(traceId)) {
                MDC.put(FeignRequestHeaderEnum.USER_ACCOUNT_NAME.getCode(), userAccountName);
            }
            return true;
        }
        else {
            throw RbcsException.of(CommonErrorCode.GATEWAY_REQUEST_ERROR);
        }
    }
}
