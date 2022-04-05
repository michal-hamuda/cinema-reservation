package com.michal.cinema.reservations.services

import java.time.temporal.ChronoUnit

import com.michal.cinema.WithTestDb
import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationRepository
import com.michal.cinema.reservations.domain.ReservationsDomain._
import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.util.{DateTimeProvider, DateTimeProviderImpl}
import com.softwaremill.macwire.wire
import org.scalamock.scalatest.MockFactory
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class ConfirmReservationServiceTest extends WithTestDb with MockFactory {

  import com.michal.cinema.TestUtils._

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val screeningRepository = mock[ScreeningRepository]
  val reservationRepository = mock[ReservationRepository]

  val dateTimeProvider: DateTimeProvider = new DateTimeProviderImpl
  val reservationConfig = ReservationConfig(
    childPrice = BigDecimal(10),
    studentPrice = BigDecimal(15),
    adultPrice = BigDecimal(20),
    reservationToScreeningMinInterval = java.time.Duration.ofMinutes(15),
    reservationToExpirationInterval = java.time.Duration.ofMinutes(15),
    reservationConfirmationBaseUrl = "url"
  )
  val service = wire[ConfirmReservationService]

  "ConfirmReservationService" should "return ok for valid data" in {
    val confirmationId = ReservationConfirmationId.generate()
    val reservation = generateReservation().copy(status = ReservationStatus.Pending)
    val screeningId = reservation.screeningId
    val screening = generateScreening(screeningId).copy(startingAt = reservation.createdAt.plus(1, ChronoUnit.HOURS))

    (reservationRepository.findByConfirmationId _).expects(confirmationId).returning(DBIO.successful(Some(reservation))).once()
    (screeningRepository.findById _).expects(screeningId).returning(DBIO.successful(Some(screening))).once()
    (reservationRepository.update _).expects(reservation.copy(status = ReservationStatus.Confirmed)).returning(DBIO.successful(1)).once()


    val result = Await.result(service.confirm(confirmationId), Duration.Inf)
    result.isRight shouldBe true
  }

  it should "return error if reservation too close to screening" in {
    val confirmationId = ReservationConfirmationId.generate()
    val reservation = generateReservation().copy(status = ReservationStatus.Pending)
    val screeningId = reservation.screeningId
    val screening = generateScreening(screeningId).copy(startingAt = reservation.createdAt.plus(10, ChronoUnit.MINUTES))

    (reservationRepository.findByConfirmationId _).expects(confirmationId).returning(DBIO.successful(Some(reservation))).once()
    (screeningRepository.findById _).expects(screeningId).returning(DBIO.successful(Some(screening))).once()

    val result = Await.result(service.confirm(confirmationId), Duration.Inf)
    result.isLeft shouldBe true
  }

  it should "return error if reservation is not pending" in {
    val confirmationId = ReservationConfirmationId.generate()
    val reservation = generateReservation().copy(status = ReservationStatus.Expired)
    val screeningId = reservation.screeningId
    val screening = generateScreening(screeningId).copy(startingAt = reservation.createdAt.plus(1, ChronoUnit.HOURS))

    (reservationRepository.findByConfirmationId _).expects(confirmationId).returning(DBIO.successful(Some(reservation))).once()
    (screeningRepository.findById _).expects(screeningId).returning(DBIO.successful(Some(screening))).once()

    val result = Await.result(service.confirm(confirmationId), Duration.Inf)
    result.isLeft shouldBe true
  }


}
