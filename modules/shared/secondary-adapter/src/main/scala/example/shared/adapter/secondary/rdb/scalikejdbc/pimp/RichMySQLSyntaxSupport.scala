package example.shared.adapter.secondary.rdb.scalikejdbc.pimp

import scalikejdbc._

object RichMySQLSyntaxSupport {

  implicit class BulkInsertSQLBuilder(val self: InsertSQLBuilder) extends AnyVal {
    def multiValues(values: Seq[Any]*): InsertSQLBuilder = {
      values.foreach(x => require(x.nonEmpty))
      val elems = values.transpose.map { xs =>
        val ys = xs.map(x => sqls"$x")
        sqls"(${sqls.csv(ys: _*)})"
      }
      self.append(sqls"values ${sqls.csv(elems: _*)}")
    }
  }

  implicit class RichInsertSQLBuilder(val self: InsertSQLBuilder) extends AnyVal {
    def onDuplicateKeyUpdate(columnsAndValues: (SQLSyntax, Any)*): InsertSQLBuilder = {
      val cvs = columnsAndValues map {
        case (c, v) => sqls"$c = $v"
      }
      self.append(sqls"on duplicate key update ${sqls.csv(cvs: _*)}")
    }
  }

}
