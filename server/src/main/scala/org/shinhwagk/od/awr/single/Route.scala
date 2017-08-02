package org.shinhwagk.od.awr.single

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._

import scala.util.{Failure, Success}

object Route {
  val route =
    pathPrefix("v1" / "awr") {
      pathPrefix("single") {
        path("snapshots") {
          get {
            parameters('name.as[String], 'days.as[Int]) { (name, days) =>
              onComplete(AwrSingleOperation.getSnapshots(name, days)) {
                case Success(json) =>
                  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
                  complete(json)
                case Failure(ex) => complete((InternalServerError, s"An error occurred: ${ex.getMessage}"))
              }
            }
          }
        } ~ path("report") {
          get {
            parameters('name.as[String], 'dbid.as[Long], 'instnum.as[Int], 'bid.as[Int], 'eid.as[Int], 'mode.as[String]) {
              (name, dbid, instnum, bid, eid, mode) =>
                val start = System.currentTimeMillis()
                println("start report awr")
                onComplete(AwrSingleOperation.generateAwr(name, dbid, instnum, bid, eid, mode)) {
                  case Success(report) =>
                    println(s"end report awr ${System.currentTimeMillis() - start}")
                    if (mode.toLowerCase == "html") {
                      complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, report))
                    } else {
                      complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, report))
                    }
                  case Failure(ex) => complete((InternalServerError, s"An error occurred: ${ex.getMessage}"))
                }
            }
          }
        }
      } ~ path("rac") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      }

    }
}