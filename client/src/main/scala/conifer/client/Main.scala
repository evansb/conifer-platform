package conifer.client

import com.google.inject.{AbstractModule, Guice}
import com.typesafe.config.ConfigFactory
import conifer.cli.{Handler, Parser, ParserImpl}
import conifer.core.{ConiferConfig, ConiferConfigProvider}
import net.codingwell.scalaguice.ScalaModule

object Main extends App {
  import net.codingwell.scalaguice.InjectorExtensions._

  val bootstrap = Guice.createInjector(new AbstractModule with ScalaModule {
    override def configure(): Unit = {
      val bootstrapConfig = ConfigFactory.load("conifer.client")
      bind[ConiferConfig].toInstance(new ConiferConfigProvider(bootstrapConfig).get())
      bind[Parser].to(classOf[ParserImpl]).asEagerSingleton()
    }
  })

  val parser = bootstrap.instance[Parser]

  parser.parse(args) match {
    case None => System.exit(1)
    case Some(command) => {
      val injector = Guice.createInjector(new Module(command))
      val handler = injector.instance[Handler]
      handler.handle(command)
    }
  }

}
