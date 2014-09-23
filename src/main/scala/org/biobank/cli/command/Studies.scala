package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import dispatch._, Defaults._
import org.json4s._
import com.ning.http.client.cookie.Cookie

object Studies extends Command with HttpCommand {

  val Name = "studies"

  val Help =
    s"""|Used to configure studies. """.stripMargin

  val Usage = s"$Name"

  val log = Logger(LoggerFactory.getLogger(this.getClass))

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

    case class Study(
      id: String,
      version: Long,
      addedDate: String,
      lastUpdateDate: Option[String],
      name: String,
      description: Option[String],
      status: String)
    case class StudiesResp(status: String, data: List[Study])

    doRequest("studies")().fold(
      err => {
        print(err)
        Http.shutdown()
      },
      json => {
        log.info(s"reply: $json")
        val resp = json.extract[StudiesResp]
        resp.data.foreach { study =>
          log.info(s"study: ${study.name}: ${study.id}")
        }
        Http.shutdown()
      }
    )
    ()
  }
}
