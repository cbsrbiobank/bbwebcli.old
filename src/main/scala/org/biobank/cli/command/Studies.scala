package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import dispatch._, Defaults._
import org.json4s._
import com.ning.http.client.cookie.Cookie

object Studies extends Command {

  val Name = "studies"

  val Help =
    s"""|Used to configure studies. """.stripMargin

  val Usage = s"$Name"

  val log = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val formats = DefaultFormats

  val theHost = host(appConfig.host, appConfig.port)

  def login:Future[Either[String, JValue]] = {
    val req = (theHost / "login").setContentType("application/json", "UTF-8")
    val post = req << s"""{"email": "${appConfig.userEmail}", "password": "administrator"}"""
    val resp = Http(post OK as.json4s.Json).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  // for http headers see:
  //
  // http://stackoverflow.com/questions/12342062/basic-usage-of-dispatch-0-9/12343111#12343111
  def list(token: String):Future[Either[String, JValue]] = {
    val cookie = new Cookie("XSRF-TOKEN", token, token, "localhost", "/", -1, 1000, false, true)
    val req = (theHost / "studies").addCookie(cookie).addHeader("X-XSRF-TOKEN", token)
    val resp = Http(req OK as.json4s.Json).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  def invokeCommand(args: Array[String]): Unit = {
    // val r = for {
    //   resp <- login().right
    // } yield resp
    // log.info(s"resp: $r")

    //log.info(s"resp: ${login().right}")

    // login() match {
    //   case Right(res) => log.info(s"resp: ${res}")
    //   case _ => log.info("login failed")
    // }

    case class LoginResp(token: String)
    case class Study(
      id: String,
      version: Long,
      addedDate: String,
      lastUpdateDate: Option[String],
      name: String,
      description: Option[String],
      status: String)

    login().right.map { json =>
      val token = json.extract[LoginResp]
      list(token.token).right.map { json =>
        log.info(s"resp: $json")
        val study = json.extract[List[Study]]
        log.info(s"resp: ${study}")
      }
    }

    Http.shutdown()
  }
}
