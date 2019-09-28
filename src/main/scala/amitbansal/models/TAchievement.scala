package amitbansal.models

import amitbansal.utils.Serializers.ObjectIdJsonSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.mongodb.scala.bson.ObjectId

object TAchievement {

  val taTypes = Set(
    "Book",
    "Journal",
    "Conference",
    "SeminarAttended"
  )

  val subTypes = Set(
    "SEMINAR",
    "CONFERENCE",
    "WORKSHOP",
    "FDP",
    "FDP1WEEK"
  )

  def apply(
    _id: ObjectId,
    user: ObjectId,
    taType: String,
    subType: Option[String],
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
    place: Option[String]
  ): TAchievement = new TAchievement(_id, user, taType, subType, international, topic, published, sponsored, reviewed, date, description, msi, place)

  def apply(
    user: ObjectId,
    taType: String,
    subType: Option[String],
    international: Boolean,
    topic: String,
    published: String,
    sponsored: Option[Boolean],
    reviewed: Option[Boolean],
    date: String,
    description: Option[String],
    msi: Boolean,
    place: Option[String]
  ): TAchievement = new TAchievement(new ObjectId(), user, taType, subType, international, topic, published, sponsored, reviewed, date, description, msi, place)
}

//Teacher achievement object
case class TAchievement(
  @JsonSerialize(using = classOf[ObjectIdJsonSerializer]) _id: ObjectId,
  @JsonSerialize(using = classOf[ObjectIdJsonSerializer]) user: ObjectId,
  taType: String,
  subType: Option[String],
  international: Boolean, // (international = True) (national = False).
  topic: String,
  published: String, //[Name of publisher, Place, Presented at, Presented At]
  sponsored: Option[Boolean],
  reviewed: Option[Boolean],
  date: String,
  description: Option[String],
  msi: Boolean,
  place: Option[String]
) {

}
