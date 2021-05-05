package com.canal

import akka.Done
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import akka.stream.scaladsl.Sink
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.duration._
import com.canal.config.Config._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import models._
import imdbakka.ImdbFilters._
import fileparser.ImdbFileParser

class AkkaFiltersSpec extends AnyWordSpecLike with Matchers {

    implicit val system = ActorSystem("MovieService")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = ExecutionContext.global
    val titles = ImdbFileParser.parseFile(TITLES_FILE)
    .map(ImdbFileParser.castTitle(_))
    val persons = ImdbFileParser.parseFile(PERSONS_FILE)
    val crewmembers = ImdbFileParser.parseFile(CREWMEMBERS_FILE)
    .map(ImdbFileParser.castCrewMember(_))
    val episodes = ImdbFileParser.parseFile(EPISODES_FILE)
    .map(ImdbFileParser.castEpisode(_))

      "AkkaFilters should return the episodes" in {
        //val episodeCounts = episodes.via(countEpisodeNumber)
        //val topEpisodes = episodeCounts.via(topElements(2)).runWith(Sink.seq)


        //val result = titles.via(asyncFilter(t => topEpisodes.map(_.contains(t.tconst))))

        val resultMovie = titles
            .via(getMovieFromName("The Man Inside"))
            .runWith(Sink.headOption)

          val result = crewmembers.via(getCrewMembersFromMovie(resultMovie))
          .runWith(Sink.seq)

          val cms = Await.result(result, 3.seconds)
          println(cms)
          /*val name: Option[String] = Await.result(result, 3.seconds)

          name match {case Some(name)=> println(name)
            case None => println("No title")}
            */
        //val topSeries = episodes.via(getTopTvSeriesCombined(2, titles))
        //val episodeCounts = countEpisodeNumber(episodes: Source[Episode, _])
        /*val future1 = episodeCounts.runForeach(i => println(i))
        val results1 = Await.result(future1, 30.seconds)

        val futureEpisodes = topElements(episodeCounts, 10)
            .runWith(Sink.head)
        
        val results = Await.result(futureEpisodes, 30.seconds)

        }
        
        println("print the episodes in the files")
         val done: Future[Done] = result.runForeach(i => println(i))
          done.onComplete(_ => system.terminate())
*/
  }


}