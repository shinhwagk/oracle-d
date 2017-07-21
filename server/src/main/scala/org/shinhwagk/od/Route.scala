package org.shinhwagk.od

import akka.http.scaladsl.server.Directives._
import org.shinhwagk.od.awr.single.{Route => AwrRoute}
import org.shinhwagk.od.version.{Route => VersionRoute}
object Route {
  val masterRoute = AwrRoute.route ~ VersionRoute.route
}
