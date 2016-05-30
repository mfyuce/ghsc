package com.ghsc

/**
  * Created by myuce on 27.5.2016.
  */
object options {
  val TXT_TRAIN_FILE: String = "data/c110_1.txt"
  val TXT_INSTANCE_FOLDER: String = "data/homberger_1000_customer_instances/"
  val numberOfLocationCenters =4
  val numberOfTWCenters =3
  val numberOfRotuingRun = 33000
  val numberOfIterations = 5000
  val TXT_MODEL_FILE_PREFIX: String = "model/model_"
  val TXT_TW_MODEL_FILE_PREFIX: String = "model/model_tw_"
  val TXT_MODEL_FILE: String = TXT_MODEL_FILE_PREFIX + "1464108791427"
  val TXT_EXPORT_FILE: String = TXT_MODEL_FILE_PREFIX + "export_" + "1464108816220"
  val scheduleName:String="l"+numberOfLocationCenters+"_tw"+numberOfTWCenters
}
