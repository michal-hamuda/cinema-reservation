package com.michal.demo.services

import java.time.{Instant, LocalDateTime}

import com.michal.demo.Domain.ScreeningId

import scala.concurrent.Future

case class ScreeningData(
                          id: ScreeningId,
                         title: String,
                         start: LocalDateTime,
                         durationMinutes: Int,
                         bookingAvailable: Boolean
                        )

class SearchScreeningsService {
  def search(from: Instant, to: Instant): Future[List[ScreeningData]] = {
    Future.successful(List(
      ScreeningData(
        ScreeningId(150),
        "Matrix reanimacja",
        LocalDateTime.now(),
        62,
        bookingAvailable = false
      ),
      ScreeningData(
        ScreeningId(159),
        "Matrix reanimacja",
        LocalDateTime.now().plusSeconds(3*60*60),
        62,
        bookingAvailable = true
      )
    ))
  }
}
