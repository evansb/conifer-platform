package conifer.client

import akka.actor.{Actor, ActorSystem}
import com.google.inject.AbstractModule
import com.google.inject.name.Names
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import conifer.cli._
import conifer.core.{ActorSystemProvider, ConiferConfig, ConiferConfigProvider}
import net.codingwell.scalaguice.ScalaModule

class Module(command: Command) extends AbstractModule with ScalaModule {

  def configure(): Unit = {
    val config = ConfigFactory.load("conifer.client")
    val overridePort = command.port.getOrElse(config.getInt("conifer.port"))

    val finalConfig = ConfigFactory.load(ConfigFactory.parseString(
      s"""
        | include classpath("conifer.client")
        |
        | conifer.port = $overridePort
      """.stripMargin))

    bind[Config].toInstance(finalConfig)
    bind[ConiferConfig].toProvider[ConiferConfigProvider]
    bind[ActorSystem].toProvider[ActorSystemProvider]
    bind[Actor].annotatedWith(Names.named(ClientActor.name)).to[ClientActor]
    bind[Handler].to(classOf[HandlerImpl])
  }

}
