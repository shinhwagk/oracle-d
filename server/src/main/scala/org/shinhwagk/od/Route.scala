package org.shinhwagk.od

import akka.http.scaladsl.server.Directives._
import org.shinhwagk.od.awr.single.{Route => AwrRoute}
import org.shinhwagk.od.version.{Route => VersionRoute}
import org.shinhwagk.od.topsql.{Route => TopSqlRoute}
import org.shinhwagk.od.keyperformance.{Route => KeyPerformanceRoute}

object Route {
  val masterRoute = AwrRoute.route ~ VersionRoute.route ~ TopSqlRoute.route ~ KeyPerformanceRoute.route
}
