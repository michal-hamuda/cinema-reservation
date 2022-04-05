package com.michal.cinema.reservations.services

import cats.data.EitherT
import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationRepository
import com.michal.cinema.reservations.domain.ReservationsDomain.{Reservation, ReservationConfirmationId, ReservationStatus}
import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.domain.ScreeningsDomain.Screening
import com.michal.cinema.util.{DateTimeProvider, CinemaError}
import com.rms.miu.slickcats.DBIOInstances._
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}


class ConfirmReservationService(
                                 db: Database,
                                 reservationConfig: ReservationConfig,
                                 dateTimeProvider: DateTimeProvider,
                                 reservationRepository: ReservationRepository,
                                 screeningRepository: ScreeningRepository
                               )(implicit ec: ExecutionContext) {

  def confirm(confirmationId: ReservationConfirmationId): Future[Either[CinemaError, Unit]] = {
    db.run((for {
      reservation <- EitherT.fromOptionF(reservationRepository.findByConfirmationId(confirmationId), CinemaError.NotFound)
      screening <- EitherT.fromOptionF(screeningRepository.findById(reservation.screeningId), CinemaError.NotFound)
      _ <- validate(reservation, screening)
      updatedReservation = reservation.copy(status = ReservationStatus.Confirmed)
      _ <- EitherT.right[CinemaError](reservationRepository.update(updatedReservation))
    } yield ()).value)
  }

  private def validate(reservation: Reservation, screening: Screening): EitherT[DBIO, CinemaError, Unit] = {
    val now = dateTimeProvider.currentInstant()
    val isPending = reservation.status == ReservationStatus.Pending
    val notExpired = reservation.createdAt.plus(reservationConfig.reservationToExpirationInterval).isAfter(now)
    val screeningNotClosed = now.plus(reservationConfig.reservationToScreeningMinInterval).isBefore(screening.startingAt)
    EitherT.cond[DBIO](isPending && notExpired && screeningNotClosed, (), CinemaError.RequestInvalid)
  }


}
