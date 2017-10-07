package stuff

import scala.concurrent.duration._

trait Debug {
  import Debug._
	private class Interval(timeInMs: Long) {
		override def toString: String = {
			val d = Duration(timeInMs, MILLISECONDS)
			timeInMs match {
				case _ if timeInMs < 1000 ⇒ s"${d.toMillis} milliseconds"
				case _ if timeInMs < 60 * 1000 ⇒ s"${d.toSeconds}.${d.toMillis % 1000}s"
				case _ if timeInMs < 60 * 60 * 1000 ⇒ f"${d.toMinutes}%02d:${d.toSeconds % 60}%02dm"
				case _ if timeInMs < 24 * 60 * 60 * 1000 ⇒ f"${d.toHours}%02d:${d.toMinutes % 60}%02d:${d.toSeconds % (60 * 60) % 60}%02dh"
			}
		}
	}
	def timed[T](task: String = "Task")(f: ⇒ T): T = {
		println("\t" * depth + "BEGIN: " + task + "...")
		val start = System.currentTimeMillis
		depth += 1
		val $ = f
		depth -= 1
		println("\t" * depth + "END: " + task + s" \ttook ${new Interval(System.currentTimeMillis - start)}")
		$
	}

	def echoLocation() {
		val trace = Thread.currentThread.getStackTrace()(3)
		println(s"${trace.getClassName}@${trace.getLineNumber}")
	}

}

object Debug {
	private var depth = 0
}
