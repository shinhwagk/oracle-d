package org.shinhwagk.od.keyperformance

import java.math.BigDecimal
import java.sql.ResultSet

import oracle.jdbc.OracleConnection
import org.shinhwagk.od.common.DatabaseOperation.{selectQuery, usedOracleConnect}
import org.shinhwagk.od.common.SqlFileVersion
import org.shinhwagk.od.lib.Libs
import spray.json.{JsArray, JsNumber, JsObject, JsValue}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class MetricInstance(name: String) {

  type metrics = Map[Int, Map[String, BigDecimal]]

  import org.shinhwagk.od.AkkaSystem._
  import org.shinhwagk.od.common.OracleJdbcConvert._

  private val instanceMetricSqlCode: (Int) => String = SqlFileVersion("script\\keyperformance", "inst-metric", false, true, false, false).getSqlCode _

  private val connFuture: Future[OracleConnection] = Libs.getDataSource(name).map(_.getConnection)

  private def processResultSet(rset: ResultSet): metrics = {
    val meta = rset.getMetaData
    val r = scala.collection.mutable.Map.empty[Int, Map[String, BigDecimal]]
    while (rset.next()) {
      r += (rset.getInt(1) -> (2 to meta.getColumnCount).map { i => (meta.getColumnName(i), rset.getBigDecimal(i)) }.toMap)
    }
    r.toMap
  }

  private val f: (Int) => (OracleConnection) => metrics = (dbVersion) => selectQuery(instanceMetricSqlCode(dbVersion), processResultSet, None) _

  private var old = Map.empty[Int, Map[String, BigDecimal]]

  def calculationMetric: Future[JsValue] = {
    for {
      conn <- connFuture
      version <- Future.successful(10)
      result <- usedOracleConnect(conn, f(version))
    } yield {
      val c = deltaMetric(old, result)
      val newVal: Map[String, JsValue] = Map("instInfo"->c)
      old = result
      JsObject(newVal)
    }
  }

  private def deltaMetric(o: metrics, n: metrics): JsValue = {
    val sumDbTime: BigDecimal = MetricProcess.pAllDbTimeSum(n, o)

    val metrics: Map[Int, mutable.Map[String, JsValue]] = n.map { case (k, v) => k -> mutable.Map.empty[String, JsValue] }

    val arr = new ArrayBuffer[JsObject]()

    n.foreach { case (k, v) =>

      val vals: mutable.Map[String, JsValue] = metrics(k)
      val old: Map[String, BigDecimal] = o.getOrElse(k, Map.empty)
      vals += ("DBTime%CW" -> JsNumber(v("DBT").subtract(old.getOrElse("DBT", BigDecimal.ZERO)).divide(sumDbTime, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("DBCpu-%DBT" -> JsNumber(v("DBCPU").subtract(old.getOrElse("DBCPU", BigDecimal.ZERO)).divide(sumDbTime, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("SQL-Xela%DBT" -> JsNumber(v("SELA").subtract(old.getOrElse("SELA", BigDecimal.ZERO)).divide(sumDbTime, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("SQL-Pela%DBT" -> JsNumber(v("SPELA").subtract(old.getOrElse("SPELA", BigDecimal.ZERO)).divide(sumDbTime, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("SQL-HPela%DBT" -> JsNumber(v("SHPELA").subtract(old.getOrElse("SHPELA", BigDecimal.ZERO)).divide(sumDbTime, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))

      val sumCpu = v("BT").subtract(old.getOrElse("BT", BigDecimal.ONE)).add(v("IT").subtract(old.getOrElse("IT", BigDecimal.ZERO)))
      vals += ("CpuBusy" -> JsNumber(v("BT").subtract(old.getOrElse("BT", BigDecimal.ZERO)).divide(sumCpu, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("CpuIdle" -> JsNumber(v("IT").subtract(old.getOrElse("IT", BigDecimal.ZERO)).divide(sumCpu, 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100))))
      vals += ("Sess" -> JsNumber(v("SESS")))
      vals += ("Asess" -> JsNumber(v("ASESS")))
      vals += ("Inst" -> JsNumber(k))
      arr += JsObject(vals.toMap)
    }

    JsArray(arr: _*)
  }

  def close: Unit = {
    connFuture.foreach(_.close())
  }
}
