package com.ghsc

import java.io.{IOException, _}
import java.nio.charset.Charset
import java.nio.file.Files
import java.util
import java.util.{Collection, Date}

import jsprit.analysis.toolbox.AlgorithmSearchProgressChartListener
import jsprit.core.algorithm.VehicleRoutingAlgorithm
import jsprit.core.algorithm.io.VehicleRoutingAlgorithms
import jsprit.core.algorithm.selector.SelectBest
import jsprit.core.problem.VehicleRoutingProblem
import jsprit.core.problem.job.Job
import jsprit.core.problem.solution.VehicleRoutingProblemSolution
import jsprit.core.problem.solution.route.VehicleRoute
import jsprit.instance.reader.SolomonReader
import jsprit.util.Examples

import scala.collection.parallel.immutable.ParHashMap
import com.ghsc.RouterUtil._
import com.ghsc.GHSCExporter._
import org.apache.spark.mllib.linalg.Vectors;
import scala.io
/**
  * Created by myuce on 27.5.2016.
  */
object GHSCRouter {
  def route(file: File, initialSol: VehicleRoutingProblemSolution,numberOFRuns:Int=options.numberOfRotuingRun): VehicleRoutingProblemSolution = {
    val vrpBuilder: VehicleRoutingProblem.Builder = VehicleRoutingProblem.Builder.newInstance
    new SolomonReader(vrpBuilder).read(file.getAbsolutePath)
    val vrp: VehicleRoutingProblem = vrpBuilder.build
    val vra: VehicleRoutingAlgorithm = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig_solomon.xml")
    vra.setMaxIterations(numberOFRuns)
    //vra.getAlgorithmListeners.addListener(new AlgorithmSearchProgressChartListener("output/sol_progress_" + file.getName + ".png"))
    if (initialSol != null) {
      vra.addInitialSolution(initialSol)
    }
    val solutions: Collection[VehicleRoutingProblemSolution] = vra.searchSolutions
    return new SelectBest().selectSolution(solutions)
    //SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE)
  }

  def main(args: Array[String]) {
    run("tw")
  }
  def run(typeText: String) {
    Examples.createOutputFolder
    val files = getListOfFiles(options.TXT_INSTANCE_FOLDER)

    for (file <- files) {
      routeOne(file, typeText)
    }
  }

  def routeOne(file: File, typeText: String) = {
    var latestExportFolder = getLatestExportFolder(file).getAbsolutePath


    val files = if (typeText == "tw") {
      latestExportFolder = latestExportFolder.replaceAllLiterally("/location", "/tw")
        .replaceAllLiterally("\\location", "\\tw");
      val folders = getListOfFolders(latestExportFolder)
      val fs = new util.LinkedList[File]()
      for (s <- folders) {
        getListOfFiles(getLatestFolder(s.getAbsolutePath).getAbsolutePath).foreach(fs.add(_))
      }
      fs.toArray()
    }
    else {
      getListOfFiles(latestExportFolder)
    }
    var totalCost = 0.0
    var totalJob = 0.0
    var lstSols = new ParHashMap[VehicleRoutingProblemSolution, Boolean]
    files.par.foreach { f =>
      val solution = route(f.asInstanceOf[File], null)
      totalCost += solution.getCost
      totalJob += solution.getUnassignedJobs.size()
      lstSols +=(solution, true)
    }

    println("solution ")
    val routes = new util.ArrayList[VehicleRoute]()
    val uJobs = new util.ArrayList[Job]()

    val alls = lstSols.map(_._1).toSeq.toArray
    for (s <- alls) {
      val sol = s.asInstanceOf[VehicleRoutingProblemSolution]
      //printSol(sol)
      uJobs.addAll(sol.getUnassignedJobs)
      for (r <- sol.getRoutes.toArray()) {
        val route = r.asInstanceOf[VehicleRoute]
        routes.add(route)
      }
    }

    println("Initial Total Cost : " + totalCost)
    println("Initial Total Unassgned Jobs : " + totalJob)

    val lastSol = new VehicleRoutingProblemSolution(routes, uJobs, totalCost)
    var basedir = new File(options.TXT_INSTANCE_FOLDER + "schedules/" + file.getName + "/");
    basedir.mkdirs()
    var sF = new File(basedir.getAbsolutePath + "/" + file.getName + "_" + totalCost + "km_" + totalJob + "uj_" + options.scheduleName +(new Date()).getTime +  ".sc");


    printSol(lastSol, sF)

    sF = new File(basedir.getAbsolutePath + "/" + file.getName + "_" + totalCost + "km_" + totalJob + "uj" + options.scheduleName +(new Date()).getTime + "_ropt.sc");
    val solution = route(file, lastSol,options.numberOfResultingRotuingRun)

    totalCost = solution.getCost
    totalJob = solution.getUnassignedJobs.size()

    //printSol(solution)
    printSol(lastSol, sF)
    println("Total Cost opt: " + totalCost)
    println("Total Unassgned Jobs opt: " + totalJob)
  }

  def exportBests() ={
    val instances = new File(options.TXT_INSTANCE_FOLDER + "schedules/" );
    val bestS =   getListOfFolders(instances.getAbsolutePath).map{
      instanceFolder=>
        val instance = getListOfFiles(instanceFolder.getAbsolutePath).map{
           f1 =>

             (f1.getName.replaceAllLiterally(instanceFolder.getName + "_","").split("[_]")(0).replaceAllLiterally ("km","").toDouble, f1,instanceFolder.getName)
        }.minBy(_._1)
        (instance._3, instance._1,io.Source.fromFile(instance._2.getAbsolutePath).getLines.size)
    }
        .sortBy(_._1)
    println("Instance\tKM\tRoutes")
    bestS.foreach{
      t=>
        print(t._1 + "\t")
        print(t._3 + "\t")
        print(t._2 + "\t")
        println()
    }
  }
}
