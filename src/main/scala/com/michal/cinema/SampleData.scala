package com.michal.cinema

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

import com.michal.cinema.screenings.domain.ScreeningsDomain._

import scala.concurrent.Future

trait SampleData {
  self: Application =>

  def initializeDatabaseWithSampleData(): Future[Unit] = {
    val start = Instant.now()

    val inOneHour = start.plus(1, ChronoUnit.HOURS)
    val inTwoHours = start.plus(2, ChronoUnit.HOURS)

    val screeningId1 = ScreeningId(UUID.fromString("103bb746-88a1-4239-ae5b-414c04ae4dab"))
    val screeningId2 = ScreeningId(UUID.fromString("4791cce2-ba82-49fe-a74c-0ab67ae2f066"))
    val screeningId3 = ScreeningId(UUID.fromString("368bbdac-d933-4e24-8db8-414c04ae4dab"))
    val screeningId4 = ScreeningId(UUID.fromString("4791cce2-d934-4e24-8db8-414c04ae4dab"))
    val screeningId5 = ScreeningId(UUID.fromString("368bbdac-d935-4e24-8db8-c7ffa880e2ab"))
    val screeningId6 = ScreeningId(UUID.fromString("4791cce2-d936-4e24-8db8-c7ffa880e2ab"))

    val dbio = for {
      _ <- Tables.createAll()
      movie1 <- insertMovie("Matrix", 80)
      movie2 <- insertMovie("Star Wars", 60)
      movie3 <- insertMovie("Interstellar", 70)
      room1 <- insertRoom(10, 20)
      room2 <- insertRoom(5, 10)
      room3 <- insertRoom(10, 10)
      screening1 <- insertScreening(screeningId1, movie1.id, room1.id, inOneHour)
      screening2 <- insertScreening(screeningId2, movie2.id, room2.id, inOneHour)
      screening3 <- insertScreening(screeningId3, movie3.id, room3.id, inOneHour)
      screening4 <- insertScreening(screeningId4, movie3.id, room1.id, inTwoHours)
      screening5 <- insertScreening(screeningId5, movie2.id, room2.id, inTwoHours)
      screening6 <- insertScreening(screeningId6, movie1.id, room3.id, inTwoHours)
    } yield ()
    db.run(dbio)
  }

  def insertMovie(title: String, duration: Int) = {
    val movie = Movie(
      MovieId(UUID.randomUUID()),
      title,
      duration
    )
    movieRepository.insert(movie)
  }

  def insertRoom(rows: Int, columns: Int) = {
    val obj = Room(
      RoomId(UUID.randomUUID()),
      rows,
      columns
    )
    roomRepository.insert(obj)
  }

  def insertScreening(screeningId: ScreeningId, movieId: MovieId, roomId: RoomId, start: Instant) = {
    val obj = Screening(
      screeningId,
      movieId,
      roomId,
      start
    )
    screeningRepository.insert(obj)
  }
}
