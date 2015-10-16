enablePlugins(ScalaJSPlugin)

name := """imageToNeopixels"""

version := "1.0"

scalaVersion := "2.11.7"

// Change this to another test framework if you prefer
//libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

// Uncomment to use Akka
//libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.11"

libraryDependencies ++= Seq(
		"org.scala-js" %%% "scalajs-dom" % "0.8.0",
		"com.lihaoyi" %%% "scalatags" % "0.5.2",
          "com.github.japgolly.scalacss" %%% "core" % "0.3.0",
           "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.3.0",
		 "com.greencatsoft" %%% "scalajs-angular" % "0.6-SNAPSHOT")

        jsDependencies ++= Seq(
            "org.webjars" % "angularjs" % "1.4.3" / "angular.js",
            "org.webjars" % "angular-material" % "0.10.1" / "angular-material.min.js")
            //"org.webjars" % "angularjs" % "1.4.3" / "angular-resource.js"
            //"org.webjars" % "angularjs" % "1.4.3" / "angular-route.js"
