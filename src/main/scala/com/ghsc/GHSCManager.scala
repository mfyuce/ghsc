package com.ghsc

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCManager{
  def main(args: Array[String]) {
    GHSCLocationBasedTrainer.run()
    GHSCExporter.run()
    GHSCTimeWindowBasedTrainer.run()
    GHSCRouter.run()
  }
}
