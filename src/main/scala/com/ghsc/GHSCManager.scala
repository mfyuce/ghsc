package com.ghsc

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCManager{
  def main(args: Array[String]) {
    GHSCLocationBasedTrainer.run()
    GHSCExporter.run(options.TXT_INSTANCE_FOLDER,null)
    GHSCTimeWindowBasedTrainer.run()
    GHSCTimeWindowBasedExporter.run()
    GHSCRouter.run("tw")
  }
}
