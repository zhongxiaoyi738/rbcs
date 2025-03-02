package com.hsbc.balance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.balance.mq.consumer.TradeConsumer;
import com.hsbc.log.enums.FeignRequestHeaderEnum;
import com.hsbc.log.idworker.IdWorkerUtils;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
public class BaseControllerTest {
    @MockBean
    private TradeConsumer tradeConsumer;

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    public ObjectMapper objectMapper;

    public HttpHeaders headers;

    // 预留：鉴权所需
//    public TokenInfo tokenInfo;

    @BeforeEach
    public void init() {
        initHttpHeaders();
    }

    private void initHttpHeaders() {
        headers = new HttpHeaders();
        // 模拟网关传递traceId，通过SecurityInterceptor
        headers.add(FeignRequestHeaderEnum.TRACE_ID.getCode(), String.valueOf(IdWorkerUtils.getTraceId()));
    }

}
