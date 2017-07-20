package org.shinhwagk.od.awr.single

import java.sql.PreparedStatement

import org.shinhwagk.od.common.{ConnectInfo, DatabaseOperation, Tools}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AwrReportOperation {
  val awrReportHtml: String = "script\\awr\\single\\10g\\generate_html.sql"
  val awrReportText: String = "script\\awr\\single\\10g\\generate_text.sql"

  def setBindVariable(dbid: Long, inum: Int, bid: Int, eid: Int)(stmt: PreparedStatement) = {
    stmt.setLong(1, dbid)
    stmt.setInt(2, inum)
    stmt.setInt(3, bid)
    stmt.setInt(4, eid)
  }

  def generateAwr(name: String, dbid: Long, instnum: Int, bid: Int, eid: Int, mode: String): Future[String] = {

    val reportSqlFile = if (mode.toLowerCase == "html") awrReportHtml else awrReportText

    for {
      ci <- Future.successful(ConnectInfo("10.65.193.25", 1521, "orayali2", "system", "oracle"))
      report <- DatabaseOperation.sqlQuery(ci, Tools.readSqlFile(reportSqlFile), DatabaseOperation.processResultSetMultipleLine, Some(setBindVariable(dbid, instnum, bid, eid)))
    } yield report
  }

  def generateAwrText() = {

  }

}

case class AwrReport(name: String, dbId: Long, instNum: Long, bId: Long, eId: Long)