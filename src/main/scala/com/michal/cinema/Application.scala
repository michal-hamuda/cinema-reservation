package com.michal.cinema

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import com.michal.cinema.reservations.ReservationsModule
import com.michal.cinema.screenings.ScreeningsModule
import com.michal.cinema.util.{DateTimeProvider, DateTimeProviderImpl}
import com.softwaremill.macwire.wire
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.H2Profile.api._

trait Application extends ScreeningsModule with ReservationsModule {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  lazy val config: Config = ConfigFactory.load()
  lazy val dateTimeProvider: DateTimeProvider = wire[DateTimeProviderImpl]
  lazy val db: Database = Database.forConfig("database")

  def startAllJobs() = {
    startCancelReservationJob()
  }

  val routes = screeningRoutes.routes ~ reservationRoutes.routes


}
