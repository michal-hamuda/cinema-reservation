package com.michal.demo

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.michal.demo.Domain.ScreeningId
import com.michal.demo.services.{CreateReservationRequest, CreateReservationService, ScreeningDetailsService, SearchScreeningsService}

class ScreeningRoutes(
                     createReservationService: CreateReservationService,
                     screeningDetailsService: ScreeningDetailsService,
                     searchScreeningsService: SearchScreeningsService
                     )(implicit val system: ActorSystem) extends JsonFormats {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  // If ask takes more time than this to complete the request is failed
//  val aaa = system.settings.config
//  val aaaeee = system.settings.config.getDuration("my-app.routes.ask-timeout")
//  private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)


  val userRoutes: Route =
    pathPrefix("screenings") {
      (pathPrefix("search") & pathEnd) {
          get {
            parameters("from".as[String], "to".as[String]) { (from, to) =>
              val fromm = Instant.now()
              val too = Instant.now().plusSeconds(60)
              complete(
                searchScreeningsService.search(fromm, too)
              )
            }
          }
        } ~
      (path(LongNumber) & pathEnd) { id =>
        get {
          val idd = ScreeningId(id)
          complete(screeningDetailsService.get(idd))
        }
      } ~
        (pathPrefix("reserve") & pathEnd) {
            post {
              entity(as[CreateReservationRequest]) { request =>
                complete(createReservationService.create(request))

              }
            }
        }
    }
  //#all-routes
}
