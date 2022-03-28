package com.michal.cinema.util

object ErrorMessage {
  val NotFound = ErrorMessage("The requested resource was not found")
  val ReservationUnavailable = ErrorMessage("Reservation cannot be made for this screening")
  val RequestInvalid = ErrorMessage("Request is not valid")
}

case class ErrorMessage(message: String)