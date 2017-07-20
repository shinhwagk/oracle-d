SELECT TO_CHAR(s.startup_time,'YY/MM/DD HH24:mi') instart_fmt,
       di.instance_name inst_name,
       di.db_name db_name,
       s.snap_id snap_id,
       TO_CHAR(s.end_interval_time,'YY/MM/DD HH24:mi') snapdat,
       s.snap_level lvl
FROM dba_hist_snapshot s,
     dba_hist_database_instance di
WHERE s.dbid = ?
  AND di.dbid = ?
  AND s.instance_number = ?
  AND di.instance_number = ?
  AND di.dbid = s.dbid
  AND di.instance_number = s.instance_number
  AND di.startup_time = s.startup_time
  AND s.end_interval_time >= DECODE(?, 0, to_date('31-01-9999','DD-MM-YYYY'), 3.14, s.end_interval_time,
                                      (SELECT TRUNC(MAX(end_interval_time), 'dd') - ? + 1
                                       FROM dba_hist_snapshot
                                       WHERE instance_number = ?
                                         AND dbid = ? ))
ORDER BY db_name,
         instance_name,
         snap_id