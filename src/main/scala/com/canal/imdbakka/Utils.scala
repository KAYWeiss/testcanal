package com.canal.imdbakka
import scala.concurrent.Future

object Utils {
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    def insertToList(li:List[(String, Int)],x:(String, Int))(implicit cmp:Ordering[Int]): List[(String, Int)]={
        val (first,last)=li.partition {e => cmp.gteq(e._2, x._2) }
        first:::x::last
    }      

    def futureSeqToBoolean[T](valuesAccepted: Future[Seq[T]], currentValue: T): Future[Boolean] = {
      valuesAccepted.map(_.contains(currentValue))
    }
    def futureValueToBoolean[T](valueNeeded: Future[Option[T]], currentValue: T): Future[Boolean] = {
      valueNeeded.map(_ match {
          case Some(value) => value == currentValue
          case None => false
    })}
}