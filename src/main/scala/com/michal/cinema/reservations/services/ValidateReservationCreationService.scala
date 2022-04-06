package com.michal.cinema.reservations.services

import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, NewSeatReservation}
import com.michal.cinema.reservations.services.ScreeningDetailsService.{ScreeningDetails, SeatStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.Screening
import com.michal.cinema.util.{DateTimeProvider, CinemaError}

class ValidateReservationCreationService(
                                          reservationConfig: ReservationConfig,
                                          dateTimeProvider: DateTimeProvider
                                        ) {

  def validate(request: CreateReservationRequest, screening: Screening, screeningDetails: ScreeningDetails): Either[CinemaError, Unit] = {
    for {
      _ <- validateReservationNotTooLate(request, screening)
      _ <- validateName(request)
      _ <- validateSeats(request, screeningDetails)
    } yield ()
  }

  private def validateReservationNotTooLate(request: CreateReservationRequest, screening: Screening): Either[CinemaError, Unit] = {
    val isTooLate = dateTimeProvider.currentInstant().plus(reservationConfig.reservationToScreeningMinInterval).isBefore(screening.startingAt)
    Either.cond(isTooLate, (), CinemaError.ReservationUnavailable)
  }


  private def validateName(request: CreateReservationRequest): Either[CinemaError, Unit] = {
    Either.cond(
      isValidFirstName(request.userFirstName) && isValidLastName(request.userLastName),
      (),
      CinemaError.RequestInvalid
    )
  }

  private def isValidFirstName(name: String) = {
    // chosen to use direct methods on string like .isLetter
    // instead of regexes because of requirement to handle Unicode (polish) characters
    name.length >= 3 &&
      name.forall(_.isLetter) &&
      name.head.isUpper
  }

  private def isValidLastName(name: String) = {
    val isCorrectLength = name.length >= 3
    val parts = name.split("-")
    isCorrectLength && parts.length <= 2 && parts.forall { part =>
      part.forall(_.isLetter) && part.head.isUpper
    }
  }

  private def validateSeats(request: CreateReservationRequest, screeningDetails: ScreeningDetails): Either[CinemaError, Unit] = {
    val isValid = request.seats.nonEmpty &&
      request.seats.forall(validateSeatIsAvailable(_, screeningDetails)) &&
      validateNoLoneSeatLeft(request, screeningDetails)

    Either.cond(isValid, (), CinemaError.RequestInvalid)
  }

  private def validateNoLoneSeatLeft(request: CreateReservationRequest, screeningDetails: ScreeningDetails) = {
    request.seats.forall { newReservedSeat =>
      val leftNeighbour = Some(newReservedSeat.copy(row = newReservedSeat.row - 1)).filter(_.row > 0)
      val rightNeighbour = Some(newReservedSeat.copy(row = newReservedSeat.row + 1)).filter(_.row < screeningDetails.totalRows)
      (leftNeighbour ++ rightNeighbour).forall {
        validateSeatIsTaken(_, request, screeningDetails)
      }
    }
  }

  private def validateSeatIsTaken(newSeat: NewSeatReservation, request: CreateReservationRequest, screeningDetails: ScreeningDetails) = {
    val seatReservedPreviously = screeningDetails.seats.exists(seat => seat.row == newSeat.row &&
      seat.column == newSeat.column &&
      seat.status == SeatStatus.Taken)
    val seatReservedNow = request.seats.exists(seat => newSeat.row == seat.row &&
      seat.column == newSeat.column)
    seatReservedPreviously || seatReservedNow
  }

  private def validateSeatIsAvailable(newReservedSeat: NewSeatReservation, screeningDetails: ScreeningDetails) = {
    screeningDetails.seats.exists(seat => seat.row == newReservedSeat.row && seat.column == newReservedSeat.column && seat.status == SeatStatus.Free)
  }
}
