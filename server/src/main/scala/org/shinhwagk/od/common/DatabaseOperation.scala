package org.shinhwagk.od.common

import java.sql.ResultSet

import oracle.jdbc.pool.OracleDataSource
import oracle.jdbc._
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

import scala.collection.mutable.ArrayBuffer

object DatabaseOperation {
  def sqlQuery(ci: ConnectInfo, sqlText: String): JsValue = {
    var conn: OracleConnection = null
    var stmt: OraclePreparedStatement = null
    var rset: OracleResultSet = null
    try {
      val ods = makeDataSource(ci)
      conn = ods.getConnection().asInstanceOf[OracleConnection]
      stmt = conn.prepareStatement(sqlText).asInstanceOf[OraclePreparedStatement]
      rset = stmt.executeQuery().asInstanceOf[OracleResultSet]
      formatResultSetToJson(rset)
    } finally {
      rset.close()
      stmt.close()
      conn.close()
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

  def formatResultSetToJson(rset: ResultSet): JsValue = {
    val meta = rset.getMetaData
    val set = new ArrayBuffer[JsValue]()
    while (rset.next()) {
      val row = scala.collection.mutable.Map.empty[String, JsValue]
      for (i <- 1 to meta.getColumnCount) {
        val jsValue: JsValue = meta.getColumnType(i) match {
          case OracleTypes.VARCHAR => JsString(rset.getString(i))
          case OracleTypes.NUMBER => JsNumber(rset.getDouble(i))
          case OracleTypes.DATE => JsString(rset.getString(i))
        }
        row += (rset.getString(i) -> jsValue)
      }
      set += JsObject(row.toMap)
    }

    JsArray(set.toVector)
  }
}
