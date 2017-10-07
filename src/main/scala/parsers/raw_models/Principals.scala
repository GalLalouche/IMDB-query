package parsers.raw_models

import models.{MovieIdentifier, PersonIdentifier}

private[parsers] case class Principals(id: MovieIdentifier, cast: Seq[PersonIdentifier])
