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
            err => println(s"error: $err"),
            json => fn(json)
          )
          Http.shutdown()
        case Failure(ex) =>
          println(ex.getMessage)
          Http.shutdown()
      }
    }

    def onCommandCompletion(failFn: String => Unit)(successFn: JValue => Unit) = {
      future onComplete {
        case Success(e) =>
          e.fold(
            err => failFn(err),
            json => successFn(json)
          )
          Http.shutdown()
        case Failure(ex) =>
          println(ex.getMessage)
          Http.shutdown()
      }
    }
  }
}
