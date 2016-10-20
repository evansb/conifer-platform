package conifer.core

import akka.actor.{IndirectActorProducer, Actor}
import com.google.inject.name.Names
import com.google.inject.{Key, Injector}

class GuiceActorProducer(val injector: Injector, val actorName: String)
    extends IndirectActorProducer {

  override def actorClass = classOf[Actor]

  override def produce() =
    injector.getBinding(Key.get(classOf[Actor], Names.named(actorName))).getProvider.get()
}
