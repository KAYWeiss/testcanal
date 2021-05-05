package com.canal.models

case class Episode(
  tconst: String,
  parentTconst: String,
  seasonNumber: Option[Int],
  episodeNumber: Option[Int]
)
