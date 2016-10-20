package conifer.tracker

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.google.inject.{AbstractModule, Inject, Provides}
import com.google.inject.name.{Named, Names}
import com.typesafe.config.{Config, ConfigFactory}
import conifer.core._
import net.codingwell.scalaguice.ScalaModule

class Module extends AbstractModule with ScalaModule with GuiceAkkaActorRefProvider {

  override def configure(): Unit = {
    val config = ConfigFactory.load("conifer.tracker")
    bind[Config].toInstance(config)
    bind[ConiferConfig].toProvider[ConiferConfigProvider]
    bind[ActorSystem].toProvider[ActorSystemProvider]
    bind[Actor].annotatedWith(Names.named(TrackerActor.name)).to[TrackerActor]
  }

  @Provides
  @Named(TrackerActor.name)
  def provideTrackerRef(@Inject() system: ActorSystem): ActorRef =
    provideActorRef(system, TrackerActor.name)

}
