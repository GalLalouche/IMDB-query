package parsers.raw_models

import models.{MovieIdentifier, PersonIdentifier}

case class Crew(id: MovieIdentifier, directors: Seq[PersonIdentifier], writers: Seq[PersonIdentifier])
