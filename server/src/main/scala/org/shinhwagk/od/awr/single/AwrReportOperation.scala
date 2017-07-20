package org.shinhwagk.od.awr.single

import java.sql.PreparedStatement

import oracle.jdbc.pool.OracleDataSource
import org.shinhwagk.od.common.{DatabaseOperation, OracleJdbcConvert, Tools}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AwrReportOperation {

  import OracleJdbcConvert._

  val awrReportHtml: String = "script\\awr\\single\\10g\\generate_html.sql"
  val awrReportText: String = "script\\awr\\single\\10g\\generate_text.sql"

  def setBindVariable(dbid: Long, inum: Int, bid: Int, eid: Int)(stmt: PreparedStatement) = {
    stmt.setLong(1, dbid)
    stmt.setInt(2, inum)
    stmt.setInt(3, bid)
    stmt.setInt(4, eid)
  }

  def generateAwr(name: String, dbid: Long, instnum: Int, bid: Int, eid: Int, mode: String): Future[String] = {
    import DatabaseOperation._

    val sqlCode = Tools.readSqlFile(if (mode.toLowerCase == "html") awrReportHtml else awrReportText)

    val ods: OracleDataSource = ???

    val f = selectQuery(sqlCode, processResultSetMultipleLine, Some(setBindVariable(dbid, instnum, bid, eid)))

    for {
      conn <- Future.successful(ods.getConnection)
      report <- usedOracleConnect(conn, f, true)
    } yield report
  }

  def generateAwrText() = {

  }

}

case class AwrReport(name: String, dbId: Long, instNum: Long, bId: Long, eId: Long)