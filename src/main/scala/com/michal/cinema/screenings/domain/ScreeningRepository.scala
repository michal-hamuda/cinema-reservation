package com.michal.cinema.screenings.domain

import java.time.{Instant, LocalDateTime, ZoneId}

import com.michal.cinema.util.CustomMappers._
import com.michal.cinema.screenings.domain.Domain.{Room, Screening, ScreeningId}
import com.michal.cinema.screenings.services.ScreeningData
import slick.dbio.DBIO
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
      .filter { case (screening, movie) => screening.start > from && screening.start < to }
      .sortBy { case (screening, movie) => movie.map(_.title) }
      .sortBy { case (screening, movie) => screening.start }
      .result.map(_.map {
      case (screening, movie) => ScreeningData(
        screening.id,
        movie.map(_.title).getOrElse(""),
        LocalDateTime.ofInstant(screening.start, ZoneId.systemDefault()), //date conversions
        movie.map(_.durationMinutes).getOrElse(0)
      )
    }

    )
  }

}
