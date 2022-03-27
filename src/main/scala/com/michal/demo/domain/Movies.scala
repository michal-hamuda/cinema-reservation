package com.michal.demo.domain

import com.michal.demo.domain.CustomMappers._
import com.michal.demo.domain.Domain.{Movie, MovieId}
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


