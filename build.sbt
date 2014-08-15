import AssemblyKeys._

assemblySettings

name := "bbwebcli"

version := "0.1-SNAPSHOT"

assemblyOption in assembly ~= { _.copy(prependShellScript = Some(defaultShellScript)) }

jarName in assembly := { s"${name.value}-${version.value}" }

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoPackage := "buildinfo"

scalaVersion := "2.11.1"

scalacOptions in ThisBuild ++= Seq(
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "deprecation",        // warning and location for usages of deprecated APIs
  "-feature",           // warning and location for usages of features that should be imported explicitly
  "-language:implicitConversions",
  "-language:higherKinds",
  "-language:existentials",
  "-language:postfixOps",
  "-unchecked",          // additional warnings where generated code depends on assumptions
//  "-Xlint",
//  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused
)

resolvers ++= Seq(
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)


libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging"        % "3.0.0",
  "ch.qos.logback"             %  "logback-classic"      % "1.0.1",
  "com.github.nscala-time"     %% "nscala-time"          % "1.2.0",
  "com.typesafe"               %  "config"               % "1.2.1",
  "net.databinder.dispatch"    %% "dispatch-core"        % "0.11.2",
  "net.liftweb"                %% "lift-json"            % "2.6-RC1"
)

// allows calling System.exit() from application
fork in run := true
