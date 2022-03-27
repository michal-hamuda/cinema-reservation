package com.michal.cinema.util

import java.util.UUID

import com.michal.cinema.screenings.domain.Domain._
import slick.jdbc.H2Profile.api._

object CustomMappers {

  implicit val movieIdMapper: BaseColumnType[MovieId] = MappedColumnType.base[MovieId, UUID](
    id => id.id,
    MovieId.apply
  )

  implicit val roomIdMapper: BaseColumnType[RoomId] = MappedColumnType.base[RoomId, UUID](
    id => id.id,
    RoomId.apply
  )

  implicit val screeningIdMapper: BaseColumnType[ScreeningId] = MappedColumnType.base[ScreeningId, UUID](
    id => id.id,
    ScreeningId.apply
  )

  implicit val reservationIdMapper: BaseColumnType[ReservationId] = MappedColumnType.base[ReservationId, UUID](
    id => id.id,
    ReservationId.apply
  )

  implicit val reservationStatusMapper: BaseColumnType[ReservationStatus.Value] = MappedColumnType.base[ReservationStatus.Value, String](
    id => id.toString,
    ReservationStatus.withName
  )

  implicit val priceCategoryMapper: BaseColumnType[PriceCategory.Value] = MappedColumnType.base[PriceCategory.Value, String](
    id => id.toString,
    PriceCategory.withName
  )

}
