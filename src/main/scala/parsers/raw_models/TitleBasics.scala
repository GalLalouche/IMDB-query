package parsers.raw_models

import models.{BasicMovie, Genre, MovieDuration, MovieIdentifier, Title, TitleType, Year}

private[parsers] case class TitleBasics(id: MovieIdentifier, titleType: TitleType, primaryTitle: Title, originalTitle: Title,
    isAdult: Boolean, startYear: Option[Year], endYear: Option[Year], runtime: Option[MovieDuration],
    genres: Seq[Genre]) {
  def toBasicMovie(rating: Option[RawRating]): BasicMovie = BasicMovie(
    id, titleType, primaryTitle, originalTitle, isAdult, startYear, endYear, runtime, genres, rating.map(_.toRating))
}
