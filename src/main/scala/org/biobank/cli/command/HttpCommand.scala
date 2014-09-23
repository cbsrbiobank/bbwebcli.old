package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}
import dispatch._, Defaults._
import org.json4s._
import com.ning.http.client.cookie.Cookie
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

object Responses {

  case class LoginResp(status: String, data: String)

}


trait HttpCommand {
  import Responses._

  val Log = Logger(LoggerFactory.getLogger(this.getClass))

  val theHost = host(appConfig.host, appConfig.port)

  implicit val formats = DefaultFormats

  var tokenOption: Option[String] = None

  def login:Future[Either[String, JValue]] = {
    val req = (theHost / "login").setContentType("application/json", "UTF-8")
    val post = req << s"""{"email": "${appConfig.userEmail}", "password": "administrator"}"""
    val resp = Http(post OK as.json4s.Json).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  // for http headers see:
  //
  // http://stackoverflow.com/questions/12342062/basic-usage-of-dispatch-0-9/12343111#12343111
  private def executeRequest(request: String, token: String): Future[Either[String, JValue]] = {
    val cookie = new Cookie("XSRF-TOKEN", token, token, "localhost", "/", -1, 1000, false, true)
    val req = (theHost / request).addCookie(cookie).addHeader("X-XSRF-TOKEN", token)
    val resp = Http(req OK as.json4s.Json).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  def doRequest(request: String): Future[Either[String, JValue]] = {
    tokenOption match {
      case Some(t) => executeRequest(request, t)
      case None =>
        login().fold(
          err => {
            Log.error(err)
            Future.successful(Left(err))
          },
          json => {
            val loginResp = json.extract[LoginResp]
            Log.info(s"login resp: $json, $loginResp")
            tokenOption = Some(loginResp.data)
            executeRequest(request, loginResp.data)
          }
        )
    }
  }

}

