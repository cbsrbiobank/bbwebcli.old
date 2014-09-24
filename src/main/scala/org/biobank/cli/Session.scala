package org.biobank.cli

import org.biobank.cli.BbwebCli.{config => appConfig}
import dispatch._, Defaults._
import org.json4s._
import org.json4s.jackson.JsonMethods._
import com.ning.http.client.cookie.Cookie
import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger

object Session {
  import Protocol._

  private val Log = Logger(LoggerFactory.getLogger(this.getClass))

  val theHost = host(appConfig.host, appConfig.port)

  implicit val formats = DefaultFormats

  var tokenOption: Option[String] = None

  var cookie: Cookie = null

  case class LoginParams(email: String, password: String)

  def login: Future[Either[String, JValue]] = {
    val loginParams = LoginParams(appConfig.userEmail, "administrator")

    val req = (theHost / "login").setContentType("application/json", "UTF-8")
    val post = req << compact(Extraction.decompose(loginParams))
    val resp = Http(post OK as.json4s.Json).either
    for (err <- resp.left) yield "Can't connect: " + err.getMessage
  }

  // for http headers see:
  //
  // http://stackoverflow.com/questions/12342062/basic-usage-of-dispatch-0-9/12343111#12343111
  private def executeRequest(request: Req, token: String): Future[Either[String, JValue]] = {
    val req = request.addCookie(cookie).addHeader("X-XSRF-TOKEN", token)
    val resp = Http(req OK as.json4s.Json).either
    for (err <- resp.left) yield "request failed: " + err.getMessage
  }

  def doRequest(request: Req): Future[Either[String, JValue]] = {
    tokenOption match {
      case Some(t) => executeRequest(request, t)
      case None =>
        login().fold(
          err => {
            Future.successful(Left(err))
          },
          json => {
            val loginResp = json.extract[LoginResp]
            Log.trace(s"login resp: ${compact(json)}")
            tokenOption = Some(loginResp.data)
            cookie = new Cookie(
              "XSRF-TOKEN", loginResp.data, loginResp.data, "localhost", "/", -1, 1000, false, true)
            executeRequest(request, loginResp.data)
          }
        )
    }
  }

  def doJsonRequest(request: Req): Future[Either[String, JValue]] = {
    doRequest(request.setContentType("application/json", "UTF-8"))
  }
}
