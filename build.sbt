
name := """Ember"""
//organization := "com.example"

PlayKeys.devSettings := Seq("play.server.http.port" -> "9001")

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

scalaVersion := "2.11.12"
crossScalaVersions := Seq("2.11.12", "2.12.4")
//crossScalaVersions := Seq("2.11.12", "2.12.8")
//scalaVersion := "2.11.12"
//crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += javaForms

// Authentication
libraryDependencies += "be.objectify" %% "deadbolt-java" % "2.6.1"
libraryDependencies += "com.feth" %% "play-authenticate" % "0.8.3"
libraryDependencies += "be.objectify" %% "deadbolt-java-gs" % "2.6.0"
libraryDependencies += "commons-io" % "commons-io" % "2.5"


//libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.10.0"

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.196"

libraryDependencies += javaJdbc
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1206-jdbc42"

//DbUnit testing
//libraryDependencies += "org.dbunit" % "dbunit" % "2.4.8" % Test
libraryDependencies += "junit" % "junit" % "4.10" % Test
libraryDependencies += "org.hibernate" % "hibernate-entitymanager" % "4.3.0.Final"
libraryDependencies += "org.hibernate" % "hibernate-core" % "3.5.6-Final"
libraryDependencies += "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.0.Final"

libraryDependencies += "com.squareup.okhttp3" % "okhttp" % "3.7.0"