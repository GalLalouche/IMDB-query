package models

import slick.lifted.MappedTo

case class AverageRating(value: Double) extends MappedTo[Double] {
  require (value <= 10 && value >= 0)
}
