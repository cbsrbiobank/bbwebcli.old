package org.biobank.cli

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.json4s._

object Protocol {

  val IsoDateTimePattern = "yyyy-MM-dd'T'HH:mm:ssZ"
  val LocalDateTimePattern = "yyyy-MM-dd HH:mm:ss"

  val formats = new DefaultFormats {
    override def dateFormatter = new java.text.SimpleDateFormat(IsoDateTimePattern)
  } ++ org.json4s.ext.JodaTimeSerializers.all

  val timeFormatter = DateTimeFormat.forPattern(LocalDateTimePattern);

  case class LoginResp(status: String, data: String)

  case class Study(
    id: String,
    version: Long,
    addedDate: String,
    lastUpdateDate: Option[String],
    name: String,
    description: Option[String],
    status: String)

  case class StudiesResp(status: String, data: List[Study])

  case class User(
    id: String,
    version: Long,
    email: String,
    name: String,
    avatarUrl: Option[String],
    status: String,
    timeAdded: DateTime,
    timeModified: Option[DateTime]
  )

  case class UsersResp(status: String, data: List[User])

  case class RegisterUserCmd(
    name: String,
    email: String,
    password: String,
    avatarUrl: Option[String])

}

