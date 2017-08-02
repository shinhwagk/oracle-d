package org.shinhwagk.od.topsql

import oracle.jdbc.OracleConnection
import org.shinhwagk.od.common.DatabaseOperation.{processResultSetToJson, selectQuery, usedOracleConnect}
import org.shinhwagk.od.common.OracleJdbcConvert
import org.shinhwagk.od.common.Tools.readSqlFile
import org.shinhwagk.od.lib.Libs
import spray.json.{JsArray, JsValue}

import scala.concurrent.ExecutionContext.Implicits.global

object TopSqlOperation {

  import OracleJdbcConvert._
  import org.shinhwagk.od.AkkaSystem._

  val ioSqlFilePath: String = "script\\topsql\\io.sql"

  def getResult(name: String) = {
    val sqlCode = readSqlFile(ioSqlFilePath)

    val f: (OracleConnection) => JsValue = selectQuery(sqlCode, processResultSetToJson, None) _

    for {
      conn <- Libs.getDataSource(name).map(_.getConnection)
      result <- usedOracleConnect(conn, f, true)
    } yield result
  }

}
