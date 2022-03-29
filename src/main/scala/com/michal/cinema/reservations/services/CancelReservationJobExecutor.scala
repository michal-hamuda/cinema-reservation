package com.michal.cinema.reservations.services

import com.michal.cinema.util.JobExecutor

import scala.concurrent.ExecutionContext

class CancelReservationJobExecutor(cancelReservationService: CancelExpiredReservationsService)(implicit ec: ExecutionContext)
  extends JobExecutor(cancelReservationService)
