package com.michal.cinema.util


import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

case class SchedulerActorConfig(initialDelay: FiniteDuration, interval: FiniteDuration)

class JobExecutor(job: RunnableJob)(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  // can be extended with retries, logging etc. if necessary
  override def receive: Receive = {
    case _ =>
      job.run
  }
}
