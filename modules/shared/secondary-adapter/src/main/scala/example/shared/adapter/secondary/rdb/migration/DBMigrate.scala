package example.shared.adapter.secondary.rdb.migration

import org.flywaydb.core.Flyway

object DBMigrate extends App {

  val flyway = new Flyway()

  //データベース接続情報セット
  flyway.setDataSource("jdbc:mysql://127.0.0.1/alp_local", "root", "admin")

  //SQLファイルが存在するディレクトリをセット
  flyway.setLocations(
    "filesystem:" + System.getProperty("user.dir") + "/modules/example-api/secondary-adapter/conf/migrations"
  )
  println("filesystem:" + System.getProperty("user.dir") + "/modules/example-api/secondary-adapter/conf/migrations")

  //マイグレーション実行
  flyway.migrate()
}
