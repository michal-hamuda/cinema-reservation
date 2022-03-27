package com.michal.demo.domain

import com.michal.demo.domain.CustomMappers._
import com.michal.demo.domain.Domain.{Room, RoomId}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class RoomRepository(implicit ec: ExecutionContext) {

  def findById(roomId: RoomId): DBIO[Option[Room]] = {
    Rooms.query.filter(_.id === roomId).result.headOption
  }

}
