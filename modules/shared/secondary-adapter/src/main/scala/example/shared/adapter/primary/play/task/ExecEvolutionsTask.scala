package example.shared.adapter.primary.play.task

import com.typesafe.scalalogging.LazyLogging

import scopt.OptionParser

import scala.util.control.Exception.allCatch

import jp.eigosapuri.es.shared.adapter.secondary.slick.evolution.EvolutionComponents

object ExecEvolutionsTask extends App with LazyLogging {

  /**
    * main
    */
  try {
    val taskOption = parseOption
    logger.info(s"""ARGS: isDryRun -> ${taskOption.isDryRun} dbName -> ${taskOption.dbName}""")

    val db = EvolutionComponents.getDB(taskOption.dbName)

    EvolutionComponents.isEvolution(db) match {
      case true if taskOption.isDryRun =>
        logger.info("evolution dry-run was successful")
      case true if taskOption.isApplyDown =>
        EvolutionComponents.applyEvolution(db)
        logger.info("evolution was successful")
      case true if EvolutionComponents.isExistsDownScript(db) =>
        logger.error("exists down script.")
        sys.exit(1)
      case true =>
        EvolutionComponents.applyEvolution(db)
        logger.info("evolution was successful")
      case false => // do nothing
    }
  } catch {
    case ex: Throwable =>
      logger.error(s"evolution was failed: ${ex.getMessage}")
      sys.exit(255)
  } finally {
    sys.exit(0)
  }

  /**
    * 引数Parse
    *
    * @param isDryRun Boolean dryRunするか否か
    * @param isApplyDown Boolean DownScriptを実行するか否か
    * @param dbName   String configで設定しているdbName
    */
  protected case class ExecEvolutionsTaskOption(
    isDryRun: Boolean = true,
    dbName: String = "default",
    isApplyDown: Boolean = false
  )

  protected def parseOption: ExecEvolutionsTaskOption = {
    val parser = new OptionParser[ExecEvolutionsTaskOption]("ExecEvolutionsTask") {
      opt[Boolean]('i', "isDryRun") required () valueName "<isDryRun>" action { (x, o) =>
        o.copy(isDryRun = x)
      } text "Boolean"

      opt[Boolean]('a', "isApplyDown") required () valueName "<isApplyDown>" action { (x, o) =>
        o.copy(isApplyDown = x)
      } text "Boolean"

      opt[String]('d', "dbName") valueName "<dbName>" action { (x, o) =>
        o.copy(dbName = x)
      } text "String default: 'default'"

    }

    parser.parse(args, ExecEvolutionsTaskOption()).getOrElse {
      // 引数解析に失敗した場合
      val isDryRun    = allCatch opt args(0) getOrElse "null"
      val isApplyDown = allCatch opt args(1) getOrElse "null"
      val dbName      = allCatch opt args(2) getOrElse "null"

      throw new IllegalArgumentException(
        s"""Invalid argument ARGS: isDryRun -> $isDryRun isApplyDown -> $isApplyDown dbName -> $dbName"""
      )
    }
  }

}
