package amitbansal.services

import amitbansal.config.Constants
import amitbansal.controllers.requests.{UserAddReq, UserAuthReq, UserResetPassReq, UserUpdateReq}
import amitbansal.models.User
import amitbansal.repositories.UserRepository
import amitbansal.utils.Serializers.ObjectIdJsonSerializer
import cats.data.OptionT
import cats.implicits._
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.google.inject.{Inject, Singleton}
import org.mongodb.scala.bson.ObjectId

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.Success
import scala.util.parsing.json.JSON

object UserService {

  case class UserData(
    @JsonSerialize(using = classOf[ObjectIdJsonSerializer]) id: ObjectId,
    email: String, firstName: String, lastName: String, department: String, shift: String, designation: String)

  case class UserServiceResponse(bool: Boolean, message: String)

  case class AuthRes(bool: Boolean, message: String, token: String)

}

@Singleton
class UserService @Inject()(userRepository: UserRepository, jwtService: JwtService) {

  import UserService._

  val secretCode = Constants.resource.getOrElse("inviteCode", "invalidCode").toString

  def existByEmail(email: String): Boolean = {
    val user = Await.result(userRepository.getByEmail(email), 1 seconds)
    user != null
  }

  def reset(userUpdateReq: UserUpdateReq) = {
    userRepository.getByEmail(userUpdateReq.email).map {
      case user: User if user.password == User.getPasshash(userUpdateReq.password) =>
        if (userUpdateReq.email != userUpdateReq.newEmail && existByEmail(userUpdateReq.newEmail)) {
          UserServiceResponse(false, s"Email(${userUpdateReq.newEmail}) already in use")
        } else {
          userRepository.reset(userUpdateReq.email, userUpdateReq.newEmail, userUpdateReq.firstName, userUpdateReq.lastName, userUpdateReq.designation)
          UserServiceResponse(true, "Profile successfully saved")
        }
      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
  }

  def resetPass(userResetPassReq: UserResetPassReq): Future[UserServiceResponse] = {
    userRepository.getByEmail(userResetPassReq.email).map {
      case user: User if user.password.equals(User.getPasshash(userResetPassReq.currentPass)) =>
        userRepository.changePass(userResetPassReq.email, User.getPasshash(userResetPassReq.newPass))
        UserServiceResponse(true, "Password successfully changed")

      case _ => UserServiceResponse(false, "Email or password doesn't match")
    }
  }

  def authenticateUser(userAuthReq: UserAuthReq): Future[AuthRes] = {
    userRepository.getByEmail(userAuthReq.email).map {
      case user: User if user.password == User.getPasshash(userAuthReq.password) =>
        AuthRes(true, "User is authenticated", jwtService.getJwtToken(user.email, user.department))
      case _ =>
        AuthRes(false, "User is not authenticated", "")
    }
  }

  def isUserValid(token: String): Future[Option[UserData]] = {
    OptionT(getUserFromToken(token)).map { user =>
      Some(UserData(user._id, user.email, user.firstName, user.lastName, user.department, user.shift, user.designation))
    }.getOrElse(None)
  }

  def getUserFromToken(token: String): Future[Option[User]] =
    jwtService.decodeToken(token) match {
      case Success(value) =>
        JSON.parseFull(value._2) match {
          case Some(map: Map[String, String]) =>
            map.get("user").map { email =>
              userRepository.getByEmail(email).map(u => Some(u))
            }.getOrElse(Future(None))
        }
      case _ => Future(None)
    }

  def addUser(userAddReq: UserAddReq): UserServiceResponse = {
    if (existByEmail(userAddReq.email))
      UserServiceResponse(false, s"User with email ${userAddReq.email} already exists")
    else {
      userRepository.addUser(User.apply(userAddReq.email, userAddReq.password, userAddReq.firstName, userAddReq.lastName, userAddReq.department, userAddReq.shift, userAddReq.designation))
      UserServiceResponse(true, "Account successfully created")
    }
  }
}
