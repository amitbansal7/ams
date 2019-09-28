package amitbansal.controllers

import amitbansal.controllers.requests._
import amitbansal.models.Achievement
import amitbansal.services.AchievementService
import com.google.inject.Inject
import com.twitter.finagle.http.{Response, Status}
import com.twitter.finatra.http.Controller
import javax.inject.Singleton

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class AchievementController @Inject()(achievementService: AchievementService) extends Controller {

  prefix("/achievement") {

    post("/approve") { req: AchievementActionReq =>
      achievementService.approveAch(req)
    }

    post("/unapprove") { req: AchievementActionReq =>
      achievementService.unApproveAch(req)
    }

    delete("/delete") { req: AchievementActionReq =>
      achievementService.deleteAch(req)
    }

    get("/all") { req: AchievementGetAllReq =>
      achievementService.getAllApproved(req)
    }

    get("/unapproved") { req: AchievementGetAllUAReq =>
      achievementService.getAllUnapproved(req)
    }

    get("/get") { req: AchievementGetReq =>
      achievementService.getOne(req).map(_.getOrElse(Response(Status.NotFound)))
    }

  }
}
