package storage

import models.TitleType

case class MovieQuery(minRating: Double = 7.0, minNumVotes: Int = 10000, titleType: TitleType = TitleType.MOVIE,
    startYear: Int = 1900, endYear: Int = 2100) {
  require(startYear <= endYear)
}
