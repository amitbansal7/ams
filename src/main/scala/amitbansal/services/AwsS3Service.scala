package amitbansal.services

import java.io.File

import amitbansal.config.Constants
import com.amazonaws.auth.{AWSCredentials, AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.s3.{AmazonS3Client, AmazonS3ClientBuilder}

object AwsS3Service {
  val credentials: AWSCredentials = new BasicAWSCredentials(
    Constants.resource.getOrElse("Access Key ID", "error").toString,
    Constants.resource.getOrElse("Secret Access Key", "error").toString
  )

  val bucketName = Constants.resource.getOrElse("bucketName", "error").toString
  val region = Constants.resource.getOrElse("bucketRegion", "us-east-2").toString

  val s3Client = AmazonS3ClientBuilder.standard()
    .withCredentials(new AWSStaticCredentialsProvider(credentials))
    .withRegion(region)
    .build();
}

class AwsS3Service {
  import AwsS3Service._

  def uploadImage(file: File, name: String): Boolean = {
    try {
      println("uploading file....")
      s3Client.putObject(bucketName, name, file)
      return true;
    } catch {
      case ex =>
        println(ex.getMessage)
        false
    }
  }

}
