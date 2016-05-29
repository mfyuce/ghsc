package com.ghsc

import java.io.File
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

import com.ghsc.RouterUtil._;
import com.ghsc.GHSCExporter._;

/**
  * Created by myuce on 27.5.2016.
  */
object GHSCRouter {
  def route(file: File, initialSol: VehicleRoutingProblemSolution): VehicleRoutingProblemSolution = {
    val vrpBuilder: VehicleRoutingProblem.Builder = VehicleRoutingProblem.Builder.newInstance
    new SolomonReader(vrpBuilder).read(file.getAbsolutePath)
    val vrp: VehicleRoutingProblem = vrpBuilder.build
    val vra: VehicleRoutingAlgorithm = VehicleRoutingAlgorithms.readAndCreateAlgorithm(vrp, "input/algorithmConfig_solomon.xml")
    vra.setMaxIterations(options.numberOfRotuingRun)
    vra.getAlgorithmListeners.addListener(new AlgorithmSearchProgressChartListener("output/sol_progress_" + file.getName + ".png"))
    if (initialSol != null) {
      vra.addInitialSolution(initialSol)
    }
    val solutions: Collection[VehicleRoutingProblemSolution] = vra.searchSolutions
    return new SelectBest().selectSolution(solutions)
    //SolutionPrinter.print(vrp, solution, SolutionPrinter.Print.VERBOSE)
  }
  def main(args: Array[String]) {
    run
  }
  def run() {
    Examples.createOutputFolder
    val files = RouterUtil.getListOfFiles(options.TXT_INSTANCE_FOLDER)

    for(file<-files){
      routeOne( file)
    }
  }
  def routeOne(file: File)= {
    val files = RouterUtil.getListOfFiles(getLatestExportFolder(file).getAbsolutePath)
    var totalCost = 0.0
    var totalJob = 0.0
    var lstSols = new ParHashMap[VehicleRoutingProblemSolution, Boolean]
    files.par.foreach { f =>
      val solution = route(f, null)
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
      RouterUtil.printSol(sol)
      uJobs.addAll(sol.getUnassignedJobs)
      for (r <- sol.getRoutes.toArray()) {
        val route = r.asInstanceOf[VehicleRoute]
        routes.add(route)
      }
    }

    println("Initial Total Cost : " + totalCost)
    println("Initial Total Unassgned Jobs : " + totalJob)

    val lastSol = new VehicleRoutingProblemSolution(routes,uJobs,totalCost)
    var sF = new File(options.TXT_INSTANCE_FOLDER + "schedules/" + file.getName + "/" );
    sF.mkdirs()
    sF = new File(sF.getAbsolutePath + "/" + file.getName+ "_" + totalCost + "km_" + totalJob + "uj" +  + (new Date()).getTime + ".sc");


    printSol(lastSol,sF)

    //    val solution=route (new File ("input/C110_1.TXT"), lastSol)
    //
    //    totalCost = solution.getCost
    //    totalJob = solution.getUnassignedJobs.size()
    //
    //    printSol(solution )
    //
    //    println("Total Cost : " + totalCost)
    //    println("Total Unassgned Jobs : " + totalJob)
  }
}
