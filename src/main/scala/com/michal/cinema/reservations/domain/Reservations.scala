package com.michal.cinema.reservations.domain

import java.sql.Timestamp

import com.michal.cinema.reservations.domain.ReservationsDomain.{Reservation, ReservationConfirmationId, ReservationId, ReservationStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {

  def id = column[ReservationId]("id", O.PrimaryKey)

  def userFirstName = column[String]("user_first_name")

  def userLastName = column[String]("user_last_name")

  def screeningId = column[ScreeningId]("screening_id")

  def status = column[ReservationStatus.Value]("status")

  //separate id for confirmation links - we don't want the 'real' database id to be too visible to the user
  def confirmationId = column[ReservationConfirmationId]("confirmation_id")

  def createdAt = column[Timestamp]("created_at")

  def createdAtInstant = (createdAt) <> (timestampToInstant _, instantToTimestamp _)

  override def * = (id, userFirstName, userLastName, screeningId, status, confirmationId, createdAtInstant) <> (Reservation.tupled, Reservation.unapply)
}

object Reservations {
  val query = TableQuery[Reservations]
}




