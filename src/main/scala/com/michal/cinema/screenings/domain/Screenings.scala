package com.michal.cinema.screenings.domain

import java.sql.Timestamp

import com.michal.cinema.screenings.domain.ScreeningsDomain.{MovieId, RoomId, Screening, ScreeningId}
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Screenings(tag: Tag) extends Table[Screening](tag, "screenings") {

  def id = column[ScreeningId]("id", O.PrimaryKey)

  def movieId = column[MovieId]("movie_id")

  def roomId = column[RoomId]("room_id")

  def startingAt = column[Timestamp]("start")

  def startingAtInstant = (startingAt) <> (timestampToInstant _, instantToTimestamp _)


  override def * = (id, movieId, roomId, startingAtInstant) <> (Screening.tupled, Screening.unapply)
}

object Screenings {
  val query = TableQuery[Screenings]
}



