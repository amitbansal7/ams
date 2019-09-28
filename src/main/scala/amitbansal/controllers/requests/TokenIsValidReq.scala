package amitbansal.controllers.requests

import com.twitter.finatra.validation.NotEmpty

case class TokenIsValidReq(
  @NotEmpty token: String
)
