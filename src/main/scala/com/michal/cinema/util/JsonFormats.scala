package com.michal.cinema.util

import com.michal.cinema.screenings.domain.Domain.PriceCategory
import com.michal.cinema.screenings.services.SeatStatus
import io.circe.{Decoder, Encoder}

trait JsonFormats {

  implicit val seatStatusDecoder: Decoder[SeatStatus.Value] = Decoder.decodeEnumeration(SeatStatus)
  implicit val seatStatusEncoder: Encoder[SeatStatus.Value] = Encoder.encodeEnumeration(SeatStatus)

  implicit val priceCategoryDecoder: Decoder[PriceCategory.Value] = Decoder.decodeEnumeration(PriceCategory)
  implicit val priceCategoryEncoder: Encoder[PriceCategory.Value] = Encoder.encodeEnumeration(PriceCategory)

}
