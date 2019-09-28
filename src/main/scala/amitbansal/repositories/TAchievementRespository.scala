package amitbansal.repositories

import amitbansal.config.MongoConfig
import amitbansal.models.TAchievement
import com.google.inject.Singleton
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document

import scala.concurrent.Future

@Singleton
class TAchievementRepository {

  val tAchievementsCollection = MongoConfig.getTAchievementsCollection

  def add(tAchievement: TAchievement) =
    tAchievementsCollection.insertOne(tAchievement).toFuture()

  def getAll(): Future[Seq[TAchievement]] =
    tAchievementsCollection.find().toFuture()

  def getAllByUserId(userId: ObjectId) =
    tAchievementsCollection
      .find(Document("user" -> userId))
      .toFuture()

  def deleteOne(id: ObjectId) =
    tAchievementsCollection
      .deleteOne(Document("_id" -> id))
      .toFuture()

  def update(id: ObjectId, tAchievement: TAchievement) =
    tAchievementsCollection.updateOne(
      Document("_id" -> id),
      Document(
        "$set" ->
          Document(
            "taType" -> tAchievement.taType,
            "date" -> tAchievement.date,
            "description" -> tAchievement.description,
            "msi" -> tAchievement.msi,
            "international" -> tAchievement.international
          )
      )
    ).toFuture()

  def getOneById(id: ObjectId) =
    tAchievementsCollection
      .find(Document("_id" -> id))
      .first()
      .toFuture()
}
