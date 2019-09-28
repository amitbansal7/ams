package controllers

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

class HealthController extends Controller {
  get("/") { req: Request =>
    Map("status" -> "Up and running")
  }
}
