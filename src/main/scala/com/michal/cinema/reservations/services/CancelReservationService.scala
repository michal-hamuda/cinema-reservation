package com.michal.cinema.reservations.services

import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationRepository
import com.michal.cinema.reservations.domain.ReservationsDomain.ReservationStatus
import com.michal.cinema.util.{DateTimeProvider, RunnableJob}
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

class CancelReservationService(
                                db: Database,
                                reservationRepository: ReservationRepository,
                                dateTimeProvider: DateTimeProvider,
                                reservationConfig: ReservationConfig
                              )(implicit ec: ExecutionContext) extends RunnableJob {

  def run(): Future[Unit] = {
    val now = dateTimeProvider.currentInstant()
    val dbio = for {
      _ <- reservationRepository.updateStatusByCreationTime(
        initialStatus = ReservationStatus.Pending,
        targetStatus = ReservationStatus.Expired,
        createdBefore = now.minus(reservationConfig.reservationToExpirationInterval)
      )
      _ <- reservationRepository.updateStatusByScreeningTime(
        initialStatus = ReservationStatus.Pending,
        targetStatus = ReservationStatus.Expired,
        screeningBeforeThan = now.plus(reservationConfig.reservationToScreeningMinInterval)
      )
    } yield ()
    db.run(dbio)
  }

}
