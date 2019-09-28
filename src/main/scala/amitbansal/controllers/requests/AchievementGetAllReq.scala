package amitbansal.controllers.requests

case class AchievementGetAllReq(
  rollno: Option[String],
  department: Option[String],
  semester: Option[Int],
  dateFrom: Option[String],
  dateTo: Option[String],
  shift: Option[String],
  section: Option[String],
  sessionFrom: Option[String],
  sessionTo: Option[String],
  category: Option[String],
  offset: Option[Int],
  limit: Option[Int]
)
