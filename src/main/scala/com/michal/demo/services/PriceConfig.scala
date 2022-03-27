package com.michal.demo.services

import java.time.Duration

case class PriceConfig(
                        childPrice: BigDecimal,
                        studentPrice: BigDecimal,
                        adultPrice: BigDecimal,
                        reservationThreshold: Duration,
                        expirationThreshold: Duration
                      )
