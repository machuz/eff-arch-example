package example.shared.adapter.secondary

import example.shared.adapter.secondary.eff.rdb.scalikejdbc.TranTaskEffect

package object eff {

  object MyEff extends TranTaskEffect

}
