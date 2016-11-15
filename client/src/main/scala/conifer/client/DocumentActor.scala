package conifer.client

import akka.actor._
import scala.concurrent.duration._
import akka.cluster.client.ClusterClient.Publish
import com.google.inject.Inject
import conifer.core._

object DocumentActor extends NamedActor {

  val name = "DocumentActor"

}

class DocumentActor @Inject() (siteId: SiteId, mediator: ActorRef) extends Actor with ActorLogging {
  import conifer.core.protocol._

  implicit val executor = context.dispatcher

  private var document: WString = new WString(siteId, ClockValue(0))
  private var isSynchronising: Boolean = false
  private var maxSyncClockValue: ClockValue = ClockValue(0)

  val updateDocument: Operation => Operation = op => {
    val (_, updated) = document.integrate(op)
    document = updated
    op
  }

  implicit def operationToMessage(op: Operation): protocol.Message = op match {
    case InsertOp(wchar: WChar, from: SiteId) => protocol.Insert(wchar, from)
    case DeleteOp(wchar: WChar, from: SiteId) => protocol.Delete(wchar, from)
  }

  implicit def messageToOperation(message: Message): Operation = message match {
    case Insert(wchar: WChar, from: SiteId) => InsertOp(wchar, from)
    case Delete(wchar: WChar, from: SiteId) => DeleteOp(wchar, from)
  }

  override def preStart(): Unit = {
    isSynchronising = true
    mediator ! Publish("document", SyncRequest)
    context.system.scheduler.scheduleOnce(1.second) {
      isSynchronising = false
      context.become(active)
    }
  }

  def active: Receive = {
    case op: Insert => updateDocument(op)
    case op: Delete => updateDocument(op)
  }

  override def receive = {
    case SyncRequest => sender ! SyncResponse(this.siteId, document)
    case SyncResponse(siteId, document) =>
      val maxClockValue = document.maxClockValue(siteId)
      if (isSynchronising && !(maxClockValue < maxSyncClockValue)) {
        maxSyncClockValue = maxClockValue
      }
  }

}
