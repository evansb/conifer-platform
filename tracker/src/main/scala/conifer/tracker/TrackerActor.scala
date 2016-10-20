package conifer.tracker

import akka.actor._
import akka.cluster.Cluster
import com.google.inject.Inject
import conifer.core.NamedActor

object TrackerActor extends NamedActor {

  override final val name = "Tracker"

}

class TrackerActor @Inject() extends Actor with ActorLogging {
  import akka.cluster.ClusterEvent._
  Cluster(context.system).subscribe(self, classOf[ClusterDomainEvent])

  override def receive = {
    case MemberUp(member) =>
      log.info(s"$member join the cluster")
    case MemberExited(member) =>
      log.info(s"$member exited the cluster")
    case MemberRemoved(member, previousState) =>
      log.info(s"Member $member gracefully exited")
    case UnreachableMember(member) =>
      log.info(s"Member $member is unreachable")
    case ReachableMember(member) =>
      log.info(s"Member $member is reachable")
    case state: CurrentClusterState =>
      log.info(s"Current cluster state: $state")
  }

  override def postStop(): Unit = {
    Cluster(context.system).unsubscribe(self)
    super.postStop()
  }

}
