package parsers

import common.rich.RichFuture._
import common.rich.collections.RichTraversableOnce._
import common.rich.path.Directory
import storage.{MovieQuery, MovieStorage}
import stuff.TimedMain

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.std.FutureInstances
import scalaz.syntax.{ToBindOps, ToFunctorOps}

object RunQuery extends TimedMain with
    FutureInstances with ToBindOps {
  override def timedMain(): Unit = {
    val movieStorage = MovieStorage
    val result = movieStorage.query(MovieQuery(startYear = 2015, endYear = 2015, minNumVotes = 50000, minRating = 7.5)).get
    //val result = movieStorage.count.get
    println(result mkString "\n")
  }
}
