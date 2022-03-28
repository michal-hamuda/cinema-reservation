package com.michal.cinema.screenings.services

import java.time.{Instant, LocalDateTime, ZoneId, ZoneOffset}

import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.screenings.services.SearchScreeningsService.ScreeningData
import com.michal.cinema.util.DateTimeProvider
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
                               screeningRepository: ScreeningRepository,
                               dateTimeProvider: DateTimeProvider
                             ) {
  import dateTimeProvider._
  def search(from: LocalDateTime, to: LocalDateTime): Future[Seq[ScreeningData]] = {
    db.run(
      screeningRepository.findScreenings(localToInstant(from), localToInstant(to))
    )
  }
}
