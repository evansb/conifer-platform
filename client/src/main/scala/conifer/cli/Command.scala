package conifer.cli

import java.nio.file.Path

sealed trait Command {
  val port: Option[Int]
}


/**
  * Start a new session.
  *
  * @param rootDir Root directory of the Conifer project.
  *             conifer.json must exist.
  *             Default: Current directory
  * @param port Port number.
  *             Default: 8080
  */
case class CreateSession(rootDir: Option[Path], port: Option[Int]) extends Command

/**
  * Join a session
  *
  * @param port Port number.
  *             Default: 8080
  */
case class JoinSession(port: Option[Int]) extends Command

/**
  * Do nothing.
  */
case object EmptyCommand extends Command {
  override val port: Option[Int] = None
}

