package com.canal

import akka.Done
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext
import scala.concurrent.Await
import scala.concurrent.duration._
import org.scalatest.funsuite.AnyFunSuite
import imdbakka._

class AkkaMovieServiceSpec extends AnyFunSuite {

    implicit val system = ActorSystem("MovieService")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = ExecutionContext.global

    test("AkkaMovieService should provide Principals from existing title") {
        val mv = new AkkaMovieService()
        val futurePrincipals = mv.principalsForMovieName("The Man Inside")
            .runWith(Sink.seq)
        
        val results: Seq[Principal] = Await.result(futurePrincipals, 3.seconds)

        val expected: Seq[Principal] = Seq(
            Principal("Sidney Bracey", Some(1877), Some(1942), List("actor", "director", "soundtrack")),
            Principal("Edwin Stevens", Some(1860), Some(1923), List("actor", "director", "writer")),
            Principal("Tina Marshall", Some(1883), Some(1980), List("actress")),
            Principal("Charles Burbridge", Some(1849), Some(1922), List("actor")),
            Principal("Justina Huff", Some(1893), Some(1977), List("actress")))

         assert(results.toSet == expected.toSet)
        }

    test(" AkkaMovieService should return the 2 series with the most episodes") {
         val mv = new AkkaMovieService()

        val futureTopThreeSeries = mv.topTvSeriesWithGreatestNumberOfEpisodes(2)
            .runWith(Sink.seq)
        val results = Await.result(futureTopThreeSeries, 3.seconds)

        val expected = Seq(
            TvSeries("Flipper", Some(1995), Some(2000), List("Action", "Adventure", "Drama")),
            TvSeries("The Enid Blyton Adventure Series", Some(1996), None, List("Adventure", "Drama", "Family"))
            )

        assert(results.toSet == expected.toSet)
    }

    test("AkkaMovieService should return an empty sequence of principals from an inexistant film name") {
         val mv = new AkkaMovieService()

        val futurePrincipals = mv.principalsForMovieName("Yolo")
            .runWith(Sink.seq)
        val results: Seq[Principal] = Await.result(futurePrincipals, 3.seconds)

        assert(results == Seq.empty)
    }

    test("AkkaMovieService should return an empty sequence of series when asking for no series") {
         val mv = new AkkaMovieService()

        val futureEmptySeries = mv.topTvSeriesWithGreatestNumberOfEpisodes(0)
            .runWith(Sink.seq)
        val results = Await.result(futureEmptySeries, 3.seconds)

        assert(results == Seq.empty)
    }

}
