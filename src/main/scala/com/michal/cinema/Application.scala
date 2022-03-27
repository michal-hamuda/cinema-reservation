package com.michal.cinema

import akka.actor.ActorSystem
import com.michal.cinema.reservations.ReservationsModule
import com.michal.cinema.screenings.ScreeeningsModule
import com.michal.cinema.util.{DateTimeProvider, DateTimeProviderImpl}
import com.typesafe.config.{Config, ConfigFactory}
import slick.jdbc.H2Profile.api._
import com.softwaremill.macwire.wire
import akka.http.scaladsl.server.Directives._

trait Application extends ScreeeningsModule with ReservationsModule {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  lazy val config: Config = ConfigFactory.load()
  lazy val dateTimeProvider: DateTimeProvider = wire[DateTimeProviderImpl]
  lazy val db: Database = Database.forConfig("database")

  val routes = screeningRoutes.routes ~ reservationRoutes.routes
}
