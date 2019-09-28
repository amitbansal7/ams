package amitbansal.repositories

import amitbansal.config.MongoConfig
import amitbansal.models.Academic
import javax.inject.Singleton
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.mutable.Document

@Singleton
class AcademicRepository {

  val academicCollection = MongoConfig.getAcademicCollection

  def add(acad: Academic) =
    academicCollection.insertOne(acad).toFuture()

  def delete(id: ObjectId) =
    academicCollection.deleteOne(Document("_id" -> id)).toFuture()

  def update(id: ObjectId, rollNo: String, name: String, batch: String, programme: String, category: String) =
    academicCollection
      .updateOne(
        Document("_id" -> id),
        Document(
          "$set" -> Document(
            "rollNo" -> rollNo,
            "name" -> name,
            "batch" -> batch,
            "programme" -> programme,
            "category" -> category
          )
        )
      ).toFuture()

  def getAll() =
    academicCollection.find().toFuture()

}
