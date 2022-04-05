package com.michal.cinema.reservations.services

import com.michal.cinema.WithTestDb
import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationsDomain.PriceCategory
import com.michal.cinema.reservations.domain.{ReservationItemRepository, ReservationRepository}
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, NewSeatReservation}
import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.util.{CinemaError, DateTimeProvider, DateTimeProviderImpl}
import com.softwaremill.macwire.wire
import org.scalamock.scalatest.MockFactory
import slick.jdbc.H2Profile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class CreateReservationServiceTest extends WithTestDb with MockFactory {

  import com.michal.cinema.TestUtils._

  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val screeningRepository = mock[ScreeningRepository]
  val reservationRepository = mock[ReservationRepository]
  val screeningDetailsService = mock[ScreeningDetailsService]
  val reservationItemRepository = mock[ReservationItemRepository]
  val validateReservationCreationService = mock[ValidateReservationCreationService]
  val dateTimeProvider: DateTimeProvider = new DateTimeProviderImpl
  val reservationConfig = ReservationConfig(
    childPrice = BigDecimal(10),
    studentPrice = BigDecimal(15),
    adultPrice = BigDecimal(20),
    reservationToScreeningMinInterval = java.time.Duration.ofMinutes(15),
    reservationToExpirationInterval = java.time.Duration.ofMinutes(15),
    reservationConfirmationBaseUrl = "url"
  )
  val service = wire[CreateReservationService]

  "CreateReservationService" should "return ok for valid data" in {
    val screeningId = ScreeningId.generate()
    val request = CreateReservationRequest(
      "Thomas", "Anderson", screeningId, List(NewSeatReservation(1, 1, PriceCategory.Adult))
    )

    (screeningRepository.findById _).expects(screeningId).returning(DBIO.successful(Some(generateScreening(screeningId)))).once()
    (screeningDetailsService.getDbio _).expects(screeningId).returning(DBIO.successful(Some(generateScreeningDetails(screeningId)))).once()
    (validateReservationCreationService.validate _).expects(request, *, *).returning(Right()).once()

    val reservation = generateReservation()
    (reservationRepository.insert _).expects(*).returning(DBIO.successful(reservation)).once()
    (reservationItemRepository.insert _).expects(*).returning(DBIO.successful(generateReservationItem(reservationConfig))).once()

    val result = Await.result(service.create(request), Duration.Inf)
    result.isRight shouldBe true
  }

  it should "return Error for invalid data" in {
    val screeningId = ScreeningId.generate()
    val request = CreateReservationRequest(
      "Thomas", "Anderson", screeningId, List(NewSeatReservation(1, 1, PriceCategory.Adult))
    )

    (screeningRepository.findById _).expects(screeningId).returning(DBIO.successful(Some(generateScreening(screeningId)))).once()
    (screeningDetailsService.getDbio _).expects(screeningId).returning(DBIO.successful(Some(generateScreeningDetails(screeningId)))).once()
    (validateReservationCreationService.validate _).expects(request, *, *).returning(Left(CinemaError.RequestInvalid)).once()

    val result = Await.result(service.create(request), Duration.Inf)
    result.isLeft shouldBe true
  }


}
