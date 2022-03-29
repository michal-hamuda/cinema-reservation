package com.michal.cinema.reservations.domain

import java.time.Instant

import com.michal.cinema.reservations.domain.ReservationsDomain.{Reservation, ReservationConfirmationId, ReservationStatus}
import com.michal.cinema.screenings.domain.Screenings
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class ReservationRepository(implicit ec: ExecutionContext) {

  def insert(reservation: Reservation): DBIO[Reservation] = {
    (Reservations.query += reservation).map(_ => reservation)
  }

  def findAll(): DBIO[Seq[Reservation]] = {
    Reservations.query.result
  }

  def findByConfirmationId(confirmationId: ReservationConfirmationId): DBIO[Option[Reservation]] = {
    Reservations.query.filter(_.confirmationId === confirmationId).result.headOption
  }

  def update(reservation: Reservation): DBIO[Int] = {
    Reservations.query.filter(_.id === reservation.id).update(reservation)
  }

  def updateStatusByCreationTime(
                                  initialStatus: ReservationStatus.Value,
                                  targetStatus: ReservationStatus.Value,
                                  createdBefore: Instant
                                ): DBIO[Int] = {
    Reservations.query
      .filter { reservation => reservation.status === initialStatus && reservation.createdAt < instantToTimestamp(createdBefore) }
      .map(_.status)
      .update(targetStatus)
  }

  def updateStatusByScreeningTime(
                                   initialStatus: ReservationStatus.Value,
                                   targetStatus: ReservationStatus.Value,
                                   screeningBeforeThan: Instant
                                 ): DBIO[Int] = {
    Reservations.query
      .join(Screenings.query).on(_.screeningId === _.id)
      .filter { case (reservation, screening) => reservation.status === initialStatus && screening.startingAt < instantToTimestamp(screeningBeforeThan) }
      .map(_._1.status)
      .update(targetStatus)

  }

}
