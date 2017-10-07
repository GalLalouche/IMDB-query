package parsers

import common.rich.path.Directory

trait CompoundParser[T] {
  def parse(dir: Directory): Seq[T]
}
