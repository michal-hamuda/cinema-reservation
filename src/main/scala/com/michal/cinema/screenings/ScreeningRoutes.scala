package com.michal.cinema.screenings

import java.time.Instant
import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.michal.cinema.screenings.services.SearchScreeningsService
import com.michal.cinema.util.JsonFormats

import scala.concurrent.ExecutionContext

class ScreeningRoutes(
                       searchScreeningsService: SearchScreeningsService
                     )(implicit val system: ActorSystem, ec: ExecutionContext) extends JsonFormats {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val routes: Route =
    pathPrefix("screenings") {
      (pathPrefix("search") & pathEnd) {
        get {
          parameters("from".as[Instant], "to".as[Instant]) { (from, to) =>
            complete(
              searchScreeningsService.search(from, to)
            )
          }
        }
      }
    }

}
