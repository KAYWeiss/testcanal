package com.canal.models

case class Person(
  nconst: String,
  primaryName: String,
  birthYear: Option[Int],
  deathYear: Option[Int],
  primaryProfession: List[String]
  )
