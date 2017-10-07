package models

import slick.lifted.MappedTo

case class NumberOfVotes(value: Int) extends MappedTo[Int] {
  require (value > 0)
}
