package com.michal.demo

import akka.actor.ActorSystem

trait Application extends Dependencies {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher

  val routes = screeningRoutes.userRoutes
}
