package com.michal.demo

import akka.actor.ActorSystem
import com.michal.demo.services.{CreateReservationService, ScreeningDetailsService, SearchScreeningsService}
import com.softwaremill.macwire._

trait Dependencies {

  val searchScreeningsService = wire[SearchScreeningsService]
  val screeningDetailsService = wire[ScreeningDetailsService]
  val createReservationService = wire[CreateReservationService]

  implicit def system: ActorSystem

  val screeningRoutes = wire[ScreeningRoutes]

}
