package com.hsbc.common.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
public class RbcsBaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 3677589517122408552L;

    /**
     * 主键ID，auto_increment
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 唯一标识，用于其他表关联
     */
    @TableField(value = "uuid")
    private Long uuid;

    /**
     * 链路跟踪id，用于唯一标识实体对象的操作日志。
     */
    @TableField(value = "trace_id")
    private Long traceId;

    @TableField(value = "created_by")
    private String createdBy;

    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    @TableField(value = "updated_by")
    private String updatedBy;

    @TableField(value = "remark")
    private String remark;

    @TableField(exist = false)
    private List<Long> ids;
}
