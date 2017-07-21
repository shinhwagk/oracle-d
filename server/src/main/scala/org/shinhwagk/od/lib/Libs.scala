package org.shinhwagk.od.lib

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import oracle.jdbc.pool.OracleDataSource
import org.shinhwagk.od.common.DatabaseOperation
import play.api.libs.ws._
import play.api.libs.ws.ahc._
import spray.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object MyJsonProtocol extends DefaultJsonProtocol {
  implicit val colorFormat = jsonFormat5(ConnectInfo)
}

object Libs {
  def getConnectInfo(name: String)(implicit system: ActorSystem, materializer: ActorMaterializer) = {
    import MyJsonProtocol._
    val wsClient = StandaloneAhcWSClient()
    val url = s"""http://10.65.193.100:9999/v1/connect/jdbc/oracle/${name}"""
    wsClient.url(url).get().map { response => response.body[String] }
      .map { rs =>
        wsClient.close(); rs.parseJson.convertTo[ConnectInfo]
      }
  }

  def getDataSource(name: String)(implicit system: ActorSystem, materializer: ActorMaterializer): Future[OracleDataSource] = {
    getConnectInfo(name).map { case ConnectInfo(ip, port, service, user, password) =>
      DatabaseOperation.makeDataSource("thin", ip, port, service, user, password)
    }
  }
}


case class ConnectInfo(ip: String, port: Int, service: String, user: String, password: String)