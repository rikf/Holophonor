name := "holophonor"


version := "0.1"


scalaVersion := "2.10.3"


libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"


libraryDependencies += "junit" % "junit" % "4.11" % "test"


packageOptions in (Compile, packageBin) +=   Package.ManifestAttributes( "Premain-Class" -> "holophonor.weave.Agent",
  "Can-Redefine-Classes" -> "true" )