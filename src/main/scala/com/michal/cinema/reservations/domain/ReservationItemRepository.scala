package com.michal.cinema.reservations.domain

import com.michal.cinema.reservations.domain.ReservationsDomain.{ReservationItem, ReservationStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.util.CustomMappers._
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ReservationItemRepository(implicit ec: ExecutionContext) {

  def insert(reservationItem: ReservationItem): DBIO[ReservationItem] = {
    (ReservationItems.query += reservationItem).map(_ => reservationItem)
  }


  def findActiveByScreeningId(screeningId: ScreeningId): DBIO[Seq[ReservationItem]] = {
    val activeStates = Seq(ReservationStatus.Pending, ReservationStatus.Confirmed, ReservationStatus.Paid)
    Reservations.query
      .joinLeft(ReservationItems.query).on(_.id === _.reservationId)
      .filter { case (reservation, item) =>
        reservation.screeningId === screeningId && reservation.status.inSet(activeStates)
      }.map { case (reservation, item) => item }
      .result.map(_.flatten)
  }

}
