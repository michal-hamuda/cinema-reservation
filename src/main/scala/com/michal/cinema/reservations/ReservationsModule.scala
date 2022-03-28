package com.michal.cinema.reservations

import akka.actor.{ActorRef, ActorSystem}
import com.michal.cinema.reservations.domain.{ReservationItemRepository, ReservationRepository}
import com.michal.cinema.reservations.services._
import com.michal.cinema.screenings.domain.{RoomRepository, ScreeningRepository}
import com.michal.cinema.util.{DateTimeProvider, SchedulerActorConfig}
import com.softwaremill.macwire._
import com.softwaremill.macwire.akkasupport._
import com.typesafe.config.Config
import slick.jdbc.H2Profile.api._

import scala.concurrent.ExecutionContext
import scala.jdk.DurationConverters._

trait ReservationsModule {
  implicit val executionContext: ExecutionContext

  implicit def system: ActorSystem

  def config: Config

  def db: Database

  def dateTimeProvider: DateTimeProvider

  def screeningRepository: ScreeningRepository

  def roomRepository: RoomRepository

  lazy val reservationItemRepository = wire[ReservationItemRepository]
  lazy val reservationRepository = wire[ReservationRepository]

  lazy val reservationConfig = ReservationConfig(
    childPrice = config.getDouble("cinema-reservations.prices.child"),
    studentPrice = config.getDouble("cinema-reservations.prices.student"),
    adultPrice = config.getDouble("cinema-reservations.prices.adult"),
    reservationToScreeningMinInterval = config.getDuration("cinema-reservations.reservationToScreeningMinInterval"),
    reservationToExpirationInterval = config.getDuration("cinema-reservations.reservationToExpirationInterval"),
    reservationConfirmationBaseUrl = config.getString("cinema-reservations.reservationConfirmationBaseUrl")
  )

  lazy val screeningDetailsService = wire[ScreeningDetailsService]
  lazy val validateReservationCreationService = wire[ValidateReservationCreationService]
  lazy val confirmReservationService = wire[ConfirmReservationService]
  lazy val createReservationService = wire[CreateReservationService]
  lazy val cancelReservationService = wire[CancelReservationService]

  lazy val cancelReservationJobConfig = SchedulerActorConfig(
    initialDelay = config.getDuration("cinema-reservations.schedulers.cancelReservation.initialDelay").toScala,
    interval = config.getDuration("cinema-reservations.schedulers.cancelReservation.interval").toScala,
  )
  lazy val cancelReservationJobExecutor: ActorRef = wireActor[CancelReservationJobExecutor]("cancelReservationJobExecutor")

  def startCancelReservationJob() = {
    system.scheduler.scheduleWithFixedDelay(
      cancelReservationJobConfig.initialDelay,
      cancelReservationJobConfig.interval,
      cancelReservationJobExecutor,
      "tick"
    )
  }


  lazy val reservationRoutes = wire[ReservationRoutes]

}
