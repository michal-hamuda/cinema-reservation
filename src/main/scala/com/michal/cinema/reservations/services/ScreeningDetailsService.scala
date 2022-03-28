package com.michal.cinema.reservations.services


import cats.data.OptionT
import com.michal.cinema.reservations.domain.ReservationItemRepository
import com.michal.cinema.reservations.domain.ReservationsDomain.ReservationItem
import com.michal.cinema.reservations.services.ScreeningDetailsService.{ScreeningDetails, SeatInfo, SeatStatus}
import com.michal.cinema.screenings.domain.ScreeningsDomain.{Room, Screening, ScreeningId}
import com.michal.cinema.screenings.domain.{RoomRepository, ScreeningRepository}
import com.rms.miu.slickcats.DBIOInstances._
import slick.jdbc.H2Profile.api._

import scala.concurrent.{ExecutionContext, Future}

object ScreeningDetailsService {

  object SeatStatus extends Enumeration {
    val Free, Taken = Value
  }

  case class SeatInfo(row: Int, column: Int, status: SeatStatus.Value)

  case class ScreeningDetails(id: ScreeningId,
                              totalRows: Int,
                              totalColumns: Int,
                              seats: Seq[SeatInfo]
                             )

}

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
    (for {
      screening <- OptionT(screeningRepository.findById(screeningId))
      room <- OptionT(roomRepository.findById(screening.roomId))
      reservedItems <- OptionT.liftF(reservationItemRepository.findActiveByScreeningId(screeningId))
    } yield createScreeningDetails(screening, room, reservedItems)).value
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
