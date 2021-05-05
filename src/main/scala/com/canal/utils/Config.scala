package com.canal.utils

import com.typesafe.config.ConfigFactory

object Config {
  private val config = ConfigFactory.load()
  private val DATA_FOLDER = config.getString("input-data.files.dataFolder")
  private def getFilePath(s: String) = {
    DATA_FOLDER + config.getString(s)
  }

  val TITLES_FILE = getFilePath("input-data.files.titles")
  val CREWMEMBERS_FILE = getFilePath("input-data.files.crewmembers")
  val PERSONS_FILE = getFilePath("input-data.files.persons")
  val EPISODES_FILE = getFilePath("input-data.files.episodes")

}
