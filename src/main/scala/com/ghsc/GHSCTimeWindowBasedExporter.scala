package com.ghsc

import com.ghsc.RouterUtil._
import com.ghsc.GHSCLocationBasedTrainer._

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCTimeWindowBasedExporter  {
  import GHSCExporter._;

  def main(args: Array[String]) {
    run
  }
  def run() {
    val files = RouterUtil.getListOfFiles(options.TXT_INSTANCE_FOLDER)

    for (file <- files) {
      val latestExportFolder = getLatestExportFolder(file,"location")
      val latestModelFolder = getLatestModelFolder(file,file)
        .replaceAllLiterally("/location","/tw")
        .replaceAllLiterally("\\location","\\tw");
//      val twBasedClusteredModels = getListOfFolders(latestModelFolder)
//      for (twBasedClusteredFile <- twBasedClusteredModels) {
        export(latestExportFolder.getAbsolutePath ,file,twTake,"tw")
//      }
    }
  }
  def currentTake(line: Array[Double]): Array[Double] = {
    twTake(line)
  }
}
