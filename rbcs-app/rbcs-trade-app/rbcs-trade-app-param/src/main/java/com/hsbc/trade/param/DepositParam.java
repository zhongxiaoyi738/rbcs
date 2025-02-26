package com.hsbc.trade.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
@Schema(name = "存钱-入参", description = "存钱-入参")
public class DepositParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -4660608004114166441L;

    @NotBlank(message = "[时间戳]不能为空，请检查！")
    @Schema(description = "时间戳", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuno;

    @NotBlank(message = "[账号]不能为空，请检查！")
    @Schema(description = "账号")
    private String accountUuno;

    @Schema(description = "科目（币种）", defaultValue = "CNY")
    private String subject;

    @NotNull(message = "[金额]不能为空，请检查！")
    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double amount;

}
