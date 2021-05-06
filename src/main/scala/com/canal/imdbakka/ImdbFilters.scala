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
import Utils._

object ImdbFilters {

    implicit val system = ActorSystem("MovieService")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
    
    def getMovieFromName(name: String)= {
       Flow[Title]
            .filter(t => t.titleType == "movie")
            .filter(t =>t.originalTitle == name || t.primaryTitle == name)
            .map(t => t.tconst)
    }
    
    def getCrewMembersFromMovie(tconst: Future[Option[String]]): Flow[CrewMember, String, _] = {
        asyncFilter[CrewMember](cm =>futureValueToBoolean(tconst, cm.tconst))
        .map(cm => cm.nconst)
    }

    def filterPersonOnCrewMembers(crewMembers: Future[Seq[String]]) = {
        asyncFilter[Person](p => futureSeqToBoolean(crewMembers, p.nconst))
    }


    def countEpisodeNumber: Flow[Episode, (String, Int), _] = {
        return Flow[Episode]
        .map(e => e.parentTconst)
        .groupBy(Integer.MAX_VALUE, identity)
        .map(_ -> 1)
        .reduce((l, r) => (l._1, l._2 + r._2))
        .mergeSubstreams
    }

    def topElements(nbOfElements: Int) = {
      Flow[(String, Int)].concat(Source.single(("-end-", 0))).statefulMapConcat { () =>
        var stashedElements: List[(String, Int)] = Nil

        { element =>
          if (stashedElements.size == 0) {
            stashedElements = element :: stashedElements
            Nil
          } else if (element._1.equals("-end-")) {
            stashedElements
          } else {
            stashedElements = insertToList(stashedElements, element).take(nbOfElements)
            Nil
          }
        }
      }.map(e=>e._1)
    }

    def filterTitleOnEpisodes(episodes: Future[Seq[String]]) = {
        asyncFilter[Title](t => futureSeqToBoolean(episodes, t.tconst))
    }

    def asyncFilter[T](filter: T => Future[Boolean], parallelism : Int = 1)
                  (implicit ec : ExecutionContext) : Flow[T, T, _] =
      Flow[T].mapAsync(parallelism)(t => filter(t).map(_ -> t))
         .filter(_._1)
         .map(_._2)

}