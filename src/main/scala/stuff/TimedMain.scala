package stuff

import common.rich.RichT._

trait TimedMain extends Debug {
  protected def timedMain()

  final def main(args: Array[String]) {
    timed("Main of " + this.simpleName) {
      timedMain()
    }
  }
}
