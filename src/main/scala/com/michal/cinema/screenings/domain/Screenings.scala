package com.michal.cinema.screenings.domain

import java.time.Instant

import com.michal.cinema.screenings.domain.ScreeningsDomain.{MovieId, RoomId, Screening, ScreeningId}
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Screenings(tag: Tag) extends Table[Screening](tag, "screenings") {

  def id = column[ScreeningId]("id", O.AutoInc, O.PrimaryKey)

  def movieId = column[MovieId]("start_time")

  def roomId = column[RoomId]("start_time")

  def startingAt = column[Instant]("start")

  override def * = (id, movieId, roomId, startingAt) <> (Screening.tupled, Screening.unapply)
}

object Screenings {
  val query = TableQuery[Screenings]
}



