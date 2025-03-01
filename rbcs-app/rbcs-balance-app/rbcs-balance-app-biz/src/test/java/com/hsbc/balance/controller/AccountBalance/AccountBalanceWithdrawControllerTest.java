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


@DisplayName("账户余额-取钱")
class AccountBalanceWithdrawControllerTest extends BaseControllerTest {

    // @todo    往一个账号存入N(N>=2)笔
    @SneakyThrows
    @Test
    @DisplayName("交易成功：交易id、时间戳、源账号(2025030114525900001)、金额(100元人民币)<=账号余额")
    void addTrade_success_deposit_success() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .srcAccountUuno("2025030114525900001")
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

    // @todo 源账号不存在；金额>账号余额
    @SneakyThrows
    @Test
    @DisplayName("插入交易记录成功but交易失败：交易id、时间戳、源账号(2025030114525900031-冻结)、金额(1000元人民币)<=账号余额")
    void addTrade_success_deposit_fail() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .srcAccountUuno("2025030114525900031")
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

    // @todo    交易id(uuid)为空、时间戳(uuno)为空、源账号(srcAccountUuno)、金额(amount)为空；交易id(uuid)重复
    @SneakyThrows
    @Test
    @DisplayName("插入交易失败：金额为0")
    void addTrade_fail_deposit_fail() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .srcAccountUuno("2025030114525900031")
                .amount(0l)
                .build();
        mockMvc.perform(post("/accountBalance/addTrade")
                        .contentType(MediaType.APPLICATION_JSON)
                        .headers(this.headers)
                        .content(JsonUtils.toJson(tradeParam)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(CommonErrorCode.REQUIRED_PARAM_NULL.getErrCode()))
                .andDo(print());
    }
}