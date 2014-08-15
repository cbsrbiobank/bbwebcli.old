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

  def invokeCommand(args: Array[String]): Unit = {
    val x = for {
      msg <- Future.successful(login)
    } yield msg
    log.info(s"resp: $x")
    Http.shutdown()

  }
}
