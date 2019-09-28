package amitbansal.controllers.requests

case class UserResetPassReq(
  email: String,
  currentPass: String,
  newPass: String
)
