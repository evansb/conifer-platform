package conifer.tracker

import akka.actor.ActorSystem
import com.google.inject.Guice
import conifer.core.GuiceAkkaExtension

object Main extends App {
  val injector = Guice.createInjector(new Module)

  val system = injector.getInstance(classOf[ActorSystem])

  val tracker = system.actorOf(GuiceAkkaExtension(system).props(TrackerActor.name))
}
