package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}
import org.biobank.cli.Session
import org.biobank.cli.Protocol

import org.slf4j.LoggerFactory
import com.typesafe.scalalogging.Logger
import dispatch._, Defaults._
import com.ning.http.client.cookie.Cookie
import scala.util.{ Try, Success, Failure }
import org.json4s._
import org.json4s.jackson.JsonMethods._

object Users extends Command {
  import Protocol._
  import HttpCommand._

  val Name = "users"

  val Help =
    s"""|Used to configure users. """.stripMargin

  val Usage = s"$Name"

  val Log = Logger(LoggerFactory.getLogger(this.getClass))

  implicit val formats = Protocol.formats

  def invokeCommand(args: Array[String]): Unit = {
    Log.trace(s"""args: ${args.mkString(",")}""")
    if (args.isEmpty) {
      print(s"invalid command")
    } else if (args(0) == "list") {
      list
    } else if (args(0) == "add") {
      add(args.slice(1, args.length))
    } else {
      print(s"invalid command: ${args(0)}")
    }
  }

  private def list: Unit = {
    Session.doRequest(Session.theHost / "users") onCommandSuccess { json =>
      Log.trace(s"reply: $json")
      val resp = json.extract[UsersResp]
      printUserTable(resp.data)
    }
  }

  private def printUserTable(users: List[User]): Unit = {
    if (users.isEmpty) {
      print("not users present.")
    } else {
      println("Users:")
      println("\tEmail                Name                 Status               Added                Modified")
      users.foreach { user =>
        printf(
          "\t%-20s %-20s %-20s %-20s %-30s\n",
          user.email,
          user.name,
          user.status,
          timeFormatter.print(user.timeAdded),
          user.timeModified.fold { "-" } { timeFormatter.print(_) })
      }
    }
  }

  private def add(args: Array[String]): Unit = {
    if (args.size != 3) {
      print(s"invalid command")
    } else {
      val name = args(0)
      val email = args(1)
      val password = args(2)
      val cmd = RegisterUserCmd(name, email, password, None)
      val json = compact(Extraction.decompose(cmd))
      Log.trace(s"json: $json")

      val req = (Session.theHost / "users") << json
      Session.doJsonRequest(req) onCommandSuccess { json =>
        Log.debug(s"reply: ${pretty(json)}")
        val status = (json \ "status").extract[String]
        if (status == "success") {
          println(s"user added: $email")
        }
      }
    }
  }
}
