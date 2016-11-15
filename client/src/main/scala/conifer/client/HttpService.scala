package conifer.client

import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import upickle.default._
import akka.http.scaladsl.model.ws.{BinaryMessage, Message, TextMessage}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.google.inject.Inject
import conifer.core.{ConiferConfig, GuiceAkkaExtension, protocol}
import de.heikoseeberger.akkahttpupickle.UpickleSupport

import scala.concurrent.Future

class HttpService @Inject()(appConfig: ConiferConfig,
                            implicit val system: ActorSystem) extends UpickleSupport {

  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val DEV_SERVER_PORT = 8000
  var bindingFuture: Future[ServerBinding] = _

  val clientActor = system.actorOf(GuiceAkkaExtension(system).props(ClientActor.name))
  var documentActor: Option[ActorRef] = None

  val devProxy: Route = Route { context =>
    val request = context.request
    val flow = Http(system).outgoingConnection(request.uri.authority.host.address, DEV_SERVER_PORT)
    val handler = Source.single(context.request)
      .via(flow)
      .runWith(Sink.head)
      .flatMap(context.complete(_))
    handler
  }

  val route = get {
    pathSingleSlash { devProxy } ~
    path("file") { get { complete(openDocument()) } } ~
    path("document") { handleWebSocketMessages(documentFlow) } ~
    pathPrefix("assets" / Remaining) { file =>
      encodeResponse {
        getFromResource("public/" + file)
      }
    } ~
    pathPrefix("__webpack_hmr") { devProxy } ~
    devProxy
  }

  def documentFlow: Flow[Message, Message, Any] =
    Flow[Message].collect {
      case TextMessage.Strict(msg) =>
        read[protocol.Message](msg)
    }.map({
      case protocol.OpenDocument => TextMessage.Strict("Hello")
    })

  def openDocument(): Future[protocol.Message] = Future.successful(protocol.OpenDocument)
  /*
    (clientActor ? protocol.OpenDocument, Timeout(2.seconds)).map {
      case response: protocol.SyncResponse => response
    }
    */

  def start(): Unit = {
    bindingFuture = Http().bindAndHandle(route, "localhost", appConfig.port + 1)
  }

  def stop(): Unit = {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete({ u =>
        clientActor ! protocol.Leave
        system.terminate().onComplete({ u =>
          System.exit(0)
        })
      })
  }

}
