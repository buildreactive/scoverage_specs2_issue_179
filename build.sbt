
name := "IDC"

version := "1.0"

lazy val `idc` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.url("scoverage-bintray", url("https://dl.bintray.com/sksamuel/sbt-plugins/"))(Resolver.ivyStylePatterns)

scalacOptions in Test ++= Seq("-Yrangepos")

coverageEnabled := true

coverageMinimum := 80

coverageFailOnMinimum := true

lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

libraryDependencies ++= {
	Seq(
		"com.trueaccord.scalapb" %% "scalapb-runtime" % "0.4.20",
		"org.scodec" %% "scodec-core" % "1.10.2",
		"com.wix" % "accord-core_2.11" % "0.5",
		"org.specs2" %% "specs2-core" % "3.8.4" % "test",
		"org.specs2" %% "specs2-scalacheck" % "3.8.4" % "test",
		"org.specs2" %% "specs2-junit" % "3.8.4" % "test"
	)
}

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )

coverageExcludedPackages :=
	"""<empty>;
	  |connectors.*;
	  |controllers.javascript.*;
	  |router.*;
	  |views.*;
	  |utility.Log.*
	  |utility.CustomBodyParser""".stripMargin

scalacOptions ++= Seq(
	"-feature",
	"-deprecation",
	"-unchecked",
	"-language:postfixOps",
	"-language:reflectiveCalls",
	"-language:implicitConversions",
	"-Xlint",
	"-Xelide-below",
	"MINIMUM"
	// "-encoding", "UTF-8",      // yes, this is 2 args
	//	"-Xfatal-warnings",
	//	"-Yno-adapted-args",
	//	"-Ywarn-dead-code",        // N.B. doesn't work well with the ??? hole
	//	"-Ywarn-value-discard",
	//	"-Xfuture"
)
