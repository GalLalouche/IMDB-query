package parsers

import java.time.Duration

import common.rich.RichT._
import common.rich.RichTuple._
import common.rich.primitives.RichString._
import models.{AverageRating, Genre, MovieDuration, MovieIdentifier, NumberOfVotes, PersonIdentifier, Title, TitleType, Year}
import parsers.raw_models._

trait Parsable[T] {
  protected val fileNamePrefix: (String, String)
  lazy val fileName: String = fileNamePrefix.toList.mkString(".") + ".tsv"
  def parse(a: Array[String]): T
}

object Parsable {
  private def isExplicitlyEmpty(s: String) = s == "\\N"
  private def maybeParse[T](s: String, f: String => T): Option[T] = if (isExplicitlyEmpty(s)) None else Some(f(s))
  private def maybeList[T](s: String, f: String => T): Seq[T] =
    if (s.isWhitespaceOrEmpty || isExplicitlyEmpty(s)) Nil else s.split(",").map(f)
  private def maybePersons(s: String): Seq[PersonIdentifier] = maybeList(s, PersonIdentifier.apply)

  implicit object RawRatingParsable extends Parsable[RawRating] {
    override protected val fileNamePrefix = "title" -> "ratings"
    override def parse(a: Array[String]): RawRating =
      RawRating(MovieIdentifier(a(0)), AverageRating(a(1).toDouble), NumberOfVotes(a(2).toInt))
  }
  implicit object TitleBasicsParsable extends Parsable[TitleBasics] {
    override protected val fileNamePrefix = "title" -> "basics"
    override def parse(a: Array[String]): TitleBasics = TitleBasics(
      id = MovieIdentifier(a(0)),
      titleType = TitleType.valueOf(a(1).toUpperCase),
      primaryTitle = Title(a(2)), originalTitle = Title(a(3)),
      isAdult = if (a(4) == "1") true else false,
      startYear = maybeParse(a(5), _.toInt |> Year),
      endYear = maybeParse(a(6), _.toInt |> Year),
      runtime = maybeParse(a(7), _.toLong |> MovieDuration.apply),
      genres = maybeList(a(8), _.toUpperCase.replaceAll("-", "_") |> Genre.valueOf)
    )
  }
  implicit object PrincipalsParsable extends Parsable[Principals] {
    override protected val fileNamePrefix = "title" -> "principals"
    override def parse(a: Array[String]): Principals = Principals(
      id = MovieIdentifier(a(0)), cast = maybePersons(a(1)))
  }
  implicit object CrewParsable extends Parsable[Crew] {
    override protected val fileNamePrefix = "title" -> "crew"
    override def parse(a: Array[String]): Crew = Crew(
      id = MovieIdentifier(a(0)), directors = maybePersons(a(1)), writers = maybePersons(a(2)))
  }
  implicit object NameParsable extends Parsable[Name] {
    override protected val fileNamePrefix = "name" -> "basics"
    override def parse(a: Array[String]): Name = Name(
      personIdentifier = PersonIdentifier(a(0)), primaryName = a(1),
      birthYear = maybeParse(a(2), _.toInt |> Year.apply),
      deathYear = maybeParse(a(3), _.toInt |> Year.apply),
      primaryProfession = maybeList(a(4), _.toUpperCase |> RawProfession.valueOf),
      knownForTitles = maybeList(a(5), MovieIdentifier.apply)
    )
  }
}
