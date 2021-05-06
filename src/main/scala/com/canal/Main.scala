package  com.canal

import akka.stream._
import akka.stream.scaladsl._
import akka.actor.ActorSystem
import scala.concurrent.{ExecutionContext, Await, Future}
import scala.concurrent.duration._
import com.canal.imdbakka._
import akka.Done
import scala.util.{Success, Failure}

object Main extends App {

    implicit val system = ActorSystem("MovieService")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val ec: ExecutionContext = ExecutionContext.global

    
    private val usage = """
        |Available commands: 
        |   - sbt run tvSeries 
        |   - sbt run movie movieName
    """.stripMargin


    def printTopTvSeries(movieService: AkkaMovieService): Future[Done] = {
        val topTvSeriesSource = movieService.tvSeriesWithGreatestNumberOfEpisodes()

        println("Top 10 Tv Series with the most episodes of all times:")
        topTvSeriesSource.runForeach(println)

    }

    def printPrincipalsForMovie(movieService: AkkaMovieService, movieName: String): Future[Done] = {
        val principalsFromMovie = movieService.principalsForMovieName(movieName)

        println(s"Principals from $movieName:")
        principalsFromMovie.runForeach(println)
    }

    def printUsage(): Future[Done] = {
        Source.single(usage).runForeach(println)
    }

    val movieService = new AkkaMovieService()

    val futureResult = args match {
        case Array("tvSeries") => printTopTvSeries(movieService)
        case Array("movie", movieName) =>  printPrincipalsForMovie(movieService, movieName)
        case _ => printUsage()
    }

    futureResult.onComplete {
        case Success(value) => {
            println(value)
            system.terminate()
        }
        case Failure(exception) => {
            exception.printStackTrace(System.out)
            system.terminate()
        }
    }
    
}