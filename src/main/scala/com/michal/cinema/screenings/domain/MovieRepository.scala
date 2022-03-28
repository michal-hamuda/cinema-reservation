package com.michal.cinema.screenings.domain

import com.michal.cinema.screenings.domain.ScreeningsDomain.{Movie, MovieId}
import com.michal.cinema.util.CustomMappers._
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

class MovieRepository(implicit ec: ExecutionContext) {

  def insert(movie: Movie): DBIO[Movie] = {
    (Movies.query += movie).map(_ => movie)
  }

  def findById(movieId: MovieId): DBIO[Option[Movie]] = {
    Movies.query.filter(_.id === movieId).result.headOption
  }

}
