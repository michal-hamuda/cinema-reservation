package com.michal.cinema.screenings.services

import java.time.{Instant, LocalDateTime}
import java.util.UUID

import com.michal.cinema.screenings.domain.Domain.ScreeningId
import com.michal.cinema.screenings.domain.ScreeningRepository
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

case class ScreeningData(
                          id: ScreeningId,
                          title: String,
                          start: LocalDateTime,
                          durationMinutes: Int
                        )

class SearchScreeningsService(
                               db: Database,
                               screeningRepository: ScreeningRepository
                             ) {
  def search(from: Instant, to: Instant): Future[List[ScreeningData]] = {
    db.run(
      screeningRepository.findScreenings(from, to)
    )

    Future.successful(List(
      ScreeningData(
        ScreeningId(UUID.randomUUID()),
        "Matrix reanimacja",
        LocalDateTime.now(),
        62

      ),
      ScreeningData(
        ScreeningId(UUID.randomUUID()),
        "Matrix reanimacja",
        LocalDateTime.now().plusSeconds(3 * 60 * 60),
        62
      )
    ))
  }
}
