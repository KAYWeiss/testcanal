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
import org.scalatest.funsuite.AnyFunSuite
import imdbakka.ImdbFilters._
import com.canal.models._
import fileparser.ImdbFileParser

class ImdbFiltersSpec extends AnyFunSuite {

    implicit val system = ActorSystem("MovieService")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = ExecutionContext.global
    val titles = ImdbFileParser.parseFile(TITLES_FILE)
    .map(ImdbFileParser.castTitle(_))
    val persons = ImdbFileParser.parseFile(PERSONS_FILE)
    .map(ImdbFileParser.castPerson(_))
    val crewmembers = ImdbFileParser.parseFile(CREWMEMBERS_FILE)
    .map(ImdbFileParser.castCrewMember(_))
    val episodes = ImdbFileParser.parseFile(EPISODES_FILE)
    .map(ImdbFileParser.castEpisode(_))

    test("AkkaFilters should return tconst from movie Name") {
        //val episodeCounts = episodes.via(countEpisodeNumber)
        //val topEpisodes = episodeCounts.via(topElements(2)).runWith(Sink.seq)


        //val result = titles.via(asyncFilter(t => topEpisodes.map(_.contains(t.tconst))))

        val resultMovie = titles
            .via(getMovieFromName("The Man Inside"))
            .runWith(Sink.headOption)

        val tconst = Await.result(resultMovie, 3.seconds)
        val expected = Some("tt0007015")

          assert(tconst == expected) 
    }

    test("AkkaFilters should return Empty nconsts crewMembers from unexisting tconst"){
      val tconst = Future(Some("t53"))

      val nconsts = crewmembers
      .via(getCrewMembersFromMovie(tconst))
      .runWith(Sink.seq)

      val resultNconsts = Await.result(nconsts, 3.seconds)

      val expected = Seq.empty
      assert(resultNconsts == expected)
    }
    
    test("AkkaFilters should return nconsts crewMembers from existing tconst"){
      val tconst = Future(Some("tt0007015"))

      val nconsts = crewmembers
      .via(getCrewMembersFromMovie(tconst))
      .runWith(Sink.seq)

      val resultNconsts = Await.result(nconsts, 3.seconds)

      val expected = Seq("nm0102718", "nm0828381", "nm0551212", "nm0120763", "nm0400212")
      assert(resultNconsts == expected)
    }

    test("AkkaFilters should return empty Seq from unexisting nconsts"){

      val nconsts = Future(Seq("n2", "n1"))

      val personNames = persons
      .via(filterPersonOnCrewMembers(nconsts)) 
      .runWith(Sink.seq)

      val resultPersons = Await.result(personNames, 3.seconds)
      val expected = Seq.empty
      assert(resultPersons == expected)
    }
    test("AkkaFilters should return Persons Seq from nconsts"){

      val nconsts = Future(Seq("nm0102718", "nm0828381", "nm0551212", "nm0120763", "nm0400212"))

      val personNames = persons
      .via(filterPersonOnCrewMembers(nconsts)) 
      .runWith(Sink.seq)

      val resultPersons = Await.result(personNames, 3.seconds)
      val expected = Seq(Person("nm0102718","Sidney Bracey",Some(1877),Some(1942),List("actor", "director", "soundtrack")),
        Person("nm0828381","Edwin Stevens",Some(1860),Some(1923),List("actor", "director", "writer")),
        Person("nm0551212","Tina Marshall",Some(1883),Some(1980),List("actress")),
        Person("nm0120763","Charles Burbridge",Some(1849),Some(1922),List("actor")),
        Person("nm0400212","Justina Huff",Some(1893),Some(1977),List("actress")))
      assert(resultPersons == expected)
    }

}