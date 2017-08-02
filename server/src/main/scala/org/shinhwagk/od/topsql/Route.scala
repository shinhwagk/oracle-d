package org.shinhwagk.od.topsql

import akka.http.scaladsl.server.Directives.{pathPrefix, _}

object Route {
  val route =
    get {
      pathPrefix("v1" / "topsql") {
        parameters('name.as[String], 'num.as[Int]) { (name, days) =>
          path("io") {
            complete("a")
          } ~ path("executes") {
            complete("a")
          } ~ path("elapsed") {
            complete("a")
          } ~ path("cpu") {
            complete("a")
          }
        }
      }
    }
}
