package example.shared.adapter.secondary.rdb.scalikejdbc
import example.shared.adapter.config.support.ApplicationConfBase

class ScalikejdbcConfig extends ApplicationConfBase {

  object rdb {
    lazy val driver: String   = getString("db.default.driver")
    lazy val url: String      = getString("db.default.url")
    lazy val user: String     = getString("db.default.user")
    lazy val password: String = getString("db.default.password")

    lazy val poolName: String                  = getString("db.default.poolName")
    lazy val poolInitialSize: Int              = getInt("db.default.poolInitialSize")
    lazy val poolMaxSize: Int                  = getInt("db.default.poolMaxSize")
    lazy val poolConnectionTimeoutMillis: Long = getLong("db.default.poolConnectionTimeoutMillis")
    lazy val poolValidationQuery: String       = getString("db.default.poolValidationQuery")
  }

}
