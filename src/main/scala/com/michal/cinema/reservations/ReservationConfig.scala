package com.michal.cinema.reservations

import java.time.Duration

case class ReservationConfig(
                              childPrice: BigDecimal,
                              studentPrice: BigDecimal,
                              adultPrice: BigDecimal,
                              reservationToScreeningMinInterval: Duration,
                              reservationToExpirationInterval: Duration,
                              reservationConfirmationBaseUrl: String
                            )
