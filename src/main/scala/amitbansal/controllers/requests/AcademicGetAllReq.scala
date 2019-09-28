package amitbansal.controllers.requests

import com.twitter.finatra.request.QueryParam

case class AcademicGetAllReq(
  @QueryParam programme: Option[String],
  @QueryParam batch: Option[String],
  @QueryParam category: Option[String]
)
