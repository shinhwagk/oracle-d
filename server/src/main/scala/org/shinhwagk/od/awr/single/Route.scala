package org.shinhwagk.od.awr.single

import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._

object Route {
  val route =
    path("v1" / "awr") {
      path("single") {
        path("snapshots") {
          get {
            parameters('name.as[String], 'days.as[Int]) { (name, days) =>
              AwrSnapshotOperation.getSnapshots(name, days)
              complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
            }

          }
        } ~ path("report") {
          post {
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
          }
        }
      } ~ path("rac") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }
    }
}
