package com.michal.cinema.reservations.domain

import java.time.Instant
import java.util.UUID

import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId

object ReservationsDomain {

  object ReservationId {
    def generate() = ReservationId(UUID.randomUUID())
  }

  case class ReservationId(id: UUID)

  object ReservationConfirmationId {
    def generate() = ReservationConfirmationId(UUID.randomUUID())
  }

  case class ReservationConfirmationId(id: UUID)

  object ReservationStatus extends Enumeration {
    val Pending, Confirmed, Paid, Expired = Value
  }

  case class Reservation(
                          id: ReservationId,
                          userFirstName: String,
                          userLastName: String,
                          screeningId: ScreeningId,
                          status: ReservationStatus.Value,
                          confirmationId: ReservationConfirmationId,
                          createdAt: Instant
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
