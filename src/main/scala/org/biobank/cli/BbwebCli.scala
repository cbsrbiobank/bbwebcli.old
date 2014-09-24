package org.biobank.cli

import org.biobank.cli.command.Commands

import com.typesafe.config.ConfigFactory

/**
  * NOTE: From SBT use single quotes to use an argument with spaces in it.
  * Eg. run users add 'test user' test2@test.com testuser
  */
object BbwebCli {

  case class Config(host: String, port: Int, userEmail: String)

  val ConfigResourceName = "bbwebcli"

  val ConfigPath = "bbweb"

  lazy val config: Config = getConfig

  val SplitBySpacesRegex = """ (?=([^"']*["'][^"']*["'])*[^"']*$)"""

  def main(args: Array[String]) = {
    println(s"${buildinfo.BuildInfo.name} version: ${buildinfo.BuildInfo.version}")
    addCommands

    if (args.size < 1) {
      println("Error: command not specified.\n")
      Commands.showCommands
      System.exit(1)
    }

    val quotedArgs = args.mkString(" ").split(SplitBySpacesRegex)
    val commandName = quotedArgs(0)

    if (commandName == "help") {
      if (args.size == 1) {
        Commands.showCommandsAndHelp
        System.exit(0)
      } else if (args.size == 2) {
        Commands.showCommandHelp(args(1))
        System.exit(0)
      } else  {
        println("\tError: invalid command:" + args.mkString(" "))
        System.exit(1)
      }
    }

    config
    Commands.invokeCommand(commandName, quotedArgs.slice(1, quotedArgs.length))
  }

  def addCommands = {
    Commands.addCommand(org.biobank.cli.command.Info)
    Commands.addCommand(org.biobank.cli.command.Studies)
    Commands.addCommand(org.biobank.cli.command.Users)
  }

  def getConfig: Config = {
    val conf = ConfigFactory.load(ConfigResourceName)

    if (!conf.hasPath(ConfigPath)) {
      println(s"\tError: settings not found in ${ConfigResourceName}.conf")
      System.exit(1)
    }

    val appConf = conf.getConfig(ConfigPath);
    val host = if (appConf.hasPath("host")) { appConf.getString("host") } else { "localhost" }
    val port = if (appConf.hasPath("port")) { appConf.getInt("port") } else { 9000 }
    val userEmail = if (appConf.hasPath("user_email")) { appConf.getString("user_email") } else { "admin@amdin.com" }

    Config(host, port, userEmail)
  }

}
