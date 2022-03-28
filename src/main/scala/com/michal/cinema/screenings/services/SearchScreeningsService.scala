package com.michal.cinema.screenings.services

import java.time.{Instant, LocalDateTime}

import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.screenings.services.SearchScreeningsService.ScreeningData
import slick.jdbc.H2Profile.api._

import scala.concurrent.Future

object SearchScreeningsService {

  case class ScreeningData(
                            id: ScreeningId,
                            title: String,
                            start: LocalDateTime,
                            durationMinutes: Int
                          )
}

class SearchScreeningsService(
                               db: Database,
                               screeningRepository: ScreeningRepository
                             ) {
  def search(from: Instant, to: Instant): Future[Seq[ScreeningData]] = {
    db.run(
      screeningRepository.findScreenings(from, to)
    )
  }
}
