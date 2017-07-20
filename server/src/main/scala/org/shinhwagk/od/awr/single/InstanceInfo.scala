package org.shinhwagk.od.awr.single

import spray.json.DefaultJsonProtocol

case class InstanceInfo(DBID: Long, DB_NAME: String, INST_NUM: Int, INST_NAME: String)

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat4(InstanceInfo)
}
