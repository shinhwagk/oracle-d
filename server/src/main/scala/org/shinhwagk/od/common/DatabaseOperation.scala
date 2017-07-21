package org.shinhwagk.od.common

import java.sql.{CallableStatement, PreparedStatement, ResultSet}

import oracle.jdbc._
import oracle.jdbc.pool.OracleDataSource
import spray.json.{JsArray, JsNumber, JsObject, JsString, JsValue}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object DatabaseOperation {

  import OracleJdbcConvert._

  def selectQuery[T](sqlCode: String, procResultSet: (ResultSet) => T,
                     bindVariableOpt: Option[(PreparedStatement) => Unit] = None)(orclConn: OracleConnection): T = {
    var stmt: OraclePreparedStatement = null
    var rset: OracleResultSet = null
    stmt = orclConn.prepareStatement(sqlCode)
    bindVariableOpt.foreach(bv => bv(stmt))
    rset = stmt.executeQuery()
    val result = procResultSet(rset)
    rset.close()
    stmt.close()
    result
  }

  def plsqlCall[T](plsqlCode: String,
                   callSet: (OracleCallableStatement) => Unit,
                   callResult: (OracleCallableStatement) => T)(orclConn: OracleConnection): T = {
    val ocs: OracleCallableStatement = orclConn.prepareCall(plsqlCode)
    callSet(ocs)
    ocs.execute()
    val result: T = callResult(ocs)
    ocs.close()
    result
  }

  def usedOracleConnect[T](orclConnect: OracleConnection, f: OracleConnection => T,
                                                  afterClose: Boolean = false): Future[T] = Future {
    val result: T = f(orclConnect)
    if (afterClose) orclConnect.close()
    result
  }

  def makeDataSource(driverType: String, serverName: String, portNumber: Int, serviceName: String,
                     user: String, password: String): OracleDataSource = {
    val ods = new OracleDataSource()
    ods.setDriverType(driverType);
    ods.setServerName(serverName)
    ods.setPortNumber(portNumber)
    ods.setServiceName(serviceName)
    ods.setUser(user)
    ods.setPassword(password)
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
