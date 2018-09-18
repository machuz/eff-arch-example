package example.shared.adapter.primary.play.task

import java.io.{ PrintWriter, StringWriter }

import play.api.{ Application, ApplicationLoader, Environment, Mode, Play }

import jp.eigosapuri.es.shared.lib.logger.EsLogger

trait Task {

  def withApplication[A](f: Application => Unit): Unit =
    withApplication(f, printStackTraceToEsLogger)

  def withApplication[A](f: Application => Unit, exf: Throwable => Unit): Unit =
    try {
      val env     = Environment(new java.io.File("."), getClass.getClassLoader, getMode)
      val context = ApplicationLoader.createContext(env)
      val loader  = ApplicationLoader(context)
      val app     = loader.load(context)
      try {
        Play.start(app)
        f(app)
      } finally {
        Play.stop(app)
      }
    } catch {
      case ex: Throwable =>
        exf(ex)
        sys.exit(255)
    } finally {
      sys.exit(0)
    }

  private def getMode: Mode.Mode = {
    Option(System.getProperty("play.mode")) match {
      case Some(mode) if mode.equalsIgnoreCase("prod") => Mode.Prod
      case _                                           => Mode.Dev
    }
  }

  private def printStackTraceToEsLogger(ex: Throwable): Unit = {
    // バッヂ処理失敗時の話なので, stackTrackの量はとりあえず気にしない.
    val sw = new StringWriter()
    val pw = new PrintWriter(sw)
    ex.printStackTrace(pw)
    pw.flush()
    EsLogger.error("Task", Map("msg" -> s"message: ${sw.toString}"))
  }
}
