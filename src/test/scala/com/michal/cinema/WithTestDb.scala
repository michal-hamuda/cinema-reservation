package com.michal.cinema

import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import slick.dbio.DBIO
import slick.jdbc.H2Profile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext}

trait WithTestDb extends AnyFlatSpec with Matchers with BeforeAndAfterAll {
  val db = Database.forConfig("test-database")

  override protected def beforeAll(): Unit = {
    executeDbio(Tables.createAll()(ExecutionContext.global))
  }


  override protected def afterAll(): Unit = {
    executeDbio(Tables.dropAll())
  }

  def executeDbio[T](dbio: DBIO[T]): T = {
    Await.result(db.run(dbio), Duration.Inf)
  }
}
