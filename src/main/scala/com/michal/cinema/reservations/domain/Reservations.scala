package com.michal.cinema.reservations.domain

import java.time.Instant

import com.michal.cinema.reservations.domain.ReservationsDomain.{Reservation, ReservationConfirmationId, ReservationId, ReservationStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {

  def id = column[ReservationId]("id", O.AutoInc, O.PrimaryKey)

  def userFirstName = column[String]("user_first_name")

  def userLastName = column[String]("user_last_name")

  def screeningId = column[ScreeningId]("screening_id")

  def status = column[ReservationStatus.Value]("status")

  //separate id for confirmation links - we don't want the 'real' database id to be too visible to the user
  def confirmationId = column[ReservationConfirmationId]("confirmation_id")

  def createdAt = column[Instant]("created_at")

  override def * = (id, userFirstName, userLastName, screeningId, status, confirmationId, createdAt) <> (Reservation.tupled, Reservation.unapply)
}

object Reservations {
  val query = TableQuery[Reservations]
}




