package models

import java.util.regex.Pattern

import slick.lifted.MappedTo

case class MovieIdentifier(value: String) extends MappedTo[String] {
  require(MovieIdentifier.regex.matcher(value).find(), s"ID <$value> did not match expected pattern")
}

object MovieIdentifier {
  private val regex = Pattern.compile("""tt\d{7}""")
}
