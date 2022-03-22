package com.michal.demo.services

import com.michal.demo.Domain.ScreeningId

import scala.concurrent.Future

object SeatStatus extends Enumeration {
  val Free, Taken, Proposed = Value
}
case class ScreeningDetails(id: ScreeningId, bookingAvailable: Boolean, seats: List[List[SeatStatus.Value]])

class ScreeningDetailsService {
  def get(screeningId: ScreeningId): Future[Option[ScreeningDetails]] = {
    Future.successful(
      Some(
        ScreeningDetails(
          ScreeningId(150),
          true,
          List(
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Taken, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Taken, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          )
        )
      )

    )
  }
}
