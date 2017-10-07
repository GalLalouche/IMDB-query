import java.io.{File, InputStream}

import com.amazonaws.auth.{AWSCredentials, AWSCredentialsProvider}
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.{AmazonS3, AmazonS3ClientBuilder}
import com.amazonaws.{AmazonClientException, AmazonServiceException}
import common.rich.RichInputStream._
import common.rich.RichT._
import common.rich.path.RichFile
import common.rich.path.RichFile._

/**
 * Sample class for downloading name.basics.tsv.gz from the 'current' folder in the
 * imdb-datasets s3 bucket.
 *
 * Use with AWS Java SDK 1.11.156 or later.
 */
object GetObject {
  private val files = List("title.basics", "title.crew", "title.principals", "title.ratings", "name.basics")
      .map(_ + ".tsv.gz")
  private val fileToDownload = "title.crew.tsv.gz"
  def main(args: Array[String]): Unit = {
    val dir = "documents/v1/current/"
    val bucketName = "imdb-datasets"
    val key = dir + fileToDownload
    val s3Client: AmazonS3 = {
      val file = RichFile(getClass.getResource("accessKeys.csv").getFile)
      val (keyId, secretAccessKey) = file.lines(1).mapTo(_.split(",").mapTo(e => e(0) -> e(1)))
      val builder = AmazonS3ClientBuilder.standard()
      builder.setCredentials(new AWSCredentialsProvider {
        override def refresh(): Unit = ???
        override def getCredentials: AWSCredentials = new AWSCredentials {
          override def getAWSAccessKeyId: String = keyId
          override def getAWSSecretKey: String = secretAccessKey
        }
      })
      builder.setRegion("us-east-1")
      builder.build()
    }
    try { // Note: It's necessary to set RequesterPays to true
      val getObjectRequest = new GetObjectRequest(bucketName, key).withRequesterPays(true)
      System.out.println("Downloading object")
      val s3object = s3Client.getObject(getObjectRequest)
      System.out.println("Content-Type: " + s3object.getObjectMetadata.getContentType)
      writeFile(s3object.getObjectContent)
    } catch {
      case ase: AmazonServiceException =>
        System.out.println("Caught an AmazonServiceException, which means your request made it to Amazon S3, but was rejected with an error response for some reason.")
        System.out.println("Error Message:    " + ase.getMessage)
        System.out.println("HTTP Status Code: " + ase.getStatusCode)
        System.out.println("AWS Error Code:   " + ase.getErrorCode)
        System.out.println("Error Type:       " + ase.getErrorType)
        System.out.println("Request ID:       " + ase.getRequestId)
      case ace: AmazonClientException =>
        System.out.println("Caught an AmazonClientException, which means the client encountered an internal error while trying to communicate with S3, such as not being able to access the network.")
        System.out.println("Error Message: " + ace.getMessage)
    }
  }
  private def writeFile(input: InputStream): Unit = {
    val fileToWriteTo = new File("E:/temp/imdb/" + fileToDownload)
    println("Writing input to file " + fileToWriteTo)
    fileToWriteTo.createNewFile()
    fileToWriteTo.clear()
    input writeTo fileToWriteTo
  }
}
