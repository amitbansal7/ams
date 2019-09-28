package amitbansal.services

import amitbansal.config.Constants
import com.google.inject.Singleton
import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}

import scala.concurrent.duration._

@Singleton
class JwtService {

  val secretKey = Constants.resource.getOrElse("jwtKey", "error").toString

  val tokenExpiry = (30 days).toSeconds

  def getJwtToken(email: String, department: String): String = {
    Jwt.encode(
      JwtClaim({ s"""{"user":"$email", "dept":"$department"}""" }).issuedNow.expiresIn(tokenExpiry),
      secretKey,
      JwtAlgorithm.HS384
    )
  }

  def decodeToken(token: String) = {
    Jwt.decodeRawAll(token, secretKey, Seq(JwtAlgorithm.HS384))
  }
}
