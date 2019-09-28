import amitbansal.controllers.{AcademicController, AchievementController, TAchievementController, UserController}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http._
import com.twitter.finatra.http.filters.{CommonFilters, LoggingMDCFilter, TraceIdMDCFilter}
import com.twitter.finatra.http.routing.HttpRouter
import controllers.HealthController

class AmsServer extends HttpServer {
  override def configureHttp(router: HttpRouter): Unit = {
    router
      .filter[CommonFilters]
      .filter[LoggingMDCFilter[Request, Response]]
      .filter[TraceIdMDCFilter[Request, Response]]
      //      .filter[OAuthFilter]
      .add[HealthController]
      .add[UserController]
      .add[AcademicController]
      .add[TAchievementController]
      .add[AchievementController]
  }
}

object Server extends AmsServer