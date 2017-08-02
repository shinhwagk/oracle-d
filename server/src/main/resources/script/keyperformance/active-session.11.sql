select to_char(a.sid) sid,
       serial#,
       username,
       DECODE(program, null, ' ', substr(program, 1, 15) || '..') program,
       DECODE(EVENT, null, ' ', substr(EVENT, 1, 40) || '..') event,
       DECODE(LAST_CALL_ET, null, 0, LAST_CALL_ET) duration,
       DECODE(a.SQL_ID,
              null,
              decode(a.PREV_SQL_ID,
                     null,
                     ' ',
                     a.PREV_SQL_ID || '/' ||
                     decode(a.PREV_CHILD_NUMBER,
                            null,
                            ' ',
                            a.PREV_CHILD_NUMBER) || '*'),
              a.SQL_ID || '/' ||
              to_char(decode(a.SQL_CHILD_NUMBER,
                             null,
                             ' ',
                             a.SQL_CHILD_NUMBER))) SQLID_CHN,
       (CASE
         when b.executions is null or b.executions = 0 then
          decode(b.elapsed_time, null, ' ', to_char(b.elapsed_time))
         ELSE
          decode(b.elapsed_time,
                 null,
                 'A',
                 to_char(round(b.elapsed_time / b.executions / 1000000, 2)))
       END) AVELA,
                      DECODE(BLOCKING_INSTANCE,
                      null,
                      ' ',
                      to_char(BLOCKING_INSTANCE) || '/' ||
                      to_char(decode(BLOCKING_SESSION,
                                     null,
                                     ' ',
                                     BLOCKING_SESSION))) BLKIS
  from (select *
          from gv$session
         where status = 'ACTIVE'
           and username not in ('SYS', 'SYSTEM')
           and username is not null
        union all
        select b.*
          from gv$lock a, gv$session b
         where a.inst_id = b.inst_id
           and a.sid = b.sid
           and a.block = 1
           and username not in ('SYS', 'SYSTEM')
           and username is not null) a,
       gv$sql b
 where a.inst_id = b.inst_id(+)
   and a.sql_id = b.sql_id(+)