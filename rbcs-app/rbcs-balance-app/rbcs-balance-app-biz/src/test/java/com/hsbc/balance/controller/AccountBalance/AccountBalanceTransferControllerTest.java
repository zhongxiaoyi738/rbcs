package com.hsbc.balance.controller.AccountBalance;

import com.hsbc.balance.controller.BaseControllerTest;
import com.hsbc.balance.param.TradeParam;
import com.hsbc.common.constant.CommonErrorCode;
import com.hsbc.common.utils.JsonUtils;
import com.hsbc.log.idworker.IdWorkerUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@DisplayName("账户余额-转账")
class AccountBalanceTransferControllerTest extends BaseControllerTest {

    // @todo    往一个账号存入N(N>=2)笔
    @SneakyThrows
    @Test
    @DisplayName("交易成功：交易id、时间戳、源账号(2025030114525900001)、目标账号(2025030114525900002)、金额(100元人民币)<=源账号余额")
    void addTrade_success_transfer_success() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .srcAccountUuno("2025030114525900001")
                .descAccountUuno("2025030114525900002")
                .amount(Long.valueOf(100 * 100 * 10000))
                .build();
        mockMvc.perform(post("/accountBalance/addTrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(this.headers)
                        .content(JsonUtils.toJson(tradeParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andDo(print());
    }

    // @todo 源账号不存在；目标账号不存在、目标账号冻结；金额>源账号余额
    @SneakyThrows
    @Test
    @DisplayName("插入交易记录成功but交易失败：交易id、时间戳、源账号(2025030114525900031-冻结)、目标账号(2025030114525900002-正常)、金额(1000元人民币)<=源账号余额")
    void addTrade_success_transfer_fail() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .srcAccountUuno("2025030114525900031")
                .descAccountUuno("2025030114525900002")
                .amount(Long.valueOf(1000 * 100 * 10000))
                .build();
        mockMvc.perform(post("/accountBalance/addTrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(this.headers)
                        .content(JsonUtils.toJson(tradeParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNumber())
                .andDo(print());
    }

}