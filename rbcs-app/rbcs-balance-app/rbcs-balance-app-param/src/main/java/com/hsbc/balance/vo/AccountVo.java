package com.hsbc.balance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "账户信息查询-出参", description = "账户信息查询-出参")
public class AccountVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 4539790099438828509L;

    @Schema(description = "唯一标识")
    private Long uuid;

    @Schema(description = "账号")
    private String uuno;

    @Schema(description = "状态，取值于字典")
    private String status;

}
