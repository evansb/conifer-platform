package conifer.core

package object protocol {
  sealed trait Message
  final case class JoinSuccess(peers: List[String]) extends Message
  final case class CreateDocument(name: String) extends Message
  final case object OpenDocument extends Message
  final case object SyncRequest extends Message
  final case class SyncResponse(siteID: SiteId, document: WString) extends Message
  final case class Insert(char: WChar, siteId: SiteId) extends Message
  final case class Delete(char: WChar, siteId: SiteId) extends Message

  case object Leave extends Message
}
