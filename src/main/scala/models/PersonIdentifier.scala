package models

import java.util.regex.Pattern

case class PersonIdentifier(id: String) {
  require(PersonIdentifier.regex.matcher(id).find())
}
object PersonIdentifier {
  private val regex = Pattern.compile("""nm\d{6}""")
}
