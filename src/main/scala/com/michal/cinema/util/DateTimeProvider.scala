package com.michal.cinema.util

import java.time.{Instant, LocalDateTime, ZoneId}

trait DateTimeProvider {
  def currentInstant(): Instant

  def instantToLocal(instant: Instant) = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Warsaw"))
  def localToInstant(localDateTime: LocalDateTime) = localDateTime.atZone(ZoneId.of("Europe/Warsaw")).toInstant
}

class DateTimeProviderImpl extends DateTimeProvider {
  override def currentInstant(): Instant = Instant.now()
}
