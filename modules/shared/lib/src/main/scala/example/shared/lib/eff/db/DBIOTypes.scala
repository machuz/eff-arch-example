package example.shared.lib.eff.db

import org.atnos.eff.{ <=, |=, Fx }

import cats.data.Reader

import example.shared.lib.eff.db.slick.DBComponent
import _root_.slick.dbio.DBIO
import monix.eval.Task

import example.shared.lib.eff.ErrorEither

object DBIOTypes {
  type _dbio[R]     = DBIO |= R
  type _DBIO[R]     = DBIO <= R
  type ReaderDB[A]  = Reader[DBComponent, A]
  type _readerDB[R] = ReaderDB |= R
  type DBIOStack    = Fx.fx3[DBIO, Task, ErrorEither]
}
