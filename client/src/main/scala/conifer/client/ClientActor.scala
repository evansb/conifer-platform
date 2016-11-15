package conifer.client

import akka.actor._
import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.cluster.pubsub.DistributedPubSub
import akka.cluster.pubsub.DistributedPubSubMediator
import conifer.core._

object ClientActor extends NamedActor {

  val name = "ClientActor"
}

class ClientActor extends Actor with ActorLogging {
  import conifer.core.protocol._
  import DistributedPubSubMediator.{Subscribe, SubscribeAck}
  private val cluster = Cluster(context.system)
  private val mediator = DistributedPubSub(context.system).mediator
  private var documents = Map.empty[String, ActorRef]

  cluster.subscribe(self, initialStateMode = InitialStateAsEvents,
    classOf[MemberEvent], classOf[UnreachableMember])

  def active: Receive = {
    case _ => log.info("received message")
  }

  override def receive = {
    case CreateDocument(name) => {
      val documentActorProps = GuiceAkkaExtension(context.system).props(DocumentActor.name)
      val documentActor = context.actorOf(documentActorProps, name = s"document-$name")
      context.watch(documentActor)
      documents = documents + (name -> documentActor)
      log.debug(s"Spawning document actor $name")
      documentActor
    }

    case MemberUp(member) => {
      if (member.address == cluster.selfAddress) {
        mediator ! Subscribe("document", self)
      }
    }
    case SubscribeAck(Subscribe("document", None, `self`)) =>
      log.info("Subscribing to document")
      context.become(active, false)
    case Leave =>
      cluster.leave(cluster.selfAddress)
    case _ =>
  }

}
