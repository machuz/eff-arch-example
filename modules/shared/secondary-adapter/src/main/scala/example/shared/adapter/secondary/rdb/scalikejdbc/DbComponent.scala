package example.shared.adapter.secondary.rdb.scalikejdbc

import com.zaxxer.hikari.{ HikariConfig, HikariDataSource }

import javax.inject.{ Inject, Singleton }
import scalikejdbc._

@Singleton
class DbComponent @Inject()(
  dbConfig: ScalikejdbcConfig
) {
  initialize()

  def initialize(): Unit = {
    val dataSource: HikariDataSource = {
      val hc = new HikariConfig()
      hc.setDriverClassName(dbConfig.rdb.driver)
      hc.setJdbcUrl(dbConfig.rdb.url)
      hc.setUsername(dbConfig.rdb.user)
      hc.setPassword(dbConfig.rdb.password)
      hc.setPoolName(dbConfig.rdb.poolName)
      hc.setMinimumIdle(dbConfig.rdb.poolInitialSize)
      hc.setMaximumPoolSize(dbConfig.rdb.poolMaxSize)
      hc.setConnectionTimeout(dbConfig.rdb.poolConnectionTimeoutMillis)
      hc.setConnectionTestQuery(dbConfig.rdb.poolValidationQuery)
      new HikariDataSource(hc)
    }
    ConnectionPool.singleton(new DataSourceConnectionPool(dataSource))
  }

  def getDB = DB(ConnectionPool.borrow())

}
