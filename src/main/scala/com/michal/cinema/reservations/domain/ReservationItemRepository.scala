package com.michal.cinema.reservations.domain

import com.michal.cinema.util.CustomMappers._
import com.michal.cinema.screenings.domain.Domain.{ReservationItem, ReservationStatus, ScreeningId}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ReservationItemRepository(implicit ec: ExecutionContext) {

  def insert(reservationItem: ReservationItem): DBIO[ReservationItem] = {
    (ReservationItems.query.returning(ReservationItems.query)) += reservationItem
  }


  def findByScreeningId(screeningId: ScreeningId): DBIO[Seq[ReservationItem]] = {
    Reservations.query
      .joinLeft(ReservationItems.query).on(_.id === _.id)
      .filter { case (reservation, item) =>
        reservation.screeningId === screeningId && reservation.reservationStatus.inSet(Seq(ReservationStatus.Created, ReservationStatus.Paid))
      }.map { case (reservation, item) => item }
      .result.map(_.flatten)
  }

}
