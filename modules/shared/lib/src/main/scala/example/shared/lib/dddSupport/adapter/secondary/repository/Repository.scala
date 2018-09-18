package example.shared.lib.dddSupport.adapter.secondary.repository

import example.shared.lib.dddSupport.domain.{ Entity, Identifier }

/**
  * リポジトリ責務を表すトレイト。
  *
  * @tparam ID 識別子の型
  * @tparam E エンティティの型
  */
trait Repository[ID <: Identifier[_], E <: Entity[ID]] {

  type This <: Repository[ID, E]
}
