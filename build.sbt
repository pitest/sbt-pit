sbtPlugin := true

name := "sbt-pit"

organization := "org.pitest.sbt"

version := "1.0.0-SNAPSHOT"

crossScalaVersions := Seq("2.9.2", "2.10.1", "2.11.0")

resolvers += "Local Maven" at Path.userHome.asFile.toURI.toURL + ".m2/repository"

libraryDependencies += "org.pitest" % "pitest" % "1.0.0"

libraryDependencies += "org.pitest" % "pitest-html-report" % "1.0.0"

publishTo <<= (version) { version: String =>
   val scalasbt = "http://repo.scala-sbt.org/scalasbt/"
   val (name, url) = if (version.contains("-SNAPSHOT"))
     ("sbt-plugin-snapshots", scalasbt+"sbt-plugin-snapshots")
   else
     ("sbt-plugin-releases", scalasbt+"sbt-plugin-releases")
   Some(Resolver.url(name, new URL(url))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false

