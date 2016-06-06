package com.ghsc

import java.io.{File, PrintWriter}
import java.nio.charset.Charset
import java.nio.file.Files
import java.util.Date
import com.ghsc.RouterUtil._;
import com.ghsc.GHSCManager._;

import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCExporter {
  def export(basePath:String=options.TXT_INSTANCE_FOLDER,baseFile: File,  currentTake: (Array[Double]) => Array[Double], typeText:String="location") = {
    val files = RouterUtil.getListOfFiles(basePath)

    for(file<-files){
      exportOne( baseFile, file,currentTake,typeText)
    }
  }
  def exportOne(baseFile: File,  clusterFile: File,currentTake: (Array[Double]) => Array[Double], typeText:String="location") ={
    val sc = start("GHSCExporter")
    val model = getLatestModel(sc,baseFile,clusterFile, typeText)
    val trainingBase = DataSource.getFileDataFromInstanceFile(sc, clusterFile.getAbsolutePath)

    val training = trainingBase
      .map {
        line =>
          Vectors.dense(currentTake(line))
      }
    val trainingIndexes = trainingBase
      .zipWithIndex()
      .map {
        line =>
          (line._2, line._1)
      }

    val groups = model.predict(training)
      .zipWithIndex()
      .map {
        line =>
          (line._2, line._1)
      }
      .join(trainingIndexes)
      .map {
        line =>
          (line._2._1, line._2._2.apply(0))
      }
      .groupBy(t => t._1);

    val toWrite = if(typeText=="location"){
      new File(options.TXT_INSTANCE_FOLDER + "/exports/" + clusterFile.getName + "/" + (new Date()).getTime + "/" + typeText + "/")
    }else{
      val exportFolder = getLatestExportFolder(baseFile,"location").getAbsolutePath

      new File(exportFolder
        .replaceAllLiterally("/location","/tw")
        .replaceAllLiterally("\\location","\\tw")
        + "/" + clusterFile.getName + "/" + (new Date()).getTime + "/");
    }

    val mappedTB = trainingBase.map{
      t=>
        (t(0),t)
    }.collect().filter(_._1!=0).toMap

    val firstLines = Files.readAllLines(clusterFile.toPath,Charset.defaultCharset()).toArray.take(10).map{
      line=>
        line.asInstanceOf[java.lang.String].replaceAllLiterally("  ", " ").trim
    }.mkString("\n")
    toWrite.mkdirs
    for (t <- groups) {
      val pw = new PrintWriter(new File(toWrite + "/" + t._1))
      pw.write(firstLines + "\n")
      for (u <- t._2.map(t => t._2).toSeq.toArray.filter(_!=0)) {
        pw.append( mappedTB(u).mkString(" ").replaceAllLiterally(".0","") +"")
        pw.append("\n")
      }
      pw.close
    }
    sc.stop()
  }

  def main(args: Array[String]) {
    run(options.TXT_INSTANCE_FOLDER,null)
  }
  def run(basePath:String=options.TXT_INSTANCE_FOLDER,baseFile:File, currentTake: (Array[Double]) => Array[Double]=GHSCLocationBasedTrainer.locationTake, typeTex:String="location") {
    export(basePath,baseFile,currentTake,typeTex)
  }
}

//
//object MovieLensALSTester {
//
//  def main(args: Array[String]) {
//    val sc =MovieLensALSTrainer.start("MovieLensALSTester")
//
//    val myRatings = loadSimpleRatings( )
//    val myRatingsRDD = sc.parallelize(myRatings, 1)
//
//    val training = DataSource.getFileData(sc,options.TXT_TRAIN_FILE)
//    val test = DataSource.getFileData(sc,options.TXT_TEST_FILE)
//    val validation = training.filter(x => x._1 >= 6 && x._1 < 8).values
//      //.cache()
//
//    val numTest = test.count()
//    val numTrain = training.count()
//    val numValidation = validation.count()
//
//    // train models and evaluate them on the validation set
//    val model =MovieLensALSTrainer.getModel(sc)
//    val validationRmse = computeRmse(model, validation, numValidation)
//    println("RMSE (validation) = " + validationRmse + " for the model trained with rank = "
//      + options.ranks + ", lambda = " + options.lambdas + ", and numIter = " + options.numIters + ".")
//
//
//    val movies = training.union(test).map { line =>
//      (line._2.product, line._2.product)
//    }.distinct().collect().toMap
//
//    println("Training: " + numTrain + ", validation: " + numValidation + ", test: " + numTest)
//
//    // evaluate the best model on the test set
//
//    val testRmse = computeRmse(model, test.values, numTest)
//
//    println("The best model was trained with rank = " + options.ranks + " and lambda = " + options.lambdas
//      + ", and numIter = " + options.numIters + ", and its RMSE on the test set is " + testRmse + ".")
//
//    // create a naive baseline and compare it with the best model
//    val meanRating = training.union(test).map(_._2.rating).mean
//    val baselineRmse =
//      math.sqrt(test.map(x => (meanRating - x._2.rating) * (meanRating - x._2.rating)).mean)
//    val improvement = (baselineRmse - testRmse) / baselineRmse * 100
//    println("The best model improves the baseline by " + "%1.2f".format(improvement) + "%.")
//
//    // make personalized recommendations
//
//    val myRatedMovieIds = myRatings.map(_.product).toSet
//    val candidatesMovies = sc.parallelize(movies.keys.filter(!myRatedMovieIds.contains(_)).toSeq)
//    val candidates = candidatesMovies.cartesian(myRatingsRDD).map{ line =>
//      (line._2.user,line._1)
//    }
//    val recommendations = model
//      .predict(candidates)
//      .collect()
//      .sortBy(- _.rating)
//      .take(50)
//
//    var i = 1
//    println("Movies recommended for you:")
//    recommendations.foreach { r =>
//      println("%2d".format(i) + ": " + movies(r.product))
//      i += 1
//    }
//    // clean up
//    sc.stop()
//  }
//
//  /** Compute RMSE (Root Mean Squared Error). */
//  def computeRmse(model: MatrixFactorizationModel, data: RDD[Rating], n: Long): Double = {
//    val predictions: RDD[Rating] = model.predict(data.map(x => (x.user, x.product)))
//    val predictionsAndRatings = predictions.map(x => ((x.user, x.product), x.rating))
//      .join(data.map(x => ((x.user, x.product), x.rating)))
//      .values
//    math.sqrt(predictionsAndRatings.map(x => (x._1 - x._2) * (x._1 - x._2)).reduce(_ + _) / n)
//  }
//
//  /** Load ratings from file. */
//  def loadRatings(path: String): Seq[Rating] = {
//    val lines = Source.fromFile(path).getLines()
//    val ratings = lines.map { line =>
//      val fields = line.split("::")
//      Rating(fields(0).toInt, fields(1).toInt, fields(2).toDouble)
//    }.filter(_.rating > 0.0)
//    if (ratings.isEmpty) {
//      sys.error("No ratings provided.")
//    } else {
//      ratings.toSeq
//    }
//  }
//
//  /** Load ratings from file. */
//  def loadSimpleRatings( ): Seq[Rating] = {
//    Seq(Rating(1612543, 7169565, 1))
//  }
//}
//