package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import dispatch._, Defaults._
import net.liftweb.json.JsonAST._

object Studies extends Command {

  val Name = "studies"

  val Help =
    s"""|Used to configure studies. """.stripMargin

  val Usage = s"$Name"

  val log = Logger(LoggerFactory.getLogger(this.getClass))

  def login:Future[Either[String, String]] = {
    val theHost = host(appConfig.host, appConfig.port)
    val req = (theHost / "login").setContentType("application/json", "UTF-8")
    val post = req << s"""{"email": "${appConfig.userEmail}", "password": "administrator"}"""
    val resp = Http(post OK as.String).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  // for http headers see:
  //
  // http://stackoverflow.com/questions/12342062/basic-usage-of-dispatch-0-9/12343111#12343111
  def invokeCommand(args: Array[String]): Unit = {
    val r = for {
      resp <- login().right
    } yield resp
    log.info(s"resp: $r")

    //log.info(s"resp: ${login().right}")

    // login() match {
    //   case Right(res) => log.info(s"resp: ${res}")
    //   case _ => log.info("login failed")
    // }

    Http.shutdown()
  }
}
