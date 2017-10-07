package models

import slick.lifted.MappedTo

case class Title(value: String) extends AnyVal with MappedTo[String]
