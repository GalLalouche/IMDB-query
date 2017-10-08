package storage

import common.storage.SlickTableUtils.TableProperties
import common.storage.{ColumnMappers, SlickTableUtils, StorageTemplate}
import models._
import slick.jdbc.H2Profile
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.std.FutureInstances
import scalaz.syntax.ToFunctorOps

object MovieStorage extends StorageTemplate[MovieIdentifier, BasicMovie]
    with FutureInstances with ToFunctorOps {

  private val mappers = new ColumnMappers()(H2Profile)
  import mappers._

  private val db = Database.forURL(s"jdbc:h2:E:/imdb/h2", driver = "org.h2.Driver")
  private class RawMovieTable(tag: Tag) extends Table[(
      MovieIdentifier, TitleType, Title, Title, Boolean, Option[Year], Option[Year], Option[MovieDuration],
          Seq[Genre], Option[AverageRating], Option[NumberOfVotes])](tag, "MOVIES") {
    def id = column[MovieIdentifier]("ID", O.PrimaryKey)
    def titleType = column[TitleType]("TITLE_TYPE")
    def titleTypeIdx = index("TITLE_TYPE_IDX", titleType)
    def primaryTitle = column[Title]("PRIMARY_TITLE")
    def originalTitle = column[Title]("ORIGINAL_TITLE")
    def isAdult = column[Boolean]("IS_ADULT")
    def startYear = column[Option[Year]]("START_YEAR")
    def startYearIdx = index("START_YEAR_IDX", startYear)
    def endYear = column[Option[Year]]("END_YEAR")
    def durationInMinutes = column[Option[MovieDuration]]("DURATION_IN_MINUTES")
    def durationIdx = index("DURATION_INDEX", durationInMinutes)
    def genres = column[Seq[Genre]]("GENRES")
    def averageRating = column[Option[AverageRating]]("AVERAGE_RATING")
    def ratingIdx = index("RATING_IDX", averageRating)
    def numberOfVotes = column[Option[NumberOfVotes]]("NUMBER_OF_VOTES")
    def votesIdx = index("VOTES_IDX", numberOfVotes)
    def * = (id, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, durationInMinutes, genres,
        averageRating, numberOfVotes)
  }
  private def to(bm: BasicMovie) = (bm.id, bm.titleType, bm.primaryTitle, bm.originalTitle, bm.isAdult, bm.startYear,
      bm.endYear, bm.duration, bm.genres, bm.rating.map(_.averageRating), bm.rating.map(_.numberOfVotes))

  private val table = TableQuery[RawMovieTable]

  override protected def internalDelete(k: MovieIdentifier) = db.run(table.filter(_.id === k).delete)
  override protected def internalForceStore(k: MovieIdentifier, v: BasicMovie) =
    db.run(table.insertOrUpdate(to(v)))
  override def storeMultiple(kvs: Seq[(MovieIdentifier, BasicMovie)]): Future[Unit] =
    db.run(table ++= kvs.map(_._2).map(to)).void

  private def from(e: (MovieIdentifier, TitleType, Title, Title, Boolean, Option[Year], Option[Year],
      Option[MovieDuration], scala.Seq[Genre], Option[AverageRating], Option[NumberOfVotes])): BasicMovie = BasicMovie(
    e._1, e._2, e._3, e._4, e._5, e._6, e._7, e._8, e._9, for (ar <- e._10; nov <- e._11) yield Rating(ar, nov))

  override def load(k: MovieIdentifier) = db.run(table.filter(_.id === k).result).map(_.headOption.map(from))
  override def utils = SlickTableUtils(new TableProperties {
    override val driver = H2Profile
    override val db = MovieStorage.this.db
    override val table = MovieStorage.this.table
  })

  def count: Future[_] = db.run(table.map(_.startYear).filter(_.isDefined).map(_.get).result)

  def query(mq: MovieQuery): Future[Seq[BasicMovie]] =
    db.run(table.filter(_.averageRating >= AverageRating(mq.minRating))
        .filter(_.numberOfVotes.value >= NumberOfVotes(mq.minNumVotes))
        .filter(_.titleType === mq.titleType)
        .filter(_.startYear >= Year(mq.startYear))
        .filter(_.startYear <= Year(mq.endYear))
        .filter(r => r.primaryTitle === r.originalTitle)
        .sortBy(_.averageRating.desc)
        .take(100)
        .result).map(_ map from)
}
