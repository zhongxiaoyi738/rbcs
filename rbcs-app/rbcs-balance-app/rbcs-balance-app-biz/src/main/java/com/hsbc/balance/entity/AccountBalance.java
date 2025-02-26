package com.hsbc.balance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hsbc.common.base.RbcsBaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import java.io.Serial;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@TableName(value = "account_balance", autoResultMap = true)
@Schema(name = "AccountBalance", description = "账户余额表")
public class AccountBalance extends RbcsBaseEntity {

    @Serial
    private static final long serialVersionUID = -2288456582052289406L;

    @TableField("account_uuid")
    private Long accountUuid;

    @TableField("account_uuno")
    private String accountUuno;

    @TableField("subject")
    private String subject;

    @TableField("balance")
    private Long balance;
}
