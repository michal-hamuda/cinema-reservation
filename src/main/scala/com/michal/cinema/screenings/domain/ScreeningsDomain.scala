package com.michal.cinema.screenings.domain

import java.time.Instant
import java.util.UUID

object ScreeningsDomain {

  object MovieId {
    def generate()=MovieId(UUID.randomUUID())
  }
  case class MovieId(id: UUID)

  case class Movie(id: MovieId, title: String, durationMinutes: Int)

  object RoomId {
    def generate()=RoomId(UUID.randomUUID())
  }
  case class RoomId(id: UUID)

  case class Room(id: RoomId, rows: Int, columns: Int)

  object ScreeningId {
    def generate()=ScreeningId(UUID.randomUUID())
  }
  case class ScreeningId(id: UUID)

  case class Screening(id: ScreeningId, movieId: MovieId, roomId: RoomId, startingAt: Instant)

}
