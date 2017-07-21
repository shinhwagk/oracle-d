select to_char(s.startup_time,'dd Mon "at" HH24:mi:ss')  instart_fmt
     , di.dbid                                           dbid
     , di.instance_name                                  inst_name
     , di.db_name                                        db_name
     , s.snap_id                                         snap_id
     , to_char(s.end_interval_time,'dd Mon YYYY HH24:mi') snapdat
     , s.snap_level                                      lvl
  from dba_hist_snapshot s
     , dba_hist_database_instance di
 where s.dbid              = ?
   and di.dbid             = ?
   and s.instance_number   = ?
   and di.instance_number  = ?
   and di.dbid             = s.dbid
   and di.instance_number  = s.instance_number
   and di.startup_time     = s.startup_time
 order by db_name, instance_name, snap_id