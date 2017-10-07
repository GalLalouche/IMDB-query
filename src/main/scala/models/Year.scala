package models

import slick.lifted.MappedTo

case class Year(value: Int) extends MappedTo[Int] with Ordered[Year] {
  require (value < 2200, s"Invalid year <$value>")
  override def compare(that: Year) = value compare that.value
}
