package example.shared.lib.eff.db

import org.atnos.eff.Eff
import org.atnos.eff.all.send

import _root_.slick.dbio.DBIO
import example.shared.lib.eff.db.DBIOTypes._dbio

trait DBIOCreation {

  final def fromDBIO[R: _dbio, A](dbio: DBIO[A]): Eff[R, A] = {
    Eff.send[DBIO, R, A](dbio)
  }

  def successful[R: _dbio, A](a: A): Eff[R, A] = {
    send[DBIO, R, A](DBIO.successful(a))
  }

  def failed[R: _dbio, A](t: Throwable): Eff[R, A] = {
    send[DBIO, R, A](DBIO.failed(t))
  }

}
