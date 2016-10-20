package conifer.cli

import java.nio.file.Paths
import javax.inject._
import akka.actor.ActorSystem
import com.typesafe.scalalogging.LazyLogging
import conifer.client.ClientActor
import conifer.core.{ConiferConfig, GuiceAkkaExtension}

trait Handler {
  def handle(command: Command): Unit
}

class HandlerImpl @Inject() (appConfig: ConiferConfig, system: ActorSystem)
  extends Handler with LazyLogging {

  override def handle(command: Command): Unit = command match {
    case EmptyCommand =>
      logger.debug("Nothing done")

    case CreateSession(maybeRootDir, maybePort) =>
      val rootDir = maybeRootDir.getOrElse(Paths.get("."))
      logger.debug(s"Creating a new session")
      logger.debug(s"Project directory is at $rootDir")

    case JoinSession(maybePort) =>
      logger.debug(s"Joining a session")
      system.actorOf(GuiceAkkaExtension(system).props(ClientActor.name))

  }
}
