package org.shinhwagk.od.keyperformance

object SqlFiles {

  private val sqlCode =
    """
      |SELECT inst_id ,
      |    (select count(*) from gv$session where inst_id = b.inst_id) SESS,
      |    (select count(*) from gv$session where inst_id = b.inst_id) SESS,
      |    (select count(*) from gv$session where status = 'ACTIVE' and inst_id = b.inst_id and username is not null) ASESS,
      |    (select value from gv$osstat where inst_id = b.inst_id and stat_name = 'BUSY_TIME') BT,
      |    (select value from gv$osstat where inst_id = b.inst_id and stat_name = 'IDLE_TIME') IT,
      |    (SELECT value FROM gv$sys_time_model WHERE b.inst_id = inst_id AND stat_name = 'DB time') DBT,
      |    (SELECT value FROM gv$sys_time_model WHERE b.inst_id = inst_id AND stat_name = 'DB CPU') DBCPU,
      |    (SELECT value FROM gv$sys_time_model WHERE b.inst_id = inst_id AND stat_name = 'sql execute elapsed time') SELA,
      |    (SELECT value FROM gv$sys_time_model WHERE b.inst_id = inst_id AND stat_name = 'parse time elapsed') SPELA,
      |    (SELECT value FROM gv$sys_time_model WHERE b.inst_id = inst_id AND stat_name = 'hard parse elapsed time') SHPELA
      |FROM gv$instance b
      |GROUP BY inst_id
      |ORDER BY inst_id
    """.stripMargin

  val instMetricSql = List(10, 11, 12).map(_ -> sqlCode).toMap
}
