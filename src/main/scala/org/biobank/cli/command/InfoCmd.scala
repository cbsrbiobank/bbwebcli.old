package org.biobank.cli.command

import org.biobank.cli.BbwebCli.{config => appConfig}

object Info extends Command {

  val Name = "info"

  val Help =
    s"""|Shows the server connection settings. """.stripMargin

  val Usage = s"$Name"

  def invokeCommand(args: Array[String]): Unit = {
    println("Connection info:")
    println(s"\thost: ${appConfig.host}\n\tport: ${appConfig.port}\n\tuserEmail: ${appConfig.userEmail}")
  }
}
