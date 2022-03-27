package com.michal.demo.services

import java.util.UUID

import cats.data.OptionT
import com.michal.demo.domain.Domain.{ReservationItem, Room, Screening, ScreeningId}
import com.michal.demo.domain.{ReservationItemRepository, RoomRepository, ScreeningRepository}
import com.rms.miu.slickcats.DBIOInstances._
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

object SeatStatus extends Enumeration {
  val Free, Taken, Proposed = Value
}

case class SeatInfo(row: Int, column: Int, status: SeatStatus.Value)

case class ScreeningDetails(id: ScreeningId,
                            totalRows: Int,
                            totalColumns: Int,
                            seats: Seq[SeatInfo]
                           )

class ScreeningDetailsService(
                               db: Database,
                               screeningRepository: ScreeningRepository,
                               roomRepository: RoomRepository,
                               reservationItemRepository: ReservationItemRepository
                             )(implicit ec: ExecutionContext) {
  def get(screeningId: ScreeningId): Future[Option[ScreeningDetails]] = {
   db.run(getDbio(screeningId))
  }

  def getDbio(screeningId: ScreeningId): DBIO[Option[ScreeningDetails]] = {
    for {
      screening <- OptionT(screeningRepository.findById(screeningId))
      room <- OptionT(roomRepository.findById(screening.roomId))
      reservedItems <- OptionT.liftF(reservationItemRepository.findByScreeningId(screeningId))
    } yield createScreeningDetails(screening, room, reservedItems)


    DBIO.successful(
      Some(
        ScreeningDetails(
          ScreeningId(UUID.randomUUID()),
          3,
          2,
          List.empty
          //          List(
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Taken, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Taken, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //            List(SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free, SeatStatus.Free),
          //          )
        )
      )

    )
  }

  def createScreeningDetails(screening: Screening, room: Room, items: Seq[ReservationItem]): ScreeningDetails = {
    val seatInfo = for {
      row <- 0 to room.rows
      column <- 0 to room.columns
    } yield {
      val isTaken = items.exists(item => item.row == row && item.column == column)
      val status = if (isTaken) SeatStatus.Taken else SeatStatus.Free
      SeatInfo(row, column, status)
    }

    ScreeningDetails(screening.id, room.rows, room.columns, seatInfo)
  }
}
