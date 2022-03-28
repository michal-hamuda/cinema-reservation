package com.michal.cinema.reservations.services

import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, NewSeatReservation}
import com.michal.cinema.reservations.services.ScreeningDetailsService.{ScreeningDetails, SeatStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.Screening
import com.michal.cinema.util.{DateTimeProvider, ErrorMessage}

class ValidateReservationCreationService(
                                          reservationConfig: ReservationConfig,
                                          dateTimeProvider: DateTimeProvider
                                        ) {

  def validate(request: CreateReservationRequest, screening: Screening, screeningDetails: ScreeningDetails): Either[ErrorMessage, Unit] = {
    for {
      _ <- validateReservationNotTooLate(request, screening)
      _ <- validateName(request)
      _ <- validateSeats(request, screeningDetails)
    } yield ()
  }

  private def validateReservationNotTooLate(request: CreateReservationRequest, screening: Screening): Either[ErrorMessage, Unit] = {
    val isTooLate = dateTimeProvider.currentInstant().plus(reservationConfig.reservationToScreeningMinInterval).isBefore(screening.start)
    Either.cond(isTooLate, (), ErrorMessage.ReservationUnavailable)
  }


  private def validateName(request: CreateReservationRequest): Either[ErrorMessage, Unit] = {
    Either.cond(
      isValidFirstName(request.userFirstName) && isValidLastName(request.userLastName),
      (),
      ErrorMessage.RequestInvalid
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

  private def validateSeats(request: CreateReservationRequest, screeningDetails: ScreeningDetails): Either[ErrorMessage, Unit] = {
    val isValid = request.seats.nonEmpty &&
      request.seats.forall(validateSeatIsAvailable(_, screeningDetails)) &&
      validateNoLoneSeatLeft(request, screeningDetails)

    Either.cond(isValid, (), ErrorMessage.RequestInvalid)
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

  private def validateSeatIsTaken(seat: NewSeatReservation, request: CreateReservationRequest, screeningDetails: ScreeningDetails) = {
    val seatReservedPreviously = screeningDetails.seats.exists(seat => seat.row == seat.row &&
      seat.column == seat.column &&
      seat.status == SeatStatus.Taken)
    val seatReservedNow = request.seats.exists(seat => seat.row == seat.row &&
      seat.column == seat.column)
    seatReservedPreviously || seatReservedNow
  }

  private def validateSeatIsAvailable(newReservedSeat: NewSeatReservation, screeningDetails: ScreeningDetails) = {
    screeningDetails.seats.exists(seat => seat.row == newReservedSeat.row && seat.column == seat.column && seat.status == SeatStatus.Free)
  }
}
