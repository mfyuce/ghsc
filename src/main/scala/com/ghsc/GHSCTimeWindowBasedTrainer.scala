package com.ghsc

import com.ghsc.RouterUtil._
/**
  * Created by myuce on 27.5.2016.
  */
object GHSCTimeWindowBasedTrainer  {
  import GHSCLocationBasedTrainer._;

  def main(args: Array[String]) {
    run
  }
  def run() {
    val files = RouterUtil.getListOfFiles(options.TXT_INSTANCE_FOLDER)

    for (file <- files) {
      val locationBasedClusteredFiles = RouterUtil.getListOfFiles(getLatestExportFolder(file).getAbsolutePath)
      for (locationBasedClusteredFile <- locationBasedClusteredFiles) {
          trainOne(locationBasedClusteredFile,file,currentTwTake,"tw")
      }


    }
  }
}
