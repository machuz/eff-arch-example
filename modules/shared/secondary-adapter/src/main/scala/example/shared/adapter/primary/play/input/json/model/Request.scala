package example.shared.adapter.primary.play.input.json.model

trait Request[A <: InputJsonModel] {
  val item: A
}

trait SeqRequest[A <: InputJsonModel] {
  val items: Seq[A]
}
