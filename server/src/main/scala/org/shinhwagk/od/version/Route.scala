package org.shinhwagk.od.version

import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server.Directives.{complete, path, _}

import scala.util.{Failure, Success}

object Route {
  val route =
    get {
      path("v1" / "version") {
        parameter('name.as[String]) { name =>
          onComplete(Action.getVersion(name)) {
            case Success(version) => complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, version))
            case Failure(ex) => complete(InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    }
}
