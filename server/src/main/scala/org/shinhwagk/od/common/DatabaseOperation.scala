package org.shinhwagk.od.common

import java.sql.{PreparedStatement, ResultSet}

import oracle.jdbc.{OracleConnection, OraclePreparedStatement, OracleResultSet, OracleTypes}
import oracle.jdbc.pool.OracleDataSource
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseOperation {
  def sqlQuery[T](ci: ConnectInfo, sqlText: String, procResultSet: (ResultSet) => T, bindVariableOpt: Option[(PreparedStatement) => Unit] = None): Future[T] = Future {
    var conn: OracleConnection = null
    var stmt: OraclePreparedStatement = null
    var rset: OracleResultSet = null
    try {
      val ods = makeDataSource(ci)
      conn = ods.getConnection().asInstanceOf[OracleConnection]
      stmt = conn.prepareStatement(sqlText).asInstanceOf[OraclePreparedStatement]
      bindVariableOpt.foreach(bv => bv(stmt))
      rset = stmt.executeQuery().asInstanceOf[OracleResultSet]
      procResultSet(rset)
    } finally {
      if (rset != null) rset.close()
      if (stmt != null) stmt.close()
      if (conn != null) conn.close()
    }
  }

  def plsqlCall(): String = ???

  def makeDataSource(ci: ConnectInfo): OracleDataSource = {
    val ods = new OracleDataSource()
    ods.setDriverType("thin");
    ods.setServerName(ci.ip)
    ods.setPortNumber(ci.port)
    ods.setServiceName(ci.service)
    ods.setUser(ci.username)
    ods.setPassword(ci.password)
    ods
  }

  def processResultSetOneRow(rset: ResultSet): JsValue = {
    rset.next()
    val meta = rset.getMetaData
    val row: mutable.Map[String, JsValue] = scala.collection.mutable.Map.empty
    for (i <- 1 to meta.getColumnCount) {
      val jsValue: JsValue = meta.getColumnType(i) match {
        case OracleTypes.VARCHAR => JsString(rset.getString(i))
        case OracleTypes.NUMBER => JsNumber(rset.getLong(i))
        case OracleTypes.DATE => JsString(rset.getString(i))
      }
      row += (meta.getColumnName(i) -> jsValue)
    }
    JsObject(row.toMap)
  }

  def processResultSetToJson(rset: ResultSet): JsValue = {
    val meta = rset.getMetaData
    val set = new ArrayBuffer[JsValue]()
    while (rset.next()) {
      val row = scala.collection.mutable.Map.empty[String, JsValue]
      for (i <- 1 to meta.getColumnCount) {
        val jsValue: JsValue = meta.getColumnType(i) match {
          case OracleTypes.VARCHAR => JsString(rset.getString(i))
          case OracleTypes.NUMBER => JsNumber(rset.getLong(i))
          case OracleTypes.DATE => JsString(rset.getString(i))
        }
        row += (meta.getColumnName(i) -> jsValue)
      }
      set += JsObject(row.toMap)
    }

    JsArray(set.toVector)
  }

  def processResultSetMultipleLine(rset: ResultSet): String = {
    val bf = new ArrayBuffer[String]()
    while (rset.next()) {
      bf += rset.getString(1)
    }
    bf.mkString("\n")
  }
}
