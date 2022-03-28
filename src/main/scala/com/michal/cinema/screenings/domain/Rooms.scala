package com.michal.cinema.screenings.domain

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Room, RoomId}
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Rooms(tag: Tag) extends Table[Room](tag, "rooms") {

  def id = column[RoomId]("id", O.AutoInc, O.PrimaryKey)

  def rows = column[Int]("rows")

  def columns = column[Int]("columns")

  override def * = (id, rows, columns) <> (Room.tupled, Room.unapply)
}

object Rooms {
  val query = TableQuery[Rooms]
}




