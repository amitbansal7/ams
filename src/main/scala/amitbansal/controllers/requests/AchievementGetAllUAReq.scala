package amitbansal.controllers.requests

//get all unapproved achievements
case class AchievementGetAllUAReq(
  token: String,
  rollno: Option[String],
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
){

}
