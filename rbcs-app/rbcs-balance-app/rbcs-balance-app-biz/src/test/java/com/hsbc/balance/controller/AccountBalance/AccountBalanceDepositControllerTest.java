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


@DisplayName("账户余额-存钱")
class AccountBalanceDepositControllerTest extends BaseControllerTest {

    // @todo    往一个账号存入N(N>=2)笔
    @SneakyThrows
    @Test
    @DisplayName("交易成功：交易id、时间戳、目标账号(2025030114525900001-提前创建)、金额(55元人民币)不能为空")
    void addTrade_success_deposit_success() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .descAccountUuno("2025030114525900001")
                .amount(Long.valueOf(55 * 100 * 10000))
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

    // @todo    目标账号不存在
    @SneakyThrows
    @Test
    @DisplayName("插入交易记录成功but交易失败：交易id、时间戳、目标账号(2025030114525900031-冻结)、金额(1000元人民币)不能为空")
    void addTrade_success_deposit_fail() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .descAccountUuno("2025030114525900031")
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

    // @todo    交易id(uuid)为空、时间戳(uuno)为空、目标账号(descAccountUuno)为空；交易id(uuid)重复
    @SneakyThrows
    @Test
    @DisplayName("插入交易失败：金额为空")
    void addTrade_fail_deposit_fail() {
        TradeParam tradeParam = TradeParam.builder()
                .uuid(IdWorkerUtils.nextId())
                .uuno(String.valueOf(System.currentTimeMillis()))
                .descAccountUuno("2025030114525900031")
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