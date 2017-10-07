package parsers.raw_models

import models.{AverageRating, MovieIdentifier, NumberOfVotes, Rating}

private[parsers] case class RawRating(id: MovieIdentifier, rating: AverageRating, numVotes: NumberOfVotes) {
  def toRating: Rating = Rating(rating, numVotes)
}
