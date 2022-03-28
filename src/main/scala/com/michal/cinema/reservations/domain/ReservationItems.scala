package com.michal.cinema.reservations.domain

import com.michal.cinema.reservations.domain.ReservationsDomain.{PriceCategory, ReservationId, ReservationItem}
import com.michal.cinema.util.CustomMappers._
import slick.jdbc.H2Profile.api._

class ReservationItems(tag: Tag) extends Table[ReservationItem](tag, "reservation_items") {

  def id = column[ReservationId]("id", O.AutoInc, O.PrimaryKey)

  def row = column[Int]("row")

  def col = column[Int]("column")

  def priceCategory = column[PriceCategory.Value]("price_category")

  def price = column[BigDecimal]("price")

  // foreign key should be (id, row, col)

  override def * = (id, row, col, priceCategory, price) <> (ReservationItem.tupled, ReservationItem.unapply)
}

object ReservationItems {
  val query = TableQuery[ReservationItems]
}


