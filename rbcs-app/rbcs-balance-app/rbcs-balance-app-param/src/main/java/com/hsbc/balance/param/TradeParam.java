package com.hsbc.balance.param;

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
@Schema(name = "实时余额计算-入参", description = "实时余额计算-入参")
public class TradeParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -4417426577607089898L;

    @NotNull(message = "[交易id]不能为空，请检查！")
    @Schema(description = "交易id", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long uuid;

    @NotBlank(message = "[时间戳]不能为空，请检查！")
    @Schema(description = "时间戳", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uuno;

    @Schema(description = "源账号")
    private String srcAccountUuno;

    @Schema(description = "目标账号")
    private String descAccountUuno;

    @NotNull(message = "[金额]不能为空，请检查！")
    @Schema(description = "金额", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long amount;
}
