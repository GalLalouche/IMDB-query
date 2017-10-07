package parsers.raw_models

import models.{MovieIdentifier, PersonIdentifier, Year}

private[parsers] case class Name(personIdentifier: PersonIdentifier, primaryName: String, birthYear: Option[Year],
    deathYear: Option[Year], primaryProfession: Seq[RawProfession], knownForTitles: Seq[MovieIdentifier])
