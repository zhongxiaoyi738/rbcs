package com.hsbc.balance.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Builder
@Schema(name = "实时余额查询-入参", description = "实时余额查询-入参")
public class AccountBalanceParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 7565569340923967477L;

    @Schema(description = "账户唯一标识")
    private Long accountUuid;

    @Schema(description = "账号")
    private String accountUuno;

    @Schema(description = "科目（币种）")
    private String subject;

    @Schema(description = "账户科目余额")
    private Long balance;

    @Schema(description = "金额")
    private Long amount;

}
