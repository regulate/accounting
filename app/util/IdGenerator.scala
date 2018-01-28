package util

import java.util.concurrent.atomic.AtomicLong
import javax.inject.Singleton

trait IDGenerator[A <: AnyVal] {
  def generate: A
}

@Singleton
class AtomicLongCounter extends IDGenerator[Long] {
  private val counter: AtomicLong = new AtomicLong(0)

  override def generate: Long = counter.incrementAndGet()
}
