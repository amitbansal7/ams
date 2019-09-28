package amitbansal.controllers.requests

import amitbansal.models.TAchievement
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class TAchievementAddReq(
  token: String,
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

  def validateTaTypes = {
    val types = TAchievement.taTypes
    ValidationResult.validate(
      types.contains(taType),
      s"Type must be one of ${types.mkString("(", ", ", ")")}"
    )
  }

  def validateSubTypes = {
    val subTypes = TAchievement.subTypes
    ValidationResult.validate(
      !subType.isDefined || subTypes.contains(subType.get),
      s"Sub type must be one of ${subTypes.mkString("(", ", ", ")")}"
    )
  }
}
