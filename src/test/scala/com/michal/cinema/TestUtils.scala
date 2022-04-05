package com.michal.cinema

import java.time.Instant

import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationsDomain._
import com.michal.cinema.reservations.services.ScreeningDetailsService.ScreeningDetails
import com.michal.cinema.screenings.domain.ScreeningsDomain.{Movie, MovieId, RoomId, Screening, ScreeningId}

import scala.util.Random

object TestUtils {

  def generateMovie(screeningId: ScreeningId = ScreeningId.generate()) = {
    Movie(MovieId.generate(), Random.nextString(5), 60 + Random.nextInt(60))
  }

  def generateScreening(screeningId: ScreeningId = ScreeningId.generate()) = {
    Screening(screeningId, MovieId.generate(), RoomId.generate(), Instant.now())
  }

  def generateScreeningDetails(screeningId: ScreeningId) = {
    ScreeningDetails(screeningId, 10, 10, Seq.empty)
  }

  def generateReservation() = {
    Reservation(ReservationId.generate(), "Aaa", "Bbb", ScreeningId.generate(), ReservationStatus.Pending, ReservationConfirmationId.generate(), Instant.now())
  }

  def generateReservationItem(config: ReservationConfig) = {
    ReservationItem(ReservationId.generate(), 1, 1, PriceCategory.Student, config.adultPrice)
  }


}
