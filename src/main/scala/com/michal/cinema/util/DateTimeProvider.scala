package com.michal.cinema.util

import java.time.{Instant, LocalDateTime, ZoneId}

trait DateTimeProvider {
  def currentInstant(): Instant

  def defaultZoneId: ZoneId

  def instantToLocal(instant: Instant) = LocalDateTime.ofInstant(instant, defaultZoneId)

  def localToInstant(localDateTime: LocalDateTime) = localDateTime.atZone(defaultZoneId).toInstant
}

class DateTimeProviderImpl extends DateTimeProvider {
  override def currentInstant(): Instant = Instant.now()

  override val defaultZoneId: ZoneId = ZoneId.of("Europe/Warsaw")
}
