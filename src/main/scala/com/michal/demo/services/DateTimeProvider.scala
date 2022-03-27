package com.michal.demo.services

import java.time.Instant

trait DateTimeProvider {
  def currentInstant(): Instant
}

class DateTimeProviderImpl extends DateTimeProvider {
  override def currentInstant(): Instant = Instant.now()
}
