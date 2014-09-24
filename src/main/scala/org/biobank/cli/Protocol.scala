package org.biobank.cli

object Protocol {

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

}

