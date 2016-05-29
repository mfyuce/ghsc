package com.ghsc

import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.rdd.RDD

/**
 * Created by myuce on 11.5.2016.
 */
object DataSource {
  def getFileData(sc: SparkContext, file: String, separator: String = "[ \t]"): RDD[Array[Double]] = {
    sc.textFile(file)
      .map { line =>
        line.split(separator)
          .map {
            field => field.toDouble
          }.toSeq.toArray
      }
  }

  def getFileDataFromInstanceFile(sc: SparkContext, file: String, separator: String = "[ ]"): RDD[Array[Double]] = {
    sc.textFile(file)
      .zipWithIndex()
        .filter(_._2>9)
        .map(_._1)
        .map(_.replaceAllLiterally("  "," "))
      .filter(_!=null)
      .filter(_.trim.length>0)
      .map { line =>
        line.split(separator).filter(_.trim.length>0)
          .map {
            field => field.toDouble
          }.toSeq.toArray
      }
  }

  def getFileDataFromInstanceText(file: String): Array[Array[Double]] = {

    file.split("[\n]")
        .drop(9)
      .filter (!_.trim.isEmpty())
      .map { line =>
      line.replaceAllLiterally("  ", " ")
        .split("[ ]")
        .map {
          field => field.toDouble
        }.toSeq.toArray
    }
  }
}

