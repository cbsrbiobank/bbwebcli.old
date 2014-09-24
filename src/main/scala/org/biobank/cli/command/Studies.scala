package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}
import org.biobank.cli.Session
import org.biobank.cli.Protocol

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import dispatch._, Defaults._
import org.json4s._
import com.ning.http.client.cookie.Cookie
import scala.util.{ Try, Success, Failure }

object Studies extends Command {
  import Protocol._

  val Name = "studies"

  val Help =
    s"""|Used to configure studies. """.stripMargin

  val Usage = s"$Name"

  val Log = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val formats = DefaultFormats

  def invokeCommand(args: Array[String]): Unit = {
    if (args.isEmpty) {
      print(s"invalid command")
    } else if (args(0) == "list") {
      list
    } else {
      print(s"invalid command: ${args(0)}")
    }
  }

  def list: Unit = {
    Session.doRequest("studies") onComplete {
      case Success(e) =>
        e.fold(
          err => Log.error(err),
          json => {
            Log.debug(s"reply: $json")
            val resp = json.extract[StudiesResp]

            if (resp.data.isEmpty) {
              print("not studies present.")
            } else {
              resp.data.foreach { study =>
                Log.info(s"study: ${study.name}: ${study.id}")
              }
            }
          }
        )
        Http.shutdown()
      case Failure(ex) =>
        Log.error(ex.getMessage)
    }
    ()
  }
}
