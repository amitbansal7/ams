package amitbansal.controllers

import amitbansal.controllers.requests._
import amitbansal.services.TAchievementService
import com.google.inject.Inject
import com.twitter.finatra.http.Controller

import scala.concurrent.ExecutionContext.Implicits.global

class TAchievementController @Inject()(tAchievementService: TAchievementService) extends Controller {

  prefix("/tachievements") {

    post("/add") { req: TAchievementAddReq =>
      tAchievementService.add(req)
    }
    put("/update") { req: TAchievementUpdateReq =>
      tAchievementService.update(req)
    }

    delete("/delete") { req: TAchievementDelReq =>
      tAchievementService.deleteOne(req)
    }

    get("/all") { req: TAchievementGetAllReq =>
      tAchievementService.getAll(req)
    }

    get("/allagg") { req: TAchievementGetAllAggReq =>
      tAchievementService.getAllAggregated(req).flatMap(identity)
    }

    get("/allUserid") { req: TAchievementGetAllByUserIdReq =>
      tAchievementService.getAllForUserId(req)
    }
  }
}
