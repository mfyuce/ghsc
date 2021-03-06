package com.ghsc

import java.io.{File, PrintWriter}

import jsprit.core.problem.solution.VehicleRoutingProblemSolution
import jsprit.core.problem.solution.route.VehicleRoute
import jsprit.core.problem.solution.route.activity.TourActivity
import org.apache.log4j.{Level, Logger}
import org.apache.spark.mllib.clustering.KMeansModel
import org.apache.spark.{SparkConf, SparkContext}
import com.ghsc.GHSCManager._

/**
  * Created by myuce on 25.5.2016.
  */
object RouterUtil {
  def getListOfFiles(dir: String): Array[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isFile)
    } else {
      null
    }
  }

  def getListOfFolders(dir: String): Array[File] = {
    val d = new File(dir)
    if (d.exists && d.isDirectory) {
      d.listFiles.filter(_.isDirectory)
    } else {
      null
    }
  }
  def printSol(sol: VehicleRoutingProblemSolution) = {
    sol.getRoutes.toArray().foreach { s =>
      print("0")
      val r = s.asInstanceOf[VehicleRoute]
      r.getActivities.toArray().foreach { t =>
        print("," + t.asInstanceOf[TourActivity].getLocation.getId)
      }
      println()
    }
  }
  def printSol(sol: VehicleRoutingProblemSolution, file: File) = {
    val pw = new PrintWriter( file )
    sol.getRoutes.toArray().foreach { s =>
      pw.write("0")
      val r = s.asInstanceOf[VehicleRoute]
      r.getActivities.toArray().foreach { t =>
        pw.write("," + t.asInstanceOf[TourActivity].getLocation.getId)
      }
      pw.write("\n")
    }
    pw.close()
  }
  def start(appName:String="GHSC"):SparkContext = {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    // set up environment
    val conf = new SparkConf()
      .setAppName(appName)
      //      .set("spark.executor.memory", "2g")
      //      .set("spark.master", "spark://organon-dev-01:7077")
      .set("spark.master", "local")
    //         .set("fs.defaultFS", "hdfs://organon-dev-01:9000")
    //      .set("spark.local.dir", "D:/try")
    //        .set("spark.externalBlockStore.baseDir", "D:/try")
    //          .set("spark.eventLog.dir", "D:/try")
    new SparkContext(conf)
  }
  def getLatestModel(sc:SparkContext, baseFile: File,  clusterFile: File, typeText:String="location"): KMeansModel ={
    KMeansModel.load(sc, getLatestModelFolder( baseFile,  clusterFile, typeText)  )
  }
  def getLatestModelFolder(baseFile: File, clusterFile: File, typeText:String="location"): String ={
   var bf = baseFile
    if(bf==null){
      bf = clusterFile
    }
    val instanceName =bf.getName.toLowerCase
    var d = new File(options.TXT_INSTANCE_FOLDER + "models/" + instanceName )
    var latest = {
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isDirectory)
          //.filter(_.getName.toLowerCase.startsWith(instanceName))
          //.filter(_.getName.length>instanceName.length)
          .map {
          f =>
            //(f, f.getName.toLowerCase.replaceAllLiterally(instanceName,"").toDouble)
            (f, f.getName.toDouble)
        }.maxBy(_._2)
      } else {
        null
      }
    }
    var latestToRet = latest._1.getAbsolutePath + "/" + typeText
    if(typeText=="tw"){
      d = new File(latestToRet + "/" + clusterFile.getName )
      latest = {
        if (d.exists && d.isDirectory) {
          d.listFiles.filter(_.isDirectory)
            //.filter(_.getName.toLowerCase.startsWith(instanceName))
            //.filter(_.getName.length>instanceName.length)
            .map {
            f =>
              //(f, f.getName.toLowerCase.replaceAllLiterally(instanceName,"").toDouble)
              (f, f.getName.toDouble)
          }.maxBy(_._2)
        } else {
          null
        }
      }
      latestToRet = latest._1.getAbsolutePath + "/"
    }
    latestToRet
  }
  def getLatestFolder(fileLoc:String): File ={
    val d = new File(fileLoc)
    val latest = {
      if (d.exists && d.isDirectory) {
        d.listFiles.filter(_.isDirectory)
          //.filter(_.getName.toLowerCase.startsWith(instanceName))
          //.filter(_.getName.length>instanceName.length)
          .map {
          f =>
            //(f, f.getName.toLowerCase.replaceAllLiterally(instanceName,"").toDouble)
            (f, f.getName.toDouble)
        }.maxBy(_._2)
      } else {
        null
      }
    }
    latest._1
  }
  def getLatestExportFolder( file: File, typeText:String="location"): File ={
    val instanceName =file.getName.toLowerCase
    val d = new File(options.TXT_INSTANCE_FOLDER + "exports/" + instanceName )
    val latest = {
      if (d.exists && d.isDirectory) {
        d.listFiles
          //.filter(_.isDirectory).filter(_.getName.toLowerCase.startsWith(instanceName))
        .map {
          f =>
            (f, f.getName.toDouble)
        }.maxBy(_._2)
      } else {
        null
      }
    }
    new File(latest._1.getAbsolutePath + "/" + typeText)
  }

  def setHadoopHomeDir() ={
    //System.setProperty("hadoop.home.dir", "C:\\projects\\github\\hadoop-common-2.2.0-bin-master\\hadoop-common-2.2.0-bin-master\\")

  }

}