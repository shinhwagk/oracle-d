package test

import java.util.UUID

import akka.http.scaladsl.server.Directives._
import akka.NotUsed
import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.{Flow, Sink, Source}
import test.Test.Protocol.{CloseConnection, OpenConnection, SignedMessage}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Test extends App {
  implicit val system = ActorSystem("test")

  implicit val materializer = ActorMaterializer()

  val route = get {
    pathEndOrSingleSlash {
      handleWebSocketMessages(websocketFlow)
    }
  }

  val maximumClients = 1

  class ChatRef extends Actor {
    override def receive: Receive = withClients(Map.empty[UUID, ActorRef])

    def withClients(clients: Map[UUID, ActorRef]): Receive = {
      case SignedMessage(uuid, msg) => clients.collect {
        case (id, ar) if id != uuid => ar ! msg
      }
      case OpenConnection(ar, uuid) if clients.size == maximumClients => ar ! PoisonPill
      case OpenConnection(ar, uuid) => context.become(withClients(clients.updated(uuid, ar)))
      case CloseConnection(uuid) => context.become(withClients(clients - uuid))
    }
  }

  object Protocol {
    case class SignedMessage(uuid: UUID, msg: String)
    case class OpenConnection(actor: ActorRef, uuid: UUID)
    case class CloseConnection(uuid: UUID)
  }

  val chatRef = system.actorOf(Props[ChatRef])

  def websocketFlow: Flow[Message, Message, Any] =
    Flow[Message]
      .mapAsync(1) {
        case TextMessage.Strict(s) => Future.successful(s)
        case TextMessage.Streamed(s) => s.runFold("")(_ + _)
        case b: BinaryMessage => throw new Exception("Binary message cannot be handled")
      }.via(chatActorFlow(UUID.randomUUID()))
      .map(p=>TextMessage(p))

  def chatActorFlow(connectionId: UUID): Flow[String, String, Any] = {

    val sink: Sink[String, NotUsed] = Flow[String]
      .map(msg => Protocol.SignedMessage(connectionId, msg))
      .to(Sink.actorRef(chatRef, Protocol.CloseConnection(connectionId)))

    val source: Source[Nothing, Unit] = Source.actorRef(16, OverflowStrategy.fail)
      .mapMaterializedValue { actor: ActorRef =>
        chatRef ! Protocol.OpenConnection(actor, connectionId)
      }

    Flow.fromSinkAndSource(sink, source)
  }

  Http().bindAndHandle(route, "0.0.0.0", 8080)
    .map(_ => println(s"Started server..."))

}