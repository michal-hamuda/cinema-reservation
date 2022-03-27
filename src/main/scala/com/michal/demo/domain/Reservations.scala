package com.michal.demo.domain

import com.michal.demo.domain.CustomMappers._
import com.michal.demo.domain.Domain.{Reservation, ReservationId, ReservationStatus, ScreeningId}
import slick.jdbc.H2Profile.api._

class Reservations(tag: Tag) extends Table[Reservation](tag, "reservations") {

  def id = column[ReservationId]("id", O.AutoInc, O.PrimaryKey)

  def userFirstName = column[String]("userFirstName")

  def userLastName = column[String]("userLastName")

  def screeningId = column[ScreeningId]("screeningId")

  def reservationStatus = column[ReservationStatus.Value]("reservationStatus")

  override def * = (id, userFirstName, userLastName, screeningId, reservationStatus) <> (Reservation.tupled, Reservation.unapply)
}

object Reservations {
  val query = TableQuery[Reservations]
}




