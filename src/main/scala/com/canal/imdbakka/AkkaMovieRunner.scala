package com.canal.imdbakka

import akka.stream._
import akka.stream.scaladsl._
import akka.actor.ActorSystem
import java.nio.file.Paths
import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.util.{Success, Failure}
import com.canal.config.Config._
import com.canal.models._
import com.canal.fileparser.ImdbFileParser

final case class Principal(
  name: String,
  birthYear: Option[Int],
  deathYear: Option[Int],
  profession: List[String]
)
final case class TvSeries(
    original: String,
    startYear: Option[Int],
    endYear: Option[Int],
    genres: List[String]
  )
class AkkaMovieRunner(implicit system: ActorSystem, materializer: ActorMaterializer, ec: ExecutionContext){
   
    def principalsForMovieName(name: String) = {
        val titles = ImdbFileParser.parseFile(TITLES_FILE)
        .map(castTitle(_))
        val persons = ImdbFileParser.parseFile(PERSONS_FILE)
        .map(castPerson(_))
        val crewmembers = ImdbFileParser.parseFile(CREWMEMBERS_FILE)
        .map(castCrewMember(_))
        
        val tconst = titles
            .via(getMovieFromName(name))
            .runWith(Sink.headOption)
        
        val nconsts = crewmembers
            .via(getCrewMembersFromMovie(tconst))
            .runWith(Sink.seq)
        
        persons
            .via(filterPersonOnCrewMembers(nconst))
            .map(p => Principal(
                p.primaryName, 
                p.birthYear, 
                p.deathYear, 
                p.primaryProfession))
    }

    def topTvSeriesWithGreatestNumberOfEpisodes(topNumber: Int) = {
        val titles = ImdbFileParser.parseFile(TITLES_FILE)
            .map(castTitle(_))
        
            val episodes = ImdbFileParser.parseFile(EPISODES_FILE)
            .map(castEpisode(_))

        val topEpisodes = episodes
            .via(countEpisodeNumber)
            .via(topElements(topNumber))
            .runWith(Sink.seq)

        titles
            .via(filterTitleOnEpisodes(topEpisodes))
            .map(t => TvSeries(
                t.originalTitle,
                t.startYear,
                t.endYear,
                t.genres))
        }

        def tvSeriesWithGreatestNumberOfEpisodes() = {
            topTvSeriesWithGreatestNumberOfEpisodes(10)
        }
}
