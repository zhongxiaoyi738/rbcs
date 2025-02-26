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
@TableName(value = "trade", autoResultMap = true)
@Schema(name = "Trade", description = "交易流水表")
public class Trade extends RbcsBaseEntity {

    @Serial
    private static final long serialVersionUID = 305635651775350808L;

    @TableField("uuno")
    private String uuno;

    @TableField("src_account_uuno")
    private String srcAccountUuno;

    @TableField("desc_account_uuno")
    private String descAccountUuno;

    @TableField("amount")
    private Long amount;

    @TableField("status")
    private String status;

    @TableField("fail_reason")
    private String failReason;

}
