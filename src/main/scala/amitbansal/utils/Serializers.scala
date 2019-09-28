package amitbansal.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.{JsonSerializer, SerializerProvider}
import org.mongodb.scala.bson.ObjectId

object Serializers {

  class ObjectIdJsonSerializer extends JsonSerializer[ObjectId] {
    override def serialize(value: ObjectId, gen: JsonGenerator, serializers: SerializerProvider): Unit = {
      if (value == null) gen.writeNull
      else gen.writeString(value.toHexString)
    }
  }

}
