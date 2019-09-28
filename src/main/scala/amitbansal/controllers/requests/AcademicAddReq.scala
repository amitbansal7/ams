package amitbansal.controllers.requests

import amitbansal.models.Academic
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class AcademicAddReq(
  rollNo: String,
  name: String,
  batch: String,
  @MethodValidation programme: String,
  token: String,
  @MethodValidation category: String
) {
  @MethodValidation
  def validateProgramme = {

    val programmes = Academic.programmes

    ValidationResult.validate(
      programmes.contains(programme),
      s"Programme must be one of ${programmes.mkString("(", ", ", ")")}"
    )
  }

  @MethodValidation
  def validateCategory = {

    val categories = Academic.categories

    ValidationResult.validate(
      categories.contains(category),
      s"Category must be one of ${categories.mkString("(", ", ", ")")}"
    )
  }

}
