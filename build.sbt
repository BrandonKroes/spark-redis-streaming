name := "Spark Redis Entry"
version := "1.0"
scalaVersion := "2.12.15"


libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % "3.3.0",
    "org.apache.spark" %% "spark-streaming" % "3.3.0" % "provided",
    "com.redislabs" %% "spark-redis" % "2.4.2",
    "com.github.mrpowers" %% "spark-daria" % "0.38.2"
) 