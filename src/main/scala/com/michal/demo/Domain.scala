package com.michal.demo

import java.time.Instant

object Domain {
  case class MovieId(id: Long)
  case class Movie(id: MovieId, title: String, durationMinutes: Int)

  case class RoomId(id: Long)
  case class Room(id: RoomId, rows: Int, columns: Int)

  case class ScreeningId(id: Long)
  case class Screening(id: ScreeningId, movieId: MovieId, roomId: RoomId, start: Instant)

  case class ReservationId(id: Long)
  object ReservationStatus extends Enumeration {
    val Created, Paid, Expired = Value
  }
  case class Reservation(id: ReservationId, userFirstName: String, userLastName: String, screeningId: ScreeningId, reservationStatus: ReservationStatus.Value)

  object PriceCategory extends Enumeration {
    val Adult, Student, Child = Value
  }
  case class ReservationItem(reservationId: ReservationId, row: Int, column: Int, priceCategory: String)
}
