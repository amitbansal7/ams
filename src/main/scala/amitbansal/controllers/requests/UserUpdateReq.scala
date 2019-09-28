package amitbansal.controllers.requests

case class UserUpdateReq(
  firstName: String,
  lastName: String,
  email: String,
  password: String,
  newEmail: String,
  designation: String
)
