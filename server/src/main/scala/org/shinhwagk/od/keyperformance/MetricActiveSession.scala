package org.shinhwagk.od.keyperformance

import java.math.BigDecimal
import java.sql.ResultSet

import oracle.jdbc.OracleConnection
import org.shinhwagk.od.common.DatabaseOperation.{selectQuery, usedOracleConnect}
import org.shinhwagk.od.common.SqlFileVersion
import org.shinhwagk.od.lib.Libs
import spray.json.{JsArray, JsObject, JsString, JsValue}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class MetricActiveSession(name: String) {

  import org.shinhwagk.od.AkkaSystem._
  import org.shinhwagk.od.common.OracleJdbcConvert._

  private val instanceMetricSqlCode: (Int) => String = SqlFileVersion("script\\keyperformance", "active-session", false, true, true, false).getSqlCode _

  private val connFuture: Future[OracleConnection] = Libs.getDataSource(name).map(_.getConnection)

  private def processResultSet(rset: ResultSet) = {
    val meta = rset.getMetaData
    val r = new ArrayBuffer[JsObject]()
    while (rset.next()) {
      val row = (1 to meta.getColumnCount).map { i => (meta.getColumnName(i), JsString(rset.getString(i))) }.toMap
      r += JsObject(row)
    }
    JsArray(r: _*)
  }

  private val f: (Int) => (OracleConnection) => JsValue = (dbVersion) => selectQuery(instanceMetricSqlCode(dbVersion), processResultSet, None) _

  def calculationMetric: Future[JsValue] = {
    for {
      conn <- connFuture
      version <- Future.successful(10)
      result <- usedOracleConnect(conn, f(version))
    } yield {
      val newVal: Map[String, JsValue] = Map("sessInfo"->result)
      println(newVal)
      JsObject(newVal)
    }
  }

  def close: Unit = {
    connFuture.foreach(_.close())
  }
}
