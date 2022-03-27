package com.michal.cinema.screenings.domain

import com.michal.cinema.util.CustomMappers._
import com.michal.cinema.screenings.domain.Domain.{Movie, MovieId}
import slick.jdbc.H2Profile.api._

class Movies(tag: Tag) extends Table[Movie](tag, "movies") {

  def id = column[MovieId]("id", O.AutoInc, O.PrimaryKey)

  def title = column[String]("start_time")

  def durationMinutes = column[Int]("end_time")

  override def * = (id, title, durationMinutes) <> (Movie.tupled, Movie.unapply)
}

object Movies {
  val query = TableQuery[Movies]
}


