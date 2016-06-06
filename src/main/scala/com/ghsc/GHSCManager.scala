package com.ghsc

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCManager{
  def setOptions(dataSet:String) {
    options = dataSet match {
    case "C1" => C1Options
    case "C2" => C2Options
    case "R1" => R1Options
    case "R2" => R2Options
    case "RC1" => RC1Options
    case "RC2" => RC2Options
  }
  }
  var options:GeneralOptions=new GeneralOptions()

  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir", "C:\\projects\\github\\hadoop-common-2.2.0-bin-master\\hadoop-common-2.2.0-bin-master\\")
//    setOptions("C1")
//    runOne()
//
//    setOptions("C2")
//    runOne()

    setOptions("R1")
    runOne()

//    setOptions("R2")
//    runOne()
//
//    setOptions("RC1")
//    runOne()
//
//    setOptions("RC2")
//    runOne()
  }
  def runOne() {
    GHSCLocationBasedTrainer.run()
    GHSCExporter.run(options.TXT_INSTANCE_FOLDER,null)
    GHSCTimeWindowBasedTrainer.run()
    GHSCTimeWindowBasedExporter.run()
    GHSCRouter.run("tw")
  }
}
