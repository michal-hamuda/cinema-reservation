package com.michal.cinema.screenings.domain

import java.time.Instant
import java.time.temporal.ChronoUnit

import com.michal.cinema.WithTestDb
import com.michal.cinema.screenings.services.SearchScreeningsService.ScreeningData
import com.michal.cinema.util.DateTimeProviderImpl

class ScreeningRepositoryTest extends WithTestDb {
  import com.michal.cinema.TestUtils._
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  val dateTimeProvider = new DateTimeProviderImpl
  val movieRepository = new MovieRepository()
  val repository = new ScreeningRepository(dateTimeProvider)

  "ConfirmReservationService" should "return ok for valid data" in {

    val now = Instant.now()
    val from = now.plus(40, ChronoUnit.MINUTES)
    val to = now.plus(100, ChronoUnit.MINUTES)

    val movie1 = generateMovie()
    val movie2 = generateMovie()

    val screening1 = generateScreening().copy(movieId = movie1.id, startingAt = now.plus(30, ChronoUnit.MINUTES))
    val screening2 = generateScreening().copy(movieId = movie2.id, startingAt = now.plus(60, ChronoUnit.MINUTES))
    val screening3 = generateScreening().copy(movieId = movie1.id, startingAt = now.plus(90, ChronoUnit.MINUTES))
    val screening4 = generateScreening().copy(movieId = movie2.id, startingAt = now.plus(120, ChronoUnit.MINUTES))


    val result = executeDbio(
      for{
        _ <- movieRepository.insert(movie1)
        _ <- movieRepository.insert(movie2)
        _ <- repository.insert(screening1)
        _ <- repository.insert(screening2)
        _ <- repository.insert(screening3)
        _ <- repository.insert(screening4)
        searchResult <- repository.findScreenings(from, to)
      } yield searchResult
    )

    result should contain theSameElementsAs Seq(
      ScreeningData(screening2.id, movie2.title, dateTimeProvider.instantToLocal(screening2.startingAt), movie2.durationMinutes),
      ScreeningData(screening3.id, movie1.title, dateTimeProvider.instantToLocal(screening3.startingAt), movie1.durationMinutes)
    )

  }
}
