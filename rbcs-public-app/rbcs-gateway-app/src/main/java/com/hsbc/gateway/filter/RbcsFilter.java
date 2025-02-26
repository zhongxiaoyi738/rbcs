package com.hsbc.gateway.filter;

import com.hsbc.gateway.constants.FilterOrdersConstants;
import com.hsbc.log.enums.FeignRequestHeaderEnum;
import com.hsbc.log.idworker.IdWorkerUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class RbcsFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Long traceId = IdWorkerUtils.getTraceId();
        MDC.put(FeignRequestHeaderEnum.TRACE_ID.getCode(), traceId.toString());
        // @todo 解析token获取用户账号
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrdersConstants.RBCS;
    }
}
