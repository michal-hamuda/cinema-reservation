package com.michal.demo.domain

import java.time.Instant
import java.util.UUID

object Domain {

  case class MovieId(id: UUID)

  case class Movie(id: MovieId, title: String, durationMinutes: Int)

  case class RoomId(id: UUID)

  case class Room(id: RoomId, rows: Int, columns: Int)

  case class ScreeningId(id: UUID)

  case class Screening(id: ScreeningId, movieId: MovieId, roomId: RoomId, start: Instant)

  object ReservationId {
    def generate() = ReservationId(UUID.randomUUID())
  }

  case class ReservationId(id: UUID)

  object ReservationStatus extends Enumeration {
    val Created, Paid, Expired = Value
  }

  case class Reservation(
                          id: ReservationId,
                          userFirstName: String,
                          userLastName: String,
                          screeningId: ScreeningId,
                          reservationStatus: ReservationStatus.Value
                        )

  object PriceCategory extends Enumeration {
    val Adult, Student, Child = Value
  }

  case class ReservationItem(
                              reservationId: ReservationId,
                              row: Int,
                              column: Int,
                              priceCategory: PriceCategory.Value,
                              price: BigDecimal
                            )

}
