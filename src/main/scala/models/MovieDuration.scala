package models

import java.time.Duration

import slick.lifted.MappedTo

case class MovieDuration(value: Long) extends MappedTo[Long] {
  val d: Duration = Duration.ofMinutes(value)
}
