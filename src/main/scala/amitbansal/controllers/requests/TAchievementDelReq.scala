package amitbansal.controllers.requests

import amitbansal.services.Utils
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class TAchievementDelReq(
  @MethodValidation id: String,
  token: String
) {
  @MethodValidation
  def validateId = {
    ValidationResult.validate(Utils.checkObjectId(id).isDefined, "Invalid Id")
  }
}
