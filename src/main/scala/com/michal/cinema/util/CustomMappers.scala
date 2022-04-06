package com.michal.cinema.util

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.michal.cinema.reservations.domain.ReservationsDomain.{PriceCategory, ReservationConfirmationId, ReservationId, ReservationStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain._
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

  implicit val reservationConfirmationIdMapper: BaseColumnType[ReservationConfirmationId] = MappedColumnType.base[ReservationConfirmationId, UUID](
    id => id.id,
    ReservationConfirmationId.apply
  )

  implicit val reservationStatusMapper: BaseColumnType[ReservationStatus.Value] = MappedColumnType.base[ReservationStatus.Value, String](
    id => id.toString,
    ReservationStatus.withName
  )

  def instantToTimestamp(instant: Instant) = Option(new Timestamp(instant.toEpochMilli))

  def timestampToInstant(timestamp: Timestamp) = Instant.ofEpochMilli(timestamp.getTime)


  implicit val priceCategoryMapper: BaseColumnType[PriceCategory.Value] = MappedColumnType.base[PriceCategory.Value, String](
    id => id.toString,
    PriceCategory.withName
  )

}
