package com.michal.cinema.screenings.domain

import java.time.Instant
import java.util.UUID

object ScreeningsDomain {

  case class MovieId(id: UUID)

  case class Movie(id: MovieId, title: String, durationMinutes: Int)

  case class RoomId(id: UUID)

  case class Room(id: RoomId, rows: Int, columns: Int)

  case class ScreeningId(id: UUID)

  case class Screening(id: ScreeningId, movieId: MovieId, roomId: RoomId, start: Instant)

}
