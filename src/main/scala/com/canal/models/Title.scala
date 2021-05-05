package com.canal.models

case class Title(
  tconst: String,
  titleType: String,
  primaryTitle: String,
  originalTitle: String,
  startYear: Option[Int],
  endYear: Option[Int],
  genres: List[String]
)
