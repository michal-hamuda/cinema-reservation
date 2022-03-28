package com.michal.cinema.screenings.domain

import java.time.{Instant, LocalDateTime, ZoneId}

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Screening, ScreeningId}
import com.michal.cinema.screenings.services.SearchScreeningsService.ScreeningData
import com.michal.cinema.util.CustomMappers._
import com.michal.cinema.util.DateTimeProvider
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ScreeningRepository(dateTimeProvider: DateTimeProvider)(implicit ec: ExecutionContext) {

  def insert(screening: Screening): DBIO[Int] = {
    Screenings.query.insertOrUpdate(screening)
  }

  def findById(screeningId: ScreeningId): DBIO[Option[Screening]] = {
    Screenings.query.filter(_.id === screeningId).result.headOption
  }

  def findScreenings(from: Instant, to: Instant): DBIO[Seq[ScreeningData]] = {
    Screenings.query
      .joinLeft(Movies.query).on(_.movieId === _.id)
      .filter { case (screening, movie) => screening.startingAt > instantToTimestamp(from) && screening.startingAt < instantToTimestamp(to) }
      .sortBy { case (screening, movie) => screening.startingAt }
      .sortBy { case (screening, movie) => movie.map(_.title) }
      .result.map(_.map {
      case (screening, movie) => ScreeningData(
        screening.id,
        movie.map(_.title).getOrElse(""),
        dateTimeProvider.instantToLocal(screening.start),
        movie.map(_.durationMinutes).getOrElse(0)
      )
    }

    )
  }

}
