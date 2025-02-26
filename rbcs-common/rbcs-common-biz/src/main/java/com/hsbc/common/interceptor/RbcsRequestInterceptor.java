package com.hsbc.common.interceptor;

import com.hsbc.log.enums.FeignRequestHeaderEnum;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;


/**
 * Feign请求拦截器，用于在每个Feign请求中添加特定的请求头。
 * 这些请求头包括追踪ID以及一些标志位信息。
 * 通过拦截器，可以在不修改调用方代码的情况下，统一管理请求头的发送。
 *
 * @Author 钟凤娟
 * @Date 2025/02/22 19:30
 */
@Slf4j
@Component
public class RbcsRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, String> headers = getHeaders();
        headers.forEach(requestTemplate::header);
    }

    public static Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<>();
        MDC.get(FeignRequestHeaderEnum.TRACE_ID.getCode());
        headers.put(FeignRequestHeaderEnum.TRACE_ID.getCode(), MDC.get(FeignRequestHeaderEnum.TRACE_ID.getCode()));
        headers.put(FeignRequestHeaderEnum.USER_ACCOUNT_NAME.getCode(), MDC.get(FeignRequestHeaderEnum.USER_ACCOUNT_NAME.getCode()));
        return headers;
    }
}
