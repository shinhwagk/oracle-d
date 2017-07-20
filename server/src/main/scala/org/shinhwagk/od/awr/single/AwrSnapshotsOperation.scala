package org.shinhwagk.od.awr.single

import java.sql.PreparedStatement

import org.shinhwagk.od.common.{ConnectInfo, DatabaseOperation, Tools}
import spray.json.{JsArray, JsValue}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object AwrSnapshotOperation {
  val instanceSqlFilePath: String = "script\\awr\\single\\10g\\instance.sql"
  val snapshotsSqlFilePath: String = "script\\awr\\single\\10g\\snapshots.sql"


  def getSnapshots(name: String, days: Int): Future[JsArray] = {
    import MyJsonProtocol._

    def setBindVariable(instance: InstanceInfo, days: Int)(stmt: PreparedStatement) = {
      stmt.setLong(1, instance.DBID)
      stmt.setLong(2, instance.DBID)
      stmt.setInt(3, instance.INST_NUM)
      stmt.setInt(4, instance.INST_NUM)
      stmt.setInt(5, days)
      stmt.setInt(6, days)
      stmt.setInt(7, instance.INST_NUM)
      stmt.setLong(8, instance.DBID)
    }

    for {
      ci <- Future.successful(ConnectInfo("10.65.193.25", 1521, "orayali2", "system", "oracle"))
      instance <- DatabaseOperation.sqlQuery(ci, Tools.readSqlFile(instanceSqlFilePath), DatabaseOperation.processResultSetOneRow).map(_.convertTo[InstanceInfo])
      j <- DatabaseOperation.sqlQuery(ci, Tools.readSqlFile(snapshotsSqlFilePath), DatabaseOperation.processResultSetToJson, Some(setBindVariable(instance, days)))
    } yield {
      j.convertTo[JsArray]
    }
  }

}

case class AwrSnapshot(name: String, snapId: Long, snapDate: String)