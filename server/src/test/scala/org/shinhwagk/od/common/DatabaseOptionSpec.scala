package org.shinhwagk.od.common

import java.sql.PreparedStatement

import org.scalatest.FlatSpec
import spray.json.JsValue

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class DatabaseOptionSpec extends FlatSpec {
  val ci = ConnectInfo("10.65.193.25", 1521, "orayali2", "system", "oracle")

  val instanceSqlFilePath: String = "script\\awr\\single\\10g\\instance.sql"
  val snapshotsSqlFilePath: String = "script\\awr\\single\\10g\\snapshots.sql"

  // http://localhost:9001/v1/awr/single/report?name=aaa&dbid=2765360818&instnum=1&bid=24544&eid=24554

  "Test query" should "no error" in {
    val instance = Await.result(DatabaseOperation.sqlQuery[JsValue](ci, "select * from dual", DatabaseOperation.processResultSetToJson), Duration.Inf)
    println(instance)
  }

  "Test setBindVariable for query" should "no error" in {
    val sqlText = Tools.readSqlFile(snapshotsSqlFilePath)
    println(sqlText)

    def setBindVariable(stmt: PreparedStatement) = {
      stmt.setLong(1, 1929719550L)
      stmt.setLong(2, 1929719550L)
      stmt.setInt(3, 1)
      stmt.setInt(4, 1)
      stmt.setInt(5, 11)
      stmt.setInt(6, 11)
      stmt.setInt(7, 1)
      stmt.setLong(8, 1929719550L)
    }

    val result: JsValue = Await.result(DatabaseOperation.sqlQuery(ci, sqlText, DatabaseOperation.processResultSetToJson, Some(setBindVariable)), Duration.Inf)
    println(result)
  }
  //
  //  "Test plsql in sqlText for query" should "no error" in {
  //    val sqlText = """select output from table(dbms_workload_repository.awr_report_html(?,?,?,?,0))"""
  //
  //    def setBindVariable(stmt: PreparedStatement) = {
  //      stmt.setLong(1, 2765360818L)
  //      stmt.setInt(2, 1)
  //      stmt.setInt(3, 24729)
  //      stmt.setInt(4, 24739)
  //    }
  //
  //    val snapshots = Await.result(DatabaseOperation.sqlQuery(ci, sqlText, DatabaseOperation.processResultSetMultipleLine, Some(setBindVariable)), Duration.Inf)
  //    import java.io._
  //    val pw = new PrintWriter(new File("hello.html"))
  //    pw.write(snapshots)
  //    pw.close
  //  }

}
