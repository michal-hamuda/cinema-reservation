package com.michal.cinema.util

import com.michal.cinema.reservations.domain.ReservationsDomain.PriceCategory
import com.michal.cinema.reservations.services.ScreeningDetailsService.SeatStatus
import io.circe.{Decoder, Encoder}

trait JsonFormats {

  implicit val seatStatusDecoder: Decoder[SeatStatus.Value] = Decoder.decodeEnumeration(SeatStatus)
  implicit val seatStatusEncoder: Encoder[SeatStatus.Value] = Encoder.encodeEnumeration(SeatStatus)

  implicit val priceCategoryDecoder: Decoder[PriceCategory.Value] = Decoder.decodeEnumeration(PriceCategory)
  implicit val priceCategoryEncoder: Encoder[PriceCategory.Value] = Encoder.encodeEnumeration(PriceCategory)

}
