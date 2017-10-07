package models

case class BasicMovie(id: MovieIdentifier, titleType: TitleType, primaryTitle: Title, originalTitle: Title,
    isAdult: Boolean, startYear: Option[Year], endYear: Option[Year], duration: Option[MovieDuration],
    genres: Seq[Genre], rating: Option[Rating])
