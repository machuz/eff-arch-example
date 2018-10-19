//package example.shared.lib.eff.db.slick
//
//import com.github.tototoshi.slick.GenericJodaSupport
//import com.typesafe.config.{ Config, ConfigFactory }
//
//import slick.jdbc.JdbcProfile
//
//import scala.concurrent.Future
//
//abstract class DBComponent {
//
//  val config: Config = ConfigFactory.load()
//
//  protected val profile: JdbcProfile
//
//  import profile.api._
//  protected val db: Database
//
//  protected val jodaSupport: GenericJodaSupport
//
//  def run[R](a: DBIO[R]): Future[R]            = db.run(a)
//  def runTransaction[R](a: DBIO[R]): Future[R] = db.run(a.transactionally)
//}
