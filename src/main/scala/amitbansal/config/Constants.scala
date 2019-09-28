package amitbansal.config

import scala.util.parsing.json.JSON

object Constants {
  val json = JSON.parseFull(
    scala.io.Source.fromResource("data.json")
      .getLines()
      .toList
      .mkString
  )

  val resource = json.map {
    case map: Map[String, String] => map
    case _ => {
      println("data.json parsing failed")
      Map[String, Any]()
    }
  }.get
}
