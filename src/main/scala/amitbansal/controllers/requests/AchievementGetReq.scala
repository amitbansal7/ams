package amitbansal.controllers.requests

import amitbansal.services.Utils
import com.twitter.finatra.request.QueryParam
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class AchievementGetReq(
  @QueryParam id: String
){
  @MethodValidation
  def validateId = {
    ValidationResult.validate(Utils.checkObjectId(id).isDefined, "Invalid Id")
  }
}
