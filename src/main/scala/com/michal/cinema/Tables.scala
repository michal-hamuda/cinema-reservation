package com.michal.cinema

import com.michal.cinema.reservations.domain.{ReservationItems, Reservations}
import com.michal.cinema.screenings.domain.{Movies, Rooms, Screenings}
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

object Tables {

  val allTables = Seq(
    Movies.query.schema,
    Rooms.query.schema,
    Screenings.query.schema,
    Reservations.query.schema,
    ReservationItems.query.schema
  )

  def createAll()(implicit ec: ExecutionContext): DBIO[Unit] = {
    val schema = allTables.reduce(_ ++ _)
    schema.create
  }

  def dropAll(): DBIO[Unit] = {
    allTables.reverse.reduce(_ ++ _).drop
  }

}
