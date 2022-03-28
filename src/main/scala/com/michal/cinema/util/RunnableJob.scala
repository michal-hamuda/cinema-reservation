package com.michal.cinema.util

import scala.concurrent.Future

trait RunnableJob {
  def run(): Future[Unit]
}
