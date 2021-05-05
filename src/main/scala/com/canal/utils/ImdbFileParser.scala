package com.canal.fileparser

import akka.stream._
import akka.stream.scaladsl._
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import java.nio.file.Paths
import com.canal.config.Config._
import com.canal.models._
object ImdbFileParser {
  
    private val delimiter: Byte = '\t'
    private val quoteChar: Byte = 'ˆ'.toByte
    private val escapeChar: Byte = '|'
    private val csvLineScanner = CsvParsing.lineScanner(delimiter=delimiter, quoteChar=quoteChar, escapeChar=escapeChar)

    def parseFile(path: String): Source[Map[String, String], _] = {
        FileIO.fromPath(Paths.get(path))
            .via(csvLineScanner)
            .via(CsvToMap.toMapAsStrings())
    }

    def castTitle(title: Map[String, String]): Title = {
        Title(
            tconst = title("tconst"),
            titleType = title("titleType"),
            primaryTitle = title("primaryTitle"),
            originalTitle = title("originalTitle"),
            startYear = title("startYear").toIntOption,
            endYear = title("endYear").toIntOption,
            genres = title("genres").split(",").toList
        )
    }

      def castPerson(person: Map[String, String]): Person = {
        Person(
            nconst = person("nconst"),
            primaryName = person("primaryName"),
            birthYear = person("birthYear").toIntOption,
            deathYear = person("deathYear").toIntOption,
            primaryProfession = person("primaryProfession").split(",").toList
        )
    }

    def castCrewMember(crewMember: Map[String, String]): CrewMember = {
        CrewMember(
            tconst = crewMember("tconst"),
            nconst = crewMember("nconst")
        )
    }

    def castEpisode(episode: Map[String, String]): Episode = {
        Episode(
            tconst = episode("tconst"),
            parentTconst = episode("parentTconst"),
            seasonNumber = episode("seasonNumber").toIntOption,
            episodeNumber = episode("episodeNumber").toIntOption
        )
    }

}
