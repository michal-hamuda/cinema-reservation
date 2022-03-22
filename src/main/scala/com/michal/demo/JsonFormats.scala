package com.michal.demo

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.michal.demo.Domain.{PriceCategory, ScreeningId}
import com.michal.demo.services.{ScreeningData, ScreeningDetails, SeatStatus}
import io.circe.{Decoder, Encoder}

//#json-formats


trait JsonFormats   {

  implicit val seatStatusDecoder: Decoder[SeatStatus.Value] = Decoder.decodeEnumeration(SeatStatus)
  implicit val seatStatusEncoder: Encoder[SeatStatus.Value] = Encoder.encodeEnumeration(SeatStatus)

  implicit val priceCategoryDecoder: Decoder[PriceCategory.Value] = Decoder.decodeEnumeration(PriceCategory)
  implicit val priceCategoryEncoder: Encoder[PriceCategory.Value] = Encoder.encodeEnumeration(PriceCategory)

}
//#json-formats
