package amitbansal.controllers.requests

import amitbansal.config.Constants
import amitbansal.models.Achievement
import com.twitter.finatra.validation.{MethodValidation, ValidationResult}

case class UserAddReq(
  firstName: String,
  lastName: String,
  email: String,
  @MethodValidation code: String,
  @MethodValidation department: String,
  @MethodValidation shift: String,
  password: String,
  designation: String
) {
  @MethodValidation
  def validateCode = {
    val inviteCode = Constants.resource.getOrElse("inviteCode", "invalidCode").toString
    ValidationResult.validate(
      code.equals(inviteCode), "secret code is wrong"
    )
  }

  @MethodValidation
  def validateShift = {
    val shifts = Achievement.shifts
    ValidationResult.validate(
      shifts.contains(shift), s"Shift must be one of ${shifts.mkString("(", ", ", ")")}"
    )
  }

  @MethodValidation
  def validateDepartment = {
    val departments = Achievement.departments
    ValidationResult.validate(
      departments.contains(department), s"Department must be one of ${departments.mkString("(", ", ", ")")}"
    )
  }
}

