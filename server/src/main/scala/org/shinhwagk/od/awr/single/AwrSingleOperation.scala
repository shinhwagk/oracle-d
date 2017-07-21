package org.shinhwagk.od.awr.single

import java.sql.PreparedStatement

import oracle.jdbc.OracleConnection
import org.shinhwagk.od.common.{DatabaseOperation, OracleJdbcConvert, Tools}
import org.shinhwagk.od.lib.Libs
import spray.json.{JsArray, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AwrSingleOperation {

  import org.shinhwagk.od.AkkaSystem._
  import OracleJdbcConvert._

  val instanceSqlFilePath: String = "script\\awr\\single\\10g\\instance.sql"
  val snapshotsSqlFilePath: String = "script\\awr\\single\\10g\\snapshots.sql"

  val awrReportHtml: String = "script\\awr\\single\\10g\\generate_html.sql"
  val awrReportText: String = "script\\awr\\single\\10g\\generate_text.sql"

  def getSnapshots(name: String, days: Int): Future[JsArray] = {
    import DatabaseOperation._
    import MyJsonProtocol._
    import OracleJdbcConvert._
    import Tools._

    val instanceSqlCode = readSqlFile(instanceSqlFilePath)
    val snapshotsSqlCode = readSqlFile(snapshotsSqlFilePath)

    def setSnapshotsBindVariable(instance: InstanceInfo, days: Int)(stmt: PreparedStatement) = {
      stmt.setLong(1, instance.DBID)
      stmt.setLong(2, instance.DBID)
      stmt.setInt(3, instance.INST_NUM)
      stmt.setInt(4, instance.INST_NUM)
      stmt.setInt(5, days)
      stmt.setInt(6, days)
      stmt.setInt(7, instance.INST_NUM)
      stmt.setLong(8, instance.DBID)
    }

    def jsValueToInstance(j: JsValue) = j.convertTo[InstanceInfo]

    val f = selectQuery(instanceSqlCode, processResultSetOneRow _ andThen jsValueToInstance _ , None) _

    val f1 = (instance: InstanceInfo) =>
      selectQuery(snapshotsSqlCode, processResultSetToJson, Some(setSnapshotsBindVariable(instance, days))) _

    for {
      conn <- Libs.getDataSource(name).map(_.getConnection)
      instance <- usedOracleConnect(conn, f)
      j <- usedOracleConnect(conn, f1(instance), true)
    } yield {
      j.convertTo[JsArray]
    }
  }

  def setAwrBindVariable(dbid: Long, inum: Int, bid: Int, eid: Int)(stmt: PreparedStatement) = {
    stmt.setLong(1, dbid)
    stmt.setInt(2, inum)
    stmt.setInt(3, bid)
    stmt.setInt(4, eid)
  }

  def generateAwr(name: String, dbid: Long, itnum: Int, bid: Int, eid: Int, mode: String): Future[String] = {
    import DatabaseOperation._

    val sqlCode = Tools.readSqlFile(if (mode.toLowerCase == "html") awrReportHtml else awrReportText)

    val f: (OracleConnection) => String = selectQuery(sqlCode, processResultSetMultipleLine, Some(setAwrBindVariable(dbid, itnum, bid, eid))) _

    for {
      ods <- Libs.getDataSource(name)
      report <- usedOracleConnect(ods.getConnection, f, true)
    } yield report
  }

}