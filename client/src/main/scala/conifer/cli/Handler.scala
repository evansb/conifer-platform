package conifer.cli

import java.nio.file.Paths
import javax.inject._

import com.typesafe.scalalogging.LazyLogging
import conifer.client.HttpService
import conifer.core.ConiferConfig

trait Handler {
  def handle(command: Command): Unit
}

class HandlerImpl @Inject() (appConfig: ConiferConfig, clientService: HttpService)
  extends Handler with LazyLogging {

  override def handle(command: Command): Unit = command match {
    case EmptyCommand =>
      logger.debug("Nothing done")

    case CreateSession(maybeRootDir, maybePort) =>
      val rootDir = maybeRootDir.getOrElse(Paths.get("."))
      logger.debug(s"Creating a new session")
      logger.debug(s"Project directory is at $rootDir")
      clientService.start()

    case JoinSession(maybePort) =>
      logger.debug(s"Joining a session")
      clientService.start()

  }
}
