package com.michal.cinema.screenings

import akka.actor.ActorSystem
import com.michal.cinema.screenings.domain.{MovieRepository, RoomRepository, ScreeningRepository}
import com.michal.cinema.screenings.services.SearchScreeningsService
import com.michal.cinema.util.DateTimeProvider
import com.softwaremill.macwire.wire
import com.typesafe.config.Config
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

trait ScreeningsModule {
  implicit val executionContext: ExecutionContext

  implicit def system: ActorSystem

  def config: Config

  def db: Database

  def dateTimeProvider: DateTimeProvider

  lazy val screeningRepository = wire[ScreeningRepository]
  lazy val roomRepository = wire[RoomRepository]
  lazy val movieRepository = wire[MovieRepository]

  lazy val searchScreeningsService = wire[SearchScreeningsService]


  lazy val screeningRoutes = wire[ScreeningRoutes]

}
