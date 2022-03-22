package com.michal.demo.services

import java.time.Instant

import com.michal.demo.Domain.PriceCategory

import scala.concurrent.Future


case class CreateReservationRequest(
                                     userFirstName: String,
                                     userLastName: String,
                                     seats: List[CreateReservationItem]
                                   )

case class CreateReservationItem(row: Int, column: Int, priceCategory: PriceCategory.Value)

case class CreateReservationResponse(
                                    totalPrice: BigDecimal,
                                    expirationTime: Instant
                                    )

case class Error(message: String)

class CreateReservationService {
  def create(request: CreateReservationRequest): Future[Either[Error, CreateReservationResponse]] = {
    Future.successful(
      Right(
        CreateReservationResponse(
          BigDecimal(5.5),
          Instant.now()
        )
      )
    )

  }
}
