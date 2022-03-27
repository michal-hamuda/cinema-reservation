package com.michal.demo

import com.michal.demo.domain.Domain.PriceCategory
import com.michal.demo.services.SeatStatus
import io.circe.{Decoder, Encoder}

//#json-formats


trait JsonFormats {

  implicit val seatStatusDecoder: Decoder[SeatStatus.Value] = Decoder.decodeEnumeration(SeatStatus)
  implicit val seatStatusEncoder: Encoder[SeatStatus.Value] = Encoder.encodeEnumeration(SeatStatus)

  implicit val priceCategoryDecoder: Decoder[PriceCategory.Value] = Decoder.decodeEnumeration(PriceCategory)
  implicit val priceCategoryEncoder: Encoder[PriceCategory.Value] = Encoder.encodeEnumeration(PriceCategory)

}

//#json-formats
