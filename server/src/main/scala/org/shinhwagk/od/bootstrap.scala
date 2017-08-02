package org.shinhwagk.od

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.io.StdIn

object AkkaSystem {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
}

object Bootstrap extends App {

  import AkkaSystem._

  implicit val executionContext = system.dispatcher

  val bindingFuture = Http().bindAndHandle(Route.masterRoute, "0.0.0.0", 9003)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}

