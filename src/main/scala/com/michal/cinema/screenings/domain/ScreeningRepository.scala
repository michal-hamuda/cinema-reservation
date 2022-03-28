package com.michal.cinema.screenings.domain

import java.time.{Instant, LocalDateTime, ZoneId}

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Room, Screening, ScreeningId}
import com.michal.cinema.screenings.services.SearchScreeningsService.ScreeningData
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ScreeningRepository(implicit ec: ExecutionContext) {

  def insert(userRow: Room): DBIO[Int] = {
    Rooms.query.insertOrUpdate(userRow)
  }

  def findById(screeningId: ScreeningId): DBIO[Option[Screening]] = {
    Screenings.query.filter(_.id === screeningId).result.headOption
  }

  def findScreenings(from: Instant, to: Instant): DBIO[Seq[ScreeningData]] = {
    Screenings.query
      .joinLeft(Movies.query).on(_.movieId === _.id)
      .filter { case (screening, movie) => screening.startingAt > from && screening.startingAt < to }
      .sortBy { case (screening, movie) => movie.map(_.title) }
      .sortBy { case (screening, movie) => screening.startingAt }
      .result.map(_.map {
      case (screening, movie) => ScreeningData(
        screening.id,
        movie.map(_.title).getOrElse(""),
        LocalDateTime.ofInstant(screening.start, ZoneId.systemDefault()),
        movie.map(_.durationMinutes).getOrElse(0)
      )
    }

    )
  }

}
