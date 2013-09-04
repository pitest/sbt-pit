sbtPlugin := true

name := "sbt-pit"

organization := "org.pitest.sbt"

version := "0.2-SNAPSHOT"

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies += "org.pitest" % "pitest" % "0.31"

publishTo <<= (version) { version: String =>
   val scalasbt = "http://repo.scala-sbt.org/scalasbt/"
   val (name, url) = if (version.contains("-SNAPSHOT"))
     ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
   else
     ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false
