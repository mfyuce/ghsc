package com.ghsc

/**
  * Created by myuce on 27.5.2016.
  */
class GeneralOptions {
  var TXT_INSTANCE_FOLDER: String = "data/homberger_1000_customer_instances/"
  var numberOfLocationCenters =5
  var numberOfTWCenters =2
  var numberOfRoutingRun =  100000
  var numberOfResultingRoutingRun = 1
  var numberOfIterations = 5000
  var scheduleName:String=numberOfLocationCenters+"loc_"+ numberOfTWCenters + "tw_"
}
object R1Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/R1/"
  numberOfLocationCenters =3
  numberOfTWCenters =3
  numberOfRoutingRun = 33000
  numberOfResultingRoutingRun = 256
  numberOfIterations = 5000
}
object R2Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/R2/"
  numberOfLocationCenters =5
  numberOfTWCenters =2
  numberOfRoutingRun =  100000
  numberOfResultingRoutingRun = 1
  numberOfIterations = 5000
}
object C1Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/C1/"
  numberOfLocationCenters =5
  numberOfTWCenters =2
  numberOfRoutingRun =  100000
  numberOfResultingRoutingRun = 1
  numberOfIterations = 5000
}
object C2Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/C2/"
  numberOfLocationCenters =5
  numberOfTWCenters =2
  numberOfRoutingRun =  100000
  numberOfResultingRoutingRun = 1
  numberOfIterations = 5000
}
object RC1Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/RC1/"
  numberOfLocationCenters =5
  numberOfTWCenters =2
  numberOfRoutingRun =  100000
  numberOfResultingRoutingRun = 1
  numberOfIterations = 5000
}
object RC2Options  extends GeneralOptions{
  TXT_INSTANCE_FOLDER = "data/homberger_1000_customer_instances/RC2/"
  numberOfLocationCenters =5
  numberOfTWCenters =2
  numberOfRoutingRun =  100000
  numberOfResultingRoutingRun = 1
  numberOfIterations = 5000
}

