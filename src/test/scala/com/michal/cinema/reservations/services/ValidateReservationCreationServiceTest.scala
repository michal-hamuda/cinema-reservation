package com.michal.cinema.reservations.services

import java.time.temporal.ChronoUnit
import java.time.{Duration, Instant}

import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationsDomain.PriceCategory
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, NewSeatReservation}
import com.michal.cinema.reservations.services.ScreeningDetailsService.{ScreeningDetails, SeatInfo, SeatStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.{MovieId, RoomId, Screening, ScreeningId}
import com.michal.cinema.util.DateTimeProviderImpl
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ValidateReservationCreationServiceTest extends AnyFlatSpec with Matchers {

  val reservationConfig = ReservationConfig(
    childPrice = BigDecimal(10),
    studentPrice = BigDecimal(15),
    adultPrice = BigDecimal(20),
    reservationToScreeningMinInterval = Duration.ofMinutes(15),
    reservationToExpirationInterval = Duration.ofMinutes(15),
    reservationConfirmationBaseUrl = "url"
  )
  val dateTimeProvider = new DateTimeProviderImpl
  val service = new ValidateReservationCreationService(reservationConfig, dateTimeProvider)

  "ValidateReservationCreationService" should "return ok for valid data" in {
    val screeningId = ScreeningId.generate()
    val movieId = MovieId.generate()
    val roomId = RoomId.generate()
    val screening = Screening(screeningId, movieId, roomId, Instant.now().plus(2, ChronoUnit.HOURS))
    val screeningDetails = ScreeningDetails(screeningId, 2, 2, Seq(
      SeatInfo(0,0, SeatStatus.Free),
      SeatInfo(0,1, SeatStatus.Free),
      SeatInfo(1,0, SeatStatus.Free),
      SeatInfo(1,1, SeatStatus.Free),
    ))

    val request = CreateReservationRequest(
      userFirstName = "Thomas",
      userLastName = "Anderson-Żabiański",
      screeningId = screeningId,
      seats = List(
        NewSeatReservation(
          row = 1,
          column = 1,
          priceCategory = PriceCategory.Adult
        )
      )
    )

    val result = service.validate(request, screening, screeningDetails)
    result.isRight shouldBe true
  }

  it should "return error for invalid user name" in {
    val screeningId = ScreeningId.generate()
    val movieId = MovieId.generate()
    val roomId = RoomId.generate()
    val screening = Screening(screeningId, movieId, roomId, Instant.now().plus(2, ChronoUnit.HOURS))
    val screeningDetails = ScreeningDetails(screeningId, 2, 2, Seq(
      SeatInfo(0,0, SeatStatus.Free),
      SeatInfo(0,1, SeatStatus.Free),
      SeatInfo(1,0, SeatStatus.Free),
      SeatInfo(1,1, SeatStatus.Free),
    ))

    val request = CreateReservationRequest(
      userFirstName = "Th",
      userLastName = "Anderson-Żabiański",
      screeningId = screeningId,
      seats = List(
        NewSeatReservation(
          row = 1,
          column = 1,
          priceCategory = PriceCategory.Adult
        )
      )
    )

    val result = service.validate(request, screening, screeningDetails)
    result.isLeft shouldBe true
  }

  it should "return error for screening that happens too soon" in {
    val screeningId = ScreeningId.generate()
    val movieId = MovieId.generate()
    val roomId = RoomId.generate()
    val screening = Screening(screeningId, movieId, roomId, Instant.now().plus(5, ChronoUnit.MINUTES))
    val screeningDetails = ScreeningDetails(screeningId, 2, 2, Seq(
      SeatInfo(0,0, SeatStatus.Free),
      SeatInfo(0,1, SeatStatus.Free),
      SeatInfo(1,0, SeatStatus.Free),
      SeatInfo(1,1, SeatStatus.Free),
    ))

    val request = CreateReservationRequest(
      userFirstName = "Thomas",
      userLastName = "Anderson-Żabiański",
      screeningId = screeningId,
      seats = List(
        NewSeatReservation(
          row = 1,
          column = 1,
          priceCategory = PriceCategory.Adult
        )
      )
    )

    val result = service.validate(request, screening, screeningDetails)
    result.isLeft shouldBe true
  }

  it should "return error for already taken place" in {
    val screeningId = ScreeningId.generate()
    val movieId = MovieId.generate()
    val roomId = RoomId.generate()
    val screening = Screening(screeningId, movieId, roomId, Instant.now().plus(2, ChronoUnit.HOURS))
    val screeningDetails = ScreeningDetails(screeningId, 2, 2, Seq(
      SeatInfo(0,0, SeatStatus.Free),
      SeatInfo(0,1, SeatStatus.Free),
      SeatInfo(1,0, SeatStatus.Free),
      SeatInfo(1,1, SeatStatus.Taken),
    ))

    val request = CreateReservationRequest(
      userFirstName = "Thomas",
      userLastName = "Anderson-Żabiański",
      screeningId = screeningId,
      seats = List(
        NewSeatReservation(
          row = 1,
          column = 1,
          priceCategory = PriceCategory.Adult
        )
      )
    )

    val result = service.validate(request, screening, screeningDetails)
    result.isLeft shouldBe true
  }

  it should "return error for reservation leaving one free spot between two taken ones" in {
    val screeningId = ScreeningId.generate()
    val movieId = MovieId.generate()
    val roomId = RoomId.generate()
    val screening = Screening(screeningId, movieId, roomId, Instant.now().plus(2, ChronoUnit.HOURS))
    val screeningDetails = ScreeningDetails(screeningId, 3, 3, Seq(
      SeatInfo(0,0, SeatStatus.Free),
      SeatInfo(0,1, SeatStatus.Free),
      SeatInfo(0,2, SeatStatus.Taken),
      SeatInfo(1,0, SeatStatus.Free),
      SeatInfo(1,1, SeatStatus.Free),
      SeatInfo(1,2, SeatStatus.Free),
      SeatInfo(2,0, SeatStatus.Free),
      SeatInfo(2,1, SeatStatus.Free),
      SeatInfo(2,2, SeatStatus.Free)
    ))

    val request = CreateReservationRequest(
      userFirstName = "Thomas",
      userLastName = "Anderson-Żabiański",
      screeningId = screeningId,
      seats = List(
        NewSeatReservation(
          row = 0,
          column = 0,
          priceCategory = PriceCategory.Adult
        )
      )
    )

    val result = service.validate(request, screening, screeningDetails)
    result.isLeft shouldBe true
  }



}
