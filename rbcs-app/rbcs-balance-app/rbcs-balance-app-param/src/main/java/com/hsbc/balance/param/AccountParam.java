package com.hsbc.balance.param;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "账户信息查询-入参", description = "账户信息查询-入参")
public class AccountParam implements Serializable {
    @Serial
    private static final long serialVersionUID = -6210920619698242416L;

    @Schema(description = "唯一标识")
    private Long uuid;

    @Schema(description = "账号")
    private String uuno;
}
