package org.shinhwagk.od.keyperformance

import akka.http.scaladsl.model.ws.{Message, TextMessage}
import akka.stream.OverflowStrategy
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.{Done, NotUsed}
import spray.json.{JsNumber, JsObject}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

class Actions(name: String, sleep: Int) {

  import org.shinhwagk.od.AkkaSystem._

  val closeString = "close"

  var switch = true

  val metricInstance = new MetricInstance(name)
  val metricActiveSession = new MetricActiveSession(name)

  val outgoingMessages: Source[Message, NotUsed] = {
    Source.actorRef[Message](10, OverflowStrategy.dropTail)
      .mapMaterializedValue { outActor =>
        val instInfo = Source.tick(1 second, 2 second, ()).to(Sink.foreach(_ => metricInstance.calculationMetric.foreach(r => outActor ! TextMessage(r.toString())))).run()
        val sessInfo = Source.tick(1 second, 2 second, ()).to(Sink.foreach(_ => metricActiveSession.calculationMetric.foreach(r => outActor ! TextMessage(r.toString())))).run()
        Future {
          while (true) {
            Thread.sleep(1000);
            if (!switch) {
              metricInstance.close
              metricActiveSession.close
              instInfo.cancel()
              sessInfo.cancel()
            }
          }
        }
        NotUsed
      }
  }

  val incomingMessages: Sink[Any, Future[Done]] = Sink.foreach { case TextMessage.Strict(text) =>
    if (text == closeString) {
      switch = false
      metricInstance.close
    };
    println(s"flow -> sink ${text}")
  }

  def greeter: Flow[Message, Message, Any] = {
    Flow.fromSinkAndSource(incomingMessages, outgoingMessages)
  }
}

object Actions {
  def apply(name: String, sleep: Int): Actions = new Actions(name, sleep)
}