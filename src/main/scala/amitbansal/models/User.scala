package amitbansal.models

import amitbansal.utils.Serializers.ObjectIdJsonSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.mongodb.scala.bson.ObjectId
import org.apache.commons.codec.digest.DigestUtils

import scala.annotation.meta.field

object User {

  def getPasshash(password: String): String =
    DigestUtils.sha256Hex(password)

  def apply(
    _id: ObjectId,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    department: String,
    shift: String,
    designation: String
  ): User = new User(_id, email, password, firstName, lastName, department, shift, designation)

  def apply(
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    department: String,
    shift: String,
    designation: String
  ): User = {
    new User(new ObjectId(), email, getPasshash(password), firstName, lastName, department.toLowerCase, shift, designation)
  }
}


case class User(
  @JsonSerialize(using = classOf[ObjectIdJsonSerializer]) _id: ObjectId,
  email: String,
  password: String,
  firstName: String,
  lastName: String,
  department: String,
  shift: String,
  designation: String
) {

}