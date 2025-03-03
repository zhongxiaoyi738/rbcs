/**
回滚
DROP TABLE IF EXISTS rbcs_log;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS account_his;
DROP TABLE IF EXISTS account_balance;
DROP TABLE IF EXISTS account_balance_his;
DROP TABLE IF EXISTS trade;
DROP TABLE IF EXISTS trade_his;
 */


USE rbcs;



CREATE TABLE IF NOT EXISTS rbcs_log
(
    id                   INT 			    NOT NULL  	AUTO_INCREMENT PRIMARY KEY COMMENT '主键id',
    uuid                 BIGINT             NOT NULL    COMMENT '唯一标识',
    uuno                 VARCHAR(256)                   COMMENT '业务唯一标识',
    trace_id             BIGINT 			NOT NULL  	COMMENT '链路跟踪id',
    created_by           VARCHAR(128)       NOT NULL    COMMENT '创建人',
    created_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)  COMMENT '创建时间',
    updated_by           VARCHAR(128)    NOT NULL    COMMENT '修改人',
    updated_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    remark               TEXT  						 COMMENT '备注'
) COMMENT '手工sql日志';


INSERT INTO rbcs_log(uuid, uuno, trace_id, created_by, updated_by, remark)
SELECT floor(uuid_short()/100),
       'sql_20250222',
       ifnull(tmp.max_trace_id, 0) + 1,
       'xiaoyi',
       'xiaoyi',
       '实时余额计算系统，新增表：account|trade|account_balance'
  FROM (SELECT max(trace_id) max_trace_id
          FROM rbcs_log) tmp;



CREATE TABLE IF NOT EXISTS account
(
    id                   INT 			    NOT NULL  	COMMENT '主键id',
    uuid                 BIGINT             NOT NULL    COMMENT '唯一标识',
    uuno                 VARCHAR(256)                   COMMENT '账号',
    status               VARCHAR(32)        NOT NULL    COMMENT '状态，NORMAL:正常,FREEZE:冻结',
    trace_id             BIGINT 			NOT NULL  	COMMENT '链路跟踪id',
    created_by           VARCHAR(128)       NOT NULL    COMMENT '创建人',
    created_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)  COMMENT '创建时间',
    updated_by           VARCHAR(128)    NOT NULL    COMMENT '修改人',
    updated_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    remark               TEXT  						 COMMENT '备注'
) COMMENT '账户信息表';

CREATE TABLE account_his LIKE account;
ALTER TABLE account_his COMMENT '账户信息表_操作流水';

ALTER TABLE account MODIFY id INT AUTO_INCREMENT PRIMARY KEY;
CREATE UNIQUE INDEX unidx1_account ON account (uuid);
CREATE INDEX idx2_account ON account (trace_id);
CREATE INDEX idx3_account ON account (uuno);


CREATE TABLE IF NOT EXISTS account_balance
(
    id                   INT 			    NOT NULL  	COMMENT '主键id',
    uuid                 BIGINT             NOT NULL    COMMENT '唯一标识',
    account_uuid         BIGINT             NOT NULL    COMMENT '账户唯一标识，关联account.uuid',
    account_uuno         VARCHAR(256)       NOT NULL    COMMENT '账号，冗余account.uuno',
    subject              VARCHAR(32)   DEFAULT 'CNY'    COMMENT '科目（币种），暂定为人民币',
    balance              BIGINT                         COMMENT '账户科目余额，subject=人民币时单位为：分/10000',
    trace_id             BIGINT 			NOT NULL  	COMMENT '链路跟踪id',
    created_by           VARCHAR(128)       NOT NULL    COMMENT '创建人',
    created_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)  COMMENT '创建时间',
    updated_by           VARCHAR(128)    NOT NULL    COMMENT '修改人',
    updated_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    remark               TEXT  						 COMMENT '备注'
) COMMENT '账户余额表';

CREATE TABLE account_balance_his LIKE account_balance;
ALTER TABLE account_balance_his COMMENT '账户余额表_操作流水';

ALTER TABLE account_balance MODIFY id INT AUTO_INCREMENT PRIMARY KEY;
CREATE UNIQUE INDEX unidx1_account_balance ON account_balance (uuid);
CREATE INDEX idx2_account_balance ON account_balance (trace_id);
CREATE INDEX idx_account_balance_account_uuno ON account_balance (account_uuno);
CREATE INDEX idx_account_balance_subject ON account_balance (subject);


CREATE TABLE IF NOT EXISTS trade
(
    id                   INT 			    NOT NULL  	COMMENT '主键id',
    uuid                 BIGINT             NOT NULL    COMMENT '交易id',
    uuno                 VARCHAR(256)       NOT NULL    COMMENT '时间戳',
    src_account_uuno     VARCHAR(256)                   COMMENT '源账号',
    desc_account_uuno    VARCHAR(256)                   COMMENT '目标账号',
    amount               BIGINT          DEFAULT 0      COMMENT '金额，目标账号的subject=人民币时单位为：分/10000',
    status               VARCHAR(32)        NOT NULL    COMMENT '状态，WAITING:待处理,DOING:处理中,SUCCESS:成功,FAIL:失败',
    fail_reason          TEXT                           COMMENT '失败原因',
    msg_id               VARCHAR(512)                   COMMENT '消息队列生产者产生的msgid',
    trace_id             BIGINT 			NOT NULL  	COMMENT '链路跟踪id',
    created_by           VARCHAR(128)       NOT NULL    COMMENT '创建人',
    created_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)  COMMENT '创建时间',
    updated_by           VARCHAR(128)    NOT NULL    COMMENT '修改人',
    updated_time         TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3) NULL ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '修改时间',
    remark               TEXT  						 COMMENT '备注'
) COMMENT '交易流水表';

CREATE TABLE trade_his LIKE trade;
ALTER TABLE trade_his COMMENT '交易流水表_操作流水';

ALTER TABLE trade MODIFY id INT AUTO_INCREMENT PRIMARY KEY;
CREATE UNIQUE INDEX unidx1_trade ON trade (uuid);
CREATE INDEX idx2_trade ON trade (trace_id);
CREATE INDEX idx3_trade ON trade (uuno);
CREATE INDEX idx_trade_src ON trade (src_account_uuno);
CREATE INDEX idx_trade_desc ON trade (desc_account_uuno);