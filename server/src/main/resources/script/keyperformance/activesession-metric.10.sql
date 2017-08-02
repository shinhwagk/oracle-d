select
  SID,
  SERIAL#,
  substr(username,1,15) || '..' USERN,
  DECODE(program, null, ' ', substr(program,1,15) || '..') prog,
  DECODE(EVENT, null, ' ', substr(EVENT,1,40) || '..') event,
  DECODE(LAST_CALL_ET, null, 0, LAST_CALL_ET) duration,
  DECODE(SQL_ID, null, decode(PREV_SQL_ID, null, ' ', a.PREV_SQL_ID || '/' || decode(PREV_CHILD_NUMBER, null, ' ', PREV_CHILD_NUMBER) || '*'),SQL_ID||'/'||to_char(decode(SQL_CHILD_NUMBER,null,' ',SQL_CHILD_NUMBER)))
FROM GV$SESSION