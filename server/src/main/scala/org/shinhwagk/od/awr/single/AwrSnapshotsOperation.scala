package org.shinhwagk.od.awr.single

import org.shinhwagk.od.common.{ApiServices, ConnectInfo}

import scala.concurrent.Future

object AwrSnapshotOperation {
  //  val ci: ConnectInfo = ApiServices.getConnectInfoByName(name)

  def getSnapshots(name: String, days: Int): Future[List[AwrSnapshot]] = ???

}

case class AwrSnapshot(snapId: Long, snapDate: String)