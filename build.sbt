name := "ams"

version := "0.1"

//scalaVersion := "2.12.10"

lazy val versions = new {
  val finatra = "19.8.0"
  val logback = "1.1.7"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com"
)

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "org.mongodb.scala" %% "mongo-scala-driver" % "2.4.2",
  "commons-codec" % "commons-codec" % "1.9",
  "com.pauldijou" %% "jwt-core" % "0.18.0",
  "org.typelevel" %% "cats-core" % "2.0.0-RC1",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.464",
  "com.github.finagle" %% "finagle-oauth2" % "19.8.0"
)

