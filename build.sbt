sbtPlugin := true

name := "sbt-pit"

organization := "org.pitest.sbt"

version := "0.1"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies += "org.pitest" % "pitest" % "0.31-SNAPSHOT"
