package com.michal.cinema.screenings.domain

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Movie, MovieId}
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class Movies(tag: Tag) extends Table[Movie](tag, "movies") {

  def id = column[MovieId]("id", O.PrimaryKey)

  def title = column[String]("title")

  def durationMinutes = column[Int]("duration_minutes")

  override def * = (id, title, durationMinutes) <> (Movie.tupled, Movie.unapply)
}

object Movies {
  val query = TableQuery[Movies]
}


