package amitbansal.controllers.requests

case class TAchievementGetAllReq(
  fromDate: Option[String],
  toDate: Option[String],
  department: Option[String],
  taType: Option[String]
)
