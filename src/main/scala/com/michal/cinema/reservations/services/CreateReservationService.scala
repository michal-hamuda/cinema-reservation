package com.michal.cinema.reservations.services

import java.time.LocalDateTime

import cats.data.EitherT
import cats.implicits._
import com.michal.cinema.reservations.ReservationConfig
import com.michal.cinema.reservations.domain.ReservationsDomain._
import com.michal.cinema.reservations.domain.{ReservationItemRepository, ReservationRepository}
import com.michal.cinema.reservations.services.CreateReservationService.{CreateReservationRequest, CreateReservationResponse}
import com.michal.cinema.reservations.services.ScreeningDetailsService.ScreeningDetails
import com.michal.cinema.screenings.domain.ScreeningRepository
import com.michal.cinema.screenings.domain.ScreeningsDomain._
import com.michal.cinema.util.{DateTimeProvider, ErrorMessage}
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
                                        expirationTime: LocalDateTime,
                                        confirmationLink: String
                                      )

}

class CreateReservationService(
                                db: Database,
                                screeningRepository: ScreeningRepository,
                                reservationRepository: ReservationRepository,
                                reservationItemRepository: ReservationItemRepository,
                                screeningDetailsService: ScreeningDetailsService,
                                validateReservationCreationService: ValidateReservationCreationService,
                                reservationConfig: ReservationConfig,
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
      response = createResponse(items, reservation)
    } yield response).value.transactionally)
  }

  private def findScreening(request: CreateReservationRequest): EitherT[DBIO, ErrorMessage, Screening] = {
    EitherT.fromOptionF(screeningRepository.findById(request.screeningId), ErrorMessage.NotFound)
  }

  private def findScreeningDetails(screeningId: ScreeningId): EitherT[DBIO, ErrorMessage, ScreeningDetails] = {
    EitherT.fromOptionF(screeningDetailsService.getDbio(screeningId), ErrorMessage.NotFound)
  }

  private def insertReservation(request: CreateReservationRequest): EitherT[DBIO, ErrorMessage, Reservation] = {
    val reservation = Reservation(
      ReservationId.generate(),
      request.userFirstName,
      request.userLastName,
      request.screeningId,
      ReservationStatus.Pending,
      ReservationConfirmationId.generate(),
      dateTimeProvider.currentInstant()
    )
    EitherT.right[ErrorMessage](reservationRepository.insert(reservation))
  }

  private def createReservationItems(request: CreateReservationRequest, reservationId: ReservationId): Seq[ReservationItem] = {
    request.seats.map(seat =>
      ReservationItem(reservationId, seat.row, seat.column, seat.priceCategory, getPrice(seat.priceCategory))
    )
  }

  private def getPrice(priceCategory: PriceCategory.Value) = {
    priceCategory match {
      case PriceCategory.Adult =>
        reservationConfig.adultPrice
      case PriceCategory.Student =>
        reservationConfig.studentPrice
      case PriceCategory.Child =>
        reservationConfig.childPrice
    }
  }

  private def insertReservationItems(items: Seq[ReservationItem]): EitherT[DBIO, ErrorMessage, Seq[ReservationItem]] = {
    EitherT.right[ErrorMessage](
      items.traverse(reservationItemRepository.insert)
    )
  }

  private def createResponse(items: Seq[ReservationItem], reservation: Reservation) = {
    import dateTimeProvider._
    val totalPrice = items.map(_.price).sum
    val expirationTime = instantToLocal(currentInstant().plus(reservationConfig.reservationToExpirationInterval))
    val confirmationLink = s"${reservationConfig.reservationConfirmationBaseUrl}${reservation.confirmationId.id}"
    CreateReservationResponse(totalPrice, expirationTime, confirmationLink)
  }
}
