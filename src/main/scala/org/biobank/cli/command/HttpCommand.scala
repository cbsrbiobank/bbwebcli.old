package org.biobank.cli.command

import dispatch._, Defaults._
import scala.util.{ Try, Success, Failure }
import org.json4s._

object HttpCommand {

  implicit class HttpCommandResult(val future: Future[Either[String, JValue]] ) {

    def onCommandSuccess(fn: JValue => Unit) = {
      future onComplete {
        case Success(e) =>
          e.fold(
            err => print(s"error: $err"),
            json => fn(json)
          )
          Http.shutdown()
        case Failure(ex) =>
          print(ex.getMessage)
          Http.shutdown()
      }
    }

  }
}
