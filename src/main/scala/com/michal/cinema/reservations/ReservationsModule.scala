package com.michal.cinema.reservations

import akka.actor.ActorSystem
import com.michal.cinema.reservations.domain.{ReservationItemRepository, ReservationRepository}
import com.michal.cinema.reservations.services.{CreateReservationService, ValidateReservationCreationService}
import com.michal.cinema.screenings.ScreeningRoutes
import com.michal.cinema.screenings.domain.{RoomRepository, ScreeningRepository}
import com.michal.cinema.screenings.services.{ScreeningDetailsService, SearchScreeningsService}
import com.michal.cinema.util.{DateTimeProvider, DateTimeProviderImpl}
import com.softwaremill.macwire._
import com.typesafe.config.Config
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext

trait ReservationsModule {
  implicit val executionContext: ExecutionContext
  implicit def system: ActorSystem
  def config: Config

  def db: Database
  def dateTimeProvider: DateTimeProvider

  def screeningRepository: ScreeningRepository
  def screeningDetailsService: ScreeningDetailsService

  //  lazy val roomRepository = wire[RoomRepository]
  lazy val reservationItemRepository = wire[ReservationItemRepository]
  lazy val reservationRepository = wire[ReservationRepository]

  lazy val reservationConfig = ReservationConfig(
    childPrice = config.getDouble("cinema-reservations.prices.child"),
    studentPrice = config.getDouble("cinema-reservations.prices.student"),
    adultPrice = config.getDouble("cinema-reservations.prices.adult"),
    reservationThreshold = config.getDuration("cinema-reservations.reservationThreshold"),
    expirationThreshold = config.getDuration("cinema-reservations.expirationThreshold")
  )

  lazy val validateReservationCreationService = wire[ValidateReservationCreationService]
  lazy val createReservationService = wire[CreateReservationService]


  lazy val reservationRoutes = wire[ReservationRoutes]

}
