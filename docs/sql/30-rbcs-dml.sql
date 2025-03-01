USE rbcs;



INSERT INTO rbcs_log(uuid, uuno, trace_id, created_by, updated_by, remark)
SELECT uuid_short(),
       'sql_20250301',
       ifnull(tmp.max_trace_id, 0) + 1,
       'xiaoyi',
       'xiaoyi',
       '实时余额计算系统，mock数据'
FROM (SELECT max(trace_id) max_trace_id
      FROM rbcs_log) tmp;



SET SESSION cte_max_recursion_depth = 100000;


INSERT INTO account(uuid, uuno, status, trace_id, created_by, updated_by)
SELECT uuid_short()                                     uuid,
       CONCAT('20250301145259', LPAD(seq_no, 5, '0'))   uuno,
       IF(MOD(seq_no, 31)=0, 'FREEZE', 'NORMAL')        status,
       l.trace_id, l.created_by, l.updated_by
  FROM (WITH RECURSIVE numbers AS (
            SELECT 1 AS seq_no
            UNION ALL
            SELECT seq_no + 1 FROM numbers WHERE seq_no < 99999)
        SELECT seq_no FROM numbers) tmp,
       rbcs_log l
 WHERE l.uuno = 'sql_20250301';

INSERT INTO account_balance(uuid, account_uuid, account_uuno, subject, balance, trace_id, created_by, updated_by)
SELECT uuid_short()         uuid,
       a.uuid               account_uuid,
       a.uuno               account_uuno,
       IF(MOD(a.uuno, 11)=0, 'USD', 'CNY') subject,
       mod(floor(RAND(a.uuno) * 1000000000), floor(RAND(a.uuno) * 100000)) balance,
       a.trace_id,
       a.created_by,
       a.updated_by
  FROM account a
 WHERE MOD(a.uuno, 7) != 0;


INSERT INTO account_his SELECT * FROM account WHERE trace_id = (SELECT trace_id FROM rbcs_log WHERE uuno = 'sql_20250301');
INSERT INTO account_balance_his SELECT * FROM account_balance WHERE trace_id = (SELECT trace_id FROM rbcs_log WHERE uuno = 'sql_20250301');
