package com.michal.cinema.reservations

import java.time.Instant

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.util.Timeout
import com.michal.cinema.reservations.services.CreateReservationService
import com.michal.cinema.reservations.services.CreateReservationService.CreateReservationRequest
import com.michal.cinema.screenings.domain.Domain.ScreeningId
import com.michal.cinema.screenings.services.{ScreeningDetailsService, SearchScreeningsService}
import com.michal.cinema.util.JsonFormats

import scala.concurrent.ExecutionContext

class ReservationRoutes(
                       createReservationService: CreateReservationService
                     )(implicit val system: ActorSystem, ec: ExecutionContext) extends JsonFormats {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import io.circe.syntax._

  // If ask takes more time than this to complete the request is failed
  //  val aaa = system.settings.config
  //  val aaaeee = system.settings.config.getDuration("my-app.routes.ask-timeout")
//    private implicit val timeout = Timeout.create(system.settings.config.getDuration("my-app.routes.ask-timeout"))
//  private implicit val timeout = Timeout(5, TimeUnit.SECONDS)


  val routes: Route =
    pathPrefix("screenings") {
        (pathPrefix("reserve") & pathEnd) {
          post {
            entity(as[CreateReservationRequest]) { request =>
              complete(
                createReservationService.create(request).map {
                  case Left(error) => StatusCodes.BadRequest -> error.asJson
                  case Right(result) => StatusCodes.OK -> result.asJson
                }
              )

            }
          }
        }
    }

}
