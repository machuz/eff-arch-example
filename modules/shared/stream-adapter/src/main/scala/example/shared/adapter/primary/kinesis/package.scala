package example.shared.adapter.primary

import scalaz.{ @@, Tag }

package object kinesis {
  sealed trait _KinesisApplicationName
  type KinesisApplicationName = String @@ _KinesisApplicationName
  def KinesisApplicationName(v: String): KinesisApplicationName = Tag[String, _KinesisApplicationName](v)

  sealed trait _KinesisStreamName
  type KinesisStreamName = String @@ _KinesisStreamName
  def KinesisStreamName(v: String): KinesisStreamName = Tag[String, _KinesisStreamName](v)
}
