package com.michal.cinema.reservations.services

import java.time.Instant

import cats.data.EitherT
import cats.implicits._
import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.{ReservationItemRepository, ReservationRepository}
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, CreateReservationResponse, ErrorMessage}
import com.michal.cinema.screenings.domain.Domain._
import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.services.{ScreeningDetails, ScreeningDetailsService}
import com.michal.cinema.util.DateTimeProvider
import com.rms.miu.slickcats.DBIOInstances._
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

object CreateReservationService {

  case class CreateReservationRequest(
                                       userFirstName: String,
                                       userLastName: String,
                                       screeningId: ScreeningId,
                                       seats: List[NewSeatReservation]
                                     )

  case class NewSeatReservation(row: Int, column: Int, priceCategory: PriceCategory.Value)

  case class CreateReservationResponse(
                                        totalPrice: BigDecimal,
                                        expirationTime: Instant
                                      )

  object ErrorMessage {
    val NotFound = ErrorMessage("The requested resource was not found")
    val ReservationUnavailable = ErrorMessage("Reservation cannot be made for this screening")
    val RequestInvalid = ErrorMessage("The reservation request is not valid")
  }

  case class ErrorMessage(message: String)

}

class CreateReservationService(
                                db: Database,
                                screeningRepository: ScreeningRepository,
                                reservationRepository: ReservationRepository,
                                reservationItemRepository: ReservationItemRepository,
                                screeningDetailsService: ScreeningDetailsService,
                                validateReservationCreationService: ValidateReservationCreationService,
                                priceConfig: ReservationConfig,
                                dateTimeProvider: DateTimeProvider
                              )(implicit ec: ExecutionContext) {
  def create(request: CreateReservationRequest): Future[Either[ErrorMessage, CreateReservationResponse]] = {
    db.run((for {
      screening <- findScreening(request)
      screeningDetails <- findScreeningDetails(screening.id)
      _ <- EitherT.fromEither[DBIO](validateReservationCreationService.validate(request, screening, screeningDetails))
      reservation <- insertReservation(request)
      items = createReservationItems(request, reservation.id)
      _ <- insertReservationItems(items)
      response = createResponse(items)
    } yield response).value.transactionally)

  }

  private def findScreening(request: CreateReservationRequest): EitherT[DBIO, ErrorMessage, Screening] = {
    val dbio: DBIO[Either[ErrorMessage, Screening]] = screeningRepository
      .findById(request.screeningId)
      .map(_.toRight(ErrorMessage.NotFound))
    EitherT(dbio)
  }

  private def findScreeningDetails(screeningId: ScreeningId): EitherT[DBIO, ErrorMessage, ScreeningDetails] = {
    val dbio: DBIO[Either[ErrorMessage, ScreeningDetails]] = screeningDetailsService.getDbio(screeningId)
      .map(_.toRight(ErrorMessage.NotFound))
    EitherT(dbio)
  }


  private def insertReservation(request: CreateReservationRequest): EitherT[DBIO, ErrorMessage, Reservation] = {
    val reservation = Reservation(
      ReservationId.generate(),
      request.userFirstName,
      request.userLastName,
      request.screeningId,
      ReservationStatus.Created
    )
    EitherT.right[ErrorMessage](reservationRepository.insert(reservation))
  }

  private def createReservationItems(request: CreateReservationRequest, reservationId: ReservationId): Seq[ReservationItem] = {
    request.seats.map(seat =>
      ReservationItem(reservationId, seat.row, seat.column, seat.priceCategory, getPrice(seat.priceCategory))
    )
  }

  private def insertReservationItems(items: Seq[ReservationItem]): EitherT[DBIO, ErrorMessage, Seq[ReservationItem]] = {
    EitherT.right[ErrorMessage](
      items.traverse(reservationItemRepository.insert)
    )
  }

  private def getPrice(priceCategory: PriceCategory.Value) = {
    priceCategory match {
      case PriceCategory.Adult =>
        priceConfig.adultPrice
      case PriceCategory.Student =>
        priceConfig.studentPrice
      case PriceCategory.Child =>
        priceConfig.childPrice
    }
  }

  private def createResponse(items: Seq[ReservationItem]) = {
    val totalPrice = items.map(_.price).sum
    val expirationTime = dateTimeProvider.currentInstant().plus(priceConfig.expirationThreshold)
    CreateReservationResponse(totalPrice, expirationTime)
  }
}
