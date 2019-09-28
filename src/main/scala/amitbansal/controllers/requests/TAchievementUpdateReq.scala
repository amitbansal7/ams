package amitbansal.controllers.requests

import amitbansal.models.TAchievement
import amitbansal.services.Utils
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class TAchievementUpdateReq(
  token: String,
  id: String,
  @MethodValidation taType: String,
  @MethodValidation subType: Option[String],
  international: Boolean,
  topic: String,
  published: String,
  sponsored: Option[Boolean],
  reviewed: Option[Boolean],
  date: String,
  description: Option[String],
  msi: Boolean,
  place: Option[String]
) {

  @MethodValidation
  def validateId = {
    ValidationResult.validate(Utils.checkObjectId(id).isDefined, "Invalid Id")
  }

  def validateTaTypes = {
    val types = TAchievement.taTypes
    ValidationResult.validate(
      types.contains(taType), s"Type must be one of ${types.mkString("(", ", ", ")")}"
    )
  }

  def validateSubTypes = {
    val subTypes = TAchievement.subTypes
    ValidationResult.validate(
      !subType.isDefined || subTypes.contains(subType.get), s"Sub type must be one of ${subTypes.mkString("(", ", ", ")")}"
    )
  }
}
