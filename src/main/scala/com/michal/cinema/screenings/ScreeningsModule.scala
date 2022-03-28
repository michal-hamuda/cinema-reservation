package com.michal.cinema.screenings

import akka.actor.ActorSystem
import com.michal.cinema.reservations.domain.ReservationItemRepository
import com.michal.cinema.screenings.domain.{RoomRepository, ScreeningRepository}
import com.michal.cinema.screenings.services.SearchScreeningsService
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

trait ScreeningsModule {
  implicit val executionContext: ExecutionContext

  implicit def system: ActorSystem

  def config: Config

  def reservationItemRepository: ReservationItemRepository

  def db: Database

  lazy val screeningRepository = wire[ScreeningRepository]
  lazy val roomRepository = wire[RoomRepository]

  //  lazy val dateTimeProvider: DateTimeProvider = wire[DateTimeProviderImpl]
  lazy val searchScreeningsService = wire[SearchScreeningsService]


  lazy val screeningRoutes = wire[ScreeningRoutes]

}
