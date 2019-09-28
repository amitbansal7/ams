package amitbansal.services

import org.mongodb.scala.bson.ObjectId

object Utils {

  def checkObjectId(id: String): Option[ObjectId] = {
    try {
      val objId = new ObjectId(id)
      Some(objId)
    } catch {
      case _ => None
    }
  }

}
