package example.exampleApi.domain.model.user

import example.shared.lib.dddSupport.domain.Identifier

case class UserId(value: String) extends Identifier[String]
