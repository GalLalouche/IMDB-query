package models

case class Person(personIdentifier: PersonIdentifier, primaryName: String, birthYear: Option[Year],
    deathYear: Option[Year], primaryProfession: Seq[Profession], knownForTitles: Seq[MovieIdentifier]) {
  for (by <- birthYear; dy <- deathYear) require(dy.value >= by.value)
}
