package amitbansal.controllers.requests

import amitbansal.models.Academic
import amitbansal.services.Utils
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class AcademicUpdateReq(
  id: String,
  rollNo: String,
  name: String,
  batch: String,
  @MethodValidation programme: String,
  token: String,
  @MethodValidation category: String
) {

  @MethodValidation
  def validateId = {
    ValidationResult.validate(Utils.checkObjectId(id).isDefined, "Invalid Id")
  }

  @MethodValidation
  def validateProgramme = {
    val programmes = Academic.programmes
    ValidationResult.validate(
      programmes.contains(programme), s"Programme must be one of ${programmes.mkString("(", ", ", ")")}"
    )
  }

  @MethodValidation
  def validateCategory = {
    val categories = Academic.categories
    ValidationResult.validate(
      categories.contains(category), s"Category must be one of ${categories.mkString("(", ", ", ")")}"
    )
  }

}
