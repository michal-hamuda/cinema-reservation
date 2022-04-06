package com.michal.cinema.util

import java.time.LocalDateTime
import java.util.UUID

import akka.http.scaladsl.unmarshalling.FromStringUnmarshaller
import akka.stream.Materializer
import com.michal.cinema.reservations.domain.ReservationsDomain.PriceCategory
import com.michal.cinema.reservations.services.ScreeningDetailsService.SeatStatus
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import io.circe.{Decoder, Encoder}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait JsonFormats {

  implicit val ScreeningIdEncoder: Encoder[ScreeningId] = Encoder.encodeString.contramap[ScreeningId](_.id.toString)
  implicit val ScreeningIdDecoder: Decoder[ScreeningId] = Decoder.decodeString.emapTry { str => Try(ScreeningId(UUID.fromString(str))) }

  implicit val seatStatusDecoder: Decoder[SeatStatus.Value] = Decoder.decodeEnumeration(SeatStatus)
  implicit val seatStatusEncoder: Encoder[SeatStatus.Value] = Encoder.encodeEnumeration(SeatStatus)

  implicit val priceCategoryDecoder: Decoder[PriceCategory.Value] = Decoder.decodeEnumeration(PriceCategory)
  implicit val priceCategoryEncoder: Encoder[PriceCategory.Value] = Encoder.encodeEnumeration(PriceCategory)

  val localDateU: FromStringUnmarshaller[LocalDateTime] = {
    new FromStringUnmarshaller[LocalDateTime] {
      override def apply(value: String)(implicit ec: ExecutionContext, materializer: Materializer): Future[LocalDateTime] =
        Future.fromTry(Try(LocalDateTime.parse(value)))
    }
  }

}
