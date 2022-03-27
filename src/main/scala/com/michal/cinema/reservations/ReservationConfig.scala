package com.michal.cinema.reservations

import java.time.Duration

case class ReservationConfig(
                        childPrice: BigDecimal,
                        studentPrice: BigDecimal,
                        adultPrice: BigDecimal,
                        reservationThreshold: Duration,
                        expirationThreshold: Duration
                      )
