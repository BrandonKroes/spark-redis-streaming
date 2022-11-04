# Spark - Redis benchmarking use Spark Stream

## Clone instruction
Important, clone using `git clone --recursive` other wise the submodules won't be downloaded.

## Requirements
- [Spark Redis](https://github.com/RedisLabs/spark-redis)
- [Scala 2.12](https://www.scala-lang.org/)
- [SBT](https://www.scala-sbt.org/)

## Inner-workings
- A host ip and port are given for the data-generator
- A socket is opened.
- A timestamp is made when the first package arrives.
- A timestamp when it gets split into the relevant fields
- A timestamp is made when the package is ready for database storage
- All timestamps get combined into a collection and stored into a DB.

## Install
- The submodule can be compiled using `mvn clean package`
- Compile using `sbt package`. The .jar file will be available in `/target/scala-2.12/spark-redis-entry_2.12.1.0.jar`

## Executables
- Copy the `.jars` to the `executables/` directory, if you don't the new version of the program can't be run.


