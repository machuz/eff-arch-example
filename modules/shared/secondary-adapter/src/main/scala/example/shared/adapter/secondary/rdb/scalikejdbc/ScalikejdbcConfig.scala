package example.shared.adapter.secondary.rdb.scalikejdbc

import example.shared.adapter.config.support.ApplicationConfBase

class ScalikejdbcConfig extends ApplicationConfBase {

  object rdb {
    lazy val driver: String   = getString("db.default.driver")
    lazy val url: String      = getString("db.default.url")
    lazy val user: String     = getString("db.default.user")
    lazy val password: String = getString("db.default.password")

    lazy val poolName: String                  = getString("db.default.poolName")
    lazy val poolInitialSize: Int              = getInt("db.default.minConnections")
    lazy val poolMaxSize: Int                  = getInt("db.default.maxConnections")
    lazy val poolConnectionTimeoutMillis: Long = getLong("db.default.connectionTimeout")
    lazy val poolValidationQuery: String       = getString("db.default.connectionInitSql")
    lazy val maxLifeTime: Long                 = getLong("db.default.maxLifetime")
    lazy val leakDetectionThreshold: Long      = getLong("db.default.leakDetectionThreshold")
    lazy val idleTimeout: Long                 = getLong("db.default.idleTimeout")
    lazy val validationTimeout: Long           = getLong("db.default.validationTimeout")

  }

}
