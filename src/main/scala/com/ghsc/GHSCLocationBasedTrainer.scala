package com.ghsc

import java.io.File
import java.util.Date

import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

import com.ghsc.RouterUtil._


/**
  * Created by myuce on 27.5.2016.
  */
object GHSCLocationBasedTrainer  {
  def main(args: Array[String]) {
    run
  }
  def run() {
    val files = RouterUtil.getListOfFiles(options.TXT_INSTANCE_FOLDER)

    for(file<-files){
      trainOne( file,file, currentTake)
    }
  }
  //basePath:String=options.TXT_INSTANCE_FOLDER,currentTake: (Array[Double]) => Array[Double], typeText:String="location"
  def trainOne(file:File,instanceFile:File,currentTake: (Array[Double]) => Array[Double], typeText:String="location") = {
    val sc = start()
    val trainingBase = DataSource.getFileDataFromInstanceFile(sc, file.getAbsolutePath)

    val training = trainingBase
      .map {
        line =>
          Vectors.dense(currentTake(line))
      }

    println("Running the K-Means clustering algorithm.")
    // Creates a new KMeans class which generates the KMeansModel
    val kMeansI = new KMeans()
    // Setting the parameters
    kMeansI.setK(if(typeText=="location"){options.numberOfLocationCenters}else{options.numberOfTWCenters})
    kMeansI.setMaxIterations(options.numberOfIterations)
    // Return the KMeansModel which we get after running the KMeans
    // algorithm on the data gathered by the DataSource component
    val model = kMeansI.run(training)
    val toWrite = if(typeText=="location"){
      new File(options.TXT_INSTANCE_FOLDER + "models/" +  file.getName + "/" + (new Date()).getTime + "/location/" );
    }else{
      val modelFolder = getLatestModelFolder(instanceFile,instanceFile,"location")

      new File(modelFolder
        .replaceAllLiterally("/location","/tw")
        .replaceAllLiterally("\\location","\\tw")
        + "/" + file.getName + "/" + (new Date()).getTime + "/");
    }
    toWrite.mkdirs()
    model.save(sc,toWrite.getAbsolutePath  )
    sc.stop()
  }
  def locationTake(line: Array[Double]): Array[Double] = {
    line.drop(1).take(2)
  }
  def twTake(line: Array[Double]): Array[Double] = {
    line.drop(4).take(2)
  }
  def twAndLocationTake(line: Array[Double]): Array[Double] = {
    line.drop(1).take(2).union(line.drop(4).take(2))
  }
  def currentTake(line: Array[Double]): Array[Double] = {
    locationTake(line)
  }
}
