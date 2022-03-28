package com.michal.cinema.screenings.domain

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Room, RoomId}
import com.michal.cinema.util.CustomMappers._
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class RoomRepository(implicit ec: ExecutionContext) {

  def insert(room: Room): DBIO[Room] = {
    (Rooms.query += room).map(_ => room)
  }

  def findById(roomId: RoomId): DBIO[Option[Room]] = {
    Rooms.query.filter(_.id === roomId).result.headOption
  }

}
