package example.shared.lib.test.eff.cache

import org.atnos.eff.{ <=, Eff }

import example.shared.lib.test.eff.cache.interpreter.CacheIOTestInterpreter

import jp.eigosapuri.es.shared.lib.eff.cache.{ CacheIO, CacheIOCreation }
import jp.eigosapuri.es.shared.lib.test.eff.cache.interpreter.CacheIOTestInterpreter

trait CacheTestIOOps extends CacheIOTestInterpreter {
  implicit class CacheOps[R, A](effects: Eff[R, A]) {
    def testRunCacheIO[U](
      implicit m: CacheIO <= R
    ) = run(effects)
  }
}

object CacheTestIOEffect extends CacheTestIOOps with CacheIOCreation {}
