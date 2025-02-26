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
@TableName(value = "account", autoResultMap = true)
@Schema(name = "Account", description = "账户信息表")
public class Account extends RbcsBaseEntity {

    @Serial
    private static final long serialVersionUID = -2288456582052289406L;

    @TableField("uuno")
    private String uuno;

    @TableField("status")
    private String status;
}
