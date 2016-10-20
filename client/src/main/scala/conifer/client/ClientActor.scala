package conifer.client

import akka.actor._
import conifer.core.NamedActor

object ClientActor extends NamedActor {

  val name = "ClientActor"

}

class ClientActor extends Actor with ActorLogging {

  override def receive = {
    case _ =>
  }

}
