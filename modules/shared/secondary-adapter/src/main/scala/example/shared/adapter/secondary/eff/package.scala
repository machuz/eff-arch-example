package example.shared.adapter.secondary

import org.atnos.eff.syntax.addon.monix.task
import org.atnos.eff.syntax._

import example.shared.adapter.secondary.eff.rdb.scalikejdbc.ScalikejdbcTransactionTaskCreation

package object eff extends ScalikejdbcTransactionTaskCreation {

  object atnosEff
    extends eval
    with option
    with either
    with validate
//    with error
    with reader
    with writer
    with choose
    with list
    with state
    with safe
    with batch
    with memo
    with eff
    with task

}
