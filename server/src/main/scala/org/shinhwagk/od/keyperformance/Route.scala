package org.shinhwagk.od.keyperformance

import akka.http.scaladsl.server.Directives._

object Route {
  val route = (get & path("v1" / "keyperformance") & parameter('name.as[String])) { name =>
    handleWebSocketMessages(Actions(name, 2000).greeter)
  }
}
