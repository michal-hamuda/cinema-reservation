package com.michal.cinema.reservations.domain

import com.michal.cinema.screenings.domain.Domain.Reservation
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ReservationRepository(implicit ec: ExecutionContext) {

  def insert(reservation: Reservation): DBIO[Reservation] = {
    (Reservations.query.returning(Reservations.query)) += reservation
  }

}
