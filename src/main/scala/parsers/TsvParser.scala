package parsers

import java.io.File

import common.rich.path.RichFile._

import scala.io.Source

object TsvParser {
  def parse[T: Parsable](f: File, maxLines: Int = -1): Seq[T] = {
    val v = if (maxLines != -1) {
      val source = Source.fromFile(f)
      val $ = source.getLines().slice(1, maxLines + 1).toVector
      source.close()
      $
    } else f.lines.drop(1)
    v map (_.split("\t")) map implicitly[Parsable[T]].parse
  }
}
