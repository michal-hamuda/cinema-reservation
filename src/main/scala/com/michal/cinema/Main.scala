package com.michal.cinema

import akka.http.scaladsl.Http

import scala.util.{Failure, Success}

object Main extends Application with SampleData {
  def main(args: Array[String]): Unit = {
    println("This is the demo, and the year is 2022")

    initializeDatabaseWithSampleData()
    val futureBinding = Http().newServerAt("localhost", 8080).bind(routes)
    futureBinding.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("Server online at http://{}:{}/", address.getHostString, address.getPort)
        startAllJobs()
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
  }
}

