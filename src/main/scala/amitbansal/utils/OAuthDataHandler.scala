package amitbansal.utils

import amitbansal.models.User
import com.twitter.finagle.oauth2.{AccessToken, AuthInfo, DataHandler}
import com.twitter.util.Future

class OAuthDataHandler extends DataHandler[User]{
  override def validateClient(clientId: String, clientSecret: String, grantType: String): Future[Boolean] = {
    Future(true)
  }

  override def findUser(username: String, password: String): Future[Option[User]] = ???

  override def createAccessToken(authInfo: AuthInfo[User]): Future[AccessToken] = ???

  override def getStoredAccessToken(authInfo: AuthInfo[User]): Future[Option[AccessToken]] = ???

  override def refreshAccessToken(authInfo: AuthInfo[User], refreshToken: String): Future[AccessToken] = ???

  override def findAuthInfoByCode(code: String): Future[Option[AuthInfo[User]]] = ???

  override def findAuthInfoByRefreshToken(refreshToken: String): Future[Option[AuthInfo[User]]] = ???

  override def findClientUser(clientId: String, clientSecret: String, scope: Option[String]): Future[Option[User]] = ???

  override def findAccessToken(token: String): Future[Option[AccessToken]] = ???

  override def findAuthInfoByAccessToken(accessToken: AccessToken): Future[Option[AuthInfo[User]]] = ???
}
