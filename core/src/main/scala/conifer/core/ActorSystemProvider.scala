package conifer.core

import akka.actor._
import com.google.inject._
import com.typesafe.config.Config

class ActorSystemProvider @Inject() (val config: Config,
                                     val injector: Injector) extends Provider[ActorSystem] {
  override def get() = {
    val system = ActorSystem("conifer", config)
    GuiceAkkaExtension(system).initialize(injector)
    system
  }
}

