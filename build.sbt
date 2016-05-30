import AssemblyKeys._

assemblySettings

name := "ghsc"

version := "0.1"

scalaVersion := "2.10.5"

resolvers +=
  "JSprit Releases" at "https://github.com/jsprit/mvn-rep/raw/master/releases"
resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
"org.apache.spark" %% "spark-core"    % "1.6.1" ,
  "org.apache.spark" %% "spark-mllib"   % "1.6.1"
)


libraryDependencies += "jsprit" % "jsprit-core" % "1.6.2"
libraryDependencies += "jsprit" % "jsprit-analysis" % "1.6.2"
libraryDependencies += "jsprit" % "jsprit-instances" % "1.6.2"
libraryDependencies += "jsprit" % "jsprit-examples" % "1.6.2"

