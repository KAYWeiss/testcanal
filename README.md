# testcanal

## Short Presentation
This modules aims at retrieving IMDb datasets and parse the data in order to fill 2 queries :
 - As a user, I would like to be able to get a movie's list of crew members (Principals), with its name.
 - As a user, I would like to be able to get a top  ten TV series based on their number of episodes.
The Implementation is based on the library akka-stream to read files and process them.

## Requirements
openjdk >= 1.8
sbt >= 1.x

## Setup
Before using the code for the queries, the files should be downloaded thanks to the `get_imdb_data.sh` script.

## Run
To retrieve the principals from a movie (e.g. Carmencita):

`sbt "run movie Carmencita"`

To retrieve the top 10 series :

`sbt "run tvSeries"`

To run the tests :

`sbt test`

