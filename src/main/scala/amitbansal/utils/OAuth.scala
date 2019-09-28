package amitbansal.utils

import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.oauth2.OAuthError
import com.twitter.finagle.{OAuth2, Service, SimpleFilter}
import com.twitter.inject.Logging
import com.twitter.util.Future
import javax.inject.Inject

@Inject
class OAuthFilter @Inject()(dataHandler: OAuthDataHandler) extends SimpleFilter[Request, Response] with OAuth2 with Logging {
  override def apply(request: Request, service: Service[Request, Response]): Future[Response] = {
    authorize(request, dataHandler) flatMap { authInfo =>

      service(request)
    } handle {
      case e: OAuthError =>
        error(e.getMessage)
        e.toResponse
    }
  }
}