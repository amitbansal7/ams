package amitbansal.controllers

import amitbansal.controllers.requests._
import amitbansal.services.UserService
import com.google.inject.Inject
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class UserController @Inject()(userService: UserService) extends Controller {

  prefix("/users") {
    post("/add") { userAddReq: UserAddReq =>
      userService.addUser(userAddReq)
    }
    post("/auth") { userAuthReq: UserAuthReq =>
      userService.authenticateUser(userAuthReq)
    }

    post("/resetpass") { userResetPassReq: UserResetPassReq =>
      userService.resetPass(userResetPassReq)
    }

    put("/reset") { userUpdateReq: UserUpdateReq =>
      userService.reset(userUpdateReq)
    }

    get("/isvalid") { tokenIsValidReq: TokenIsValidReq =>
      userService.isUserValid(tokenIsValidReq.token)
        .map(_.getOrElse(response.status(Status.Unauthorized)))
    }
  }
}
