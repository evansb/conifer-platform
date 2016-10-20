package conifer.core

import javax.inject.Inject

import com.google.inject.Provider

trait ConiferConfig {
  val name: String
  val version: String
  val port: Int
}

class ConiferConfigProvider @Inject() (config: com.typesafe.config.Config)
    extends Provider[ConiferConfig] {
  override def get() = {
    new ConiferConfig {
      private val conifer = config.getConfig("conifer")
      lazy val name = conifer.getString("name")
      lazy val version = conifer.getString("version")
      lazy val port = conifer.getInt("port")
    }
  }
}
