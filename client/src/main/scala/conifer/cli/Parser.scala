package conifer.cli

import java.nio.file.Paths
import javax.inject._

import com.typesafe.scalalogging.LazyLogging

trait Parser {
  def parse(args: Seq[String]): Option[Command]
}

class ParserImpl @Inject() (appConfig: conifer.core.ConiferConfig)
  extends scopt.OptionParser[Command](appConfig.name)
  with Parser
  with LazyLogging {

  head(appConfig.name, appConfig.version)

  opt[String]("directory").
    text("project directory").
    action((dir, sc) => {
      val rootDir = Paths.
        get(dir.replaceFirst("^~", System.getProperty("user.home"))).
        toAbsolutePath
      sc match {
        case s:CreateSession => s.copy(rootDir = Some(rootDir))
        case other => other
      }
    })

  opt[Int]("port").
    text("port").
    action((port, sc) => {
      sc match {
        case s:JoinSession => s.copy(port = Some(port))
        case s:CreateSession => s.copy(port = Some(port))
        case other => other
      }
    })

  cmd("init").action((_, c) => CreateSession(None, None)).
    text(s"Join a ${appConfig.name} session")

  cmd("start").action((_, c) => JoinSession(None)).
    text(s"Create new ${appConfig.name} session")

  override def parse(args: Seq[String]): Option[Command] = {
    val result = this.parse(args, EmptyCommand)
    result match {
      case None => logger.error("Parse arguments error")
      case Some(c) => logger.debug(s"Parsed as $c")
    }
    result
  }
}
