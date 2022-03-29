package com.michal.cinema.reservations

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.michal.cinema.reservations.domain.ReservationsDomain.ReservationConfirmationId
import com.michal.cinema.reservations.services.CreateReservationService.CreateReservationRequest
import com.michal.cinema.reservations.services.{ConfirmReservationService, CreateReservationService, ScreeningDetailsService}
import com.michal.cinema.screenings.domain.ScreeningsDomain.ScreeningId
import com.michal.cinema.util.JsonFormats
import io.circe.{Json, JsonObject}

import scala.concurrent.ExecutionContext

class ReservationRoutes(
                         createReservationService: CreateReservationService,
                         screeningDetailsService: ScreeningDetailsService,
                         confirmReservationService: ConfirmReservationService
                       )(implicit val system: ActorSystem, ec: ExecutionContext) extends JsonFormats {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  import io.circe.syntax._

  val routes: Route =
    (pathPrefix("reservations")) {
      (pathEnd & post) {
        entity(as[CreateReservationRequest]) { request =>
          complete(
            createReservationService.create(request).map {
              case Left(error) =>
                StatusCodes.BadRequest -> error.asJson
              case Right(result) =>
                StatusCodes.OK -> result.asJson
            }
          )

        }
      } ~
        pathPrefix("confirm") {
          (path(JavaUUID) & pathEnd) { id =>
            get {
              complete(
                confirmReservationService.confirm(ReservationConfirmationId(id)).map {
                  case Left(error) => StatusCodes.BadRequest -> error.asJson
                  case Right(_) => StatusCodes.OK -> Json.fromJsonObject(JsonObject.empty)
                }
              )
            }
          }
        }
    } ~
      pathPrefix("screenings") {
        (path(JavaUUID) & pathEnd) { id =>
          get {
            complete(
              screeningDetailsService.get(ScreeningId(id)).map {
                case Left(error) => StatusCodes.BadRequest -> error.asJson
                case Right(result) => StatusCodes.OK -> result.asJson
              }
            )
          }
        }
      }

}
