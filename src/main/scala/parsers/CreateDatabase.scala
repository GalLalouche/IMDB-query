package parsers

import common.rich.RichFuture._
import common.rich.collections.RichTraversableOnce._
import common.rich.path.Directory
import storage.{MovieQuery, MovieStorage}
import stuff.TimedMain

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.std.FutureInstances
import scalaz.syntax.ToBindOps

object CreateDatabase extends TimedMain with
    FutureInstances with ToBindOps {
  override def timedMain(): Unit = {
    val dir = Directory("""E:\temp\imdb""")
    def parse[T: Parsable](p: Parsable[T]): Seq[T] = TsvParser.parse(dir / p.fileName)
    //val names = parse(Parsable.NameParsable)
    //val principals = parse(Parsable.PrincipalsParsable)
    //val crews = parse(Parsable.CrewParsable)
    val ratings = timed("Parsing ratings") {parse(Parsable.RawRatingParsable).mapBy(_.id)}
    val basics = timed("Parsing basics") {parse(Parsable.TitleBasicsParsable).map(e => e.toBasicMovie(ratings.get(e.id)))}
    val movieStorage = MovieStorage
    val utils = movieStorage.utils
    timed("Saving to DB") {
      utils.doesTableExist
          .map(e => {
            if (e) {
              println("Clearing table...")
              utils.clearTable().get
            } else {
              println("Creating table...")
              utils.createTable().get
            }
            Unit
          })
          .>>(movieStorage.storeMultiple(basics.map(e => e.id -> e)))
          .get
    }
  }
}
