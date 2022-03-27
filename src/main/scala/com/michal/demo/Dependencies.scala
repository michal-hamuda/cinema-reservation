package com.michal.demo

import java.time.Duration

import akka.actor.ActorSystem
import com.michal.demo.domain.{ReservationItemRepository, ReservationRepository, RoomRepository, ScreeningRepository}
import com.michal.demo.services._
import com.softwaremill.macwire._
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

trait Dependencies {
  implicit val executionContext: ExecutionContext

  lazy val db: Database = Database.forConfig("database")

  lazy val screeningRepository = wire[ScreeningRepository]
  lazy val roomRepository = wire[RoomRepository]
  lazy val reservationItemRepository = wire[ReservationItemRepository]
  lazy val reservationRepository = wire[ReservationRepository]
  lazy val priceConfig = PriceConfig(
    childPrice = 1,
    studentPrice = 1,
    adultPrice = 1,
    reservationThreshold = Duration.ZERO,
    expirationThreshold = Duration.ZERO
  )

  lazy val dateTimeProvider: DateTimeProvider = wire[DateTimeProviderImpl]
  lazy val validateReservationCreationService = wire[ValidateReservationCreationService]
  lazy val searchScreeningsService = wire[SearchScreeningsService]
  lazy val screeningDetailsService = wire[ScreeningDetailsService]
  lazy val createReservationService = wire[CreateReservationService]

  implicit def system: ActorSystem

  lazy val screeningRoutes = wire[ScreeningRoutes]

}
