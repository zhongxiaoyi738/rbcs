package com.hsbc.balance.controller;

import com.hsbc.balance.manager.IAccountBalanceManager;
import com.hsbc.balance.param.TradeParam;
import com.hsbc.common.base.RbcsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/accountBalance")
@Tag(name = "账户余额")
public class AccountBalanceController {

    @Autowired
    private IAccountBalanceManager accountBalanceManager;

    @Operation(summary = "余额计算")
    @PostMapping("/addTrade")
    public RbcsResponse<Integer> addTrade(@RequestBody @Valid TradeParam tradeParam) {
        return RbcsResponse.success(accountBalanceManager.addTrade(tradeParam));
    }

}
