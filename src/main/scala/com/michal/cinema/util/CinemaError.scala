package com.michal.cinema.util

object CinemaError {
  val NotFound = CinemaError("The requested resource was not found")
  val ReservationUnavailable = CinemaError("Reservation cannot be made for this screening")
  val RequestInvalid = CinemaError("Request is not valid")
}

case class CinemaError(message: String)