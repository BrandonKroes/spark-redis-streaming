import org.apache.spark.sql.SparkSession
import com.redislabs.provider.redis._

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.sql.types.{StructType, StructField, StringType, IntegerType}

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession, SaveMode, Row}
import org.apache.spark.sql.functions._
import com.github.mrpowers.spark.daria.sql.SparkSessionExt._
import java.time.LocalDateTime

object SparkRedisEntry {
  
  case class Entry(ID : String, generationTimestamp : String, arrivalTimestamp : String,  parsingTimestamp : String, insertTimestamp : String)

  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Requires the following params <hostname> <port> of the host server")
      System.exit(1)
    }

    val spark = SparkSession.builder.appName("SparkRedisEntry")
        // Instantiating the redis host. 
    // Each host has their own redis instance.
    .config("spark.redis.host", "localhost")
    .config("spark.redis.port", "6379")
    .getOrCreate()

    val ssc = new StreamingContext(spark.sparkContext, Seconds(1))

    // Setting up the connection for the socket to listen to.
    val lines = ssc.socketTextStream(args(0), args(1).toInt, StorageLevel.MEMORY_ONLY_SER)
    
    // When the entry enters a spark instance for the first time.
    val arrivalTimestamp = LocalDateTime.now().toString()

    lines.foreachRDD(rdd =>
        if(!rdd.isEmpty()){
          // Second timestamp pre-parsing
          val parsingTimestamp = LocalDateTime.now().toString()
           
          // Example: 123,2022-10-18-18:30:24:241 becomes two seperate values.
          val splitValues = rdd.first.split(",")
          if (splitValues.length == 2){          
          val id = splitValues(0)
          val generationTimestamp = splitValues(1)

          // Combining all timestamps into a Scala Collection.
          val entrySeq = Seq(Entry(id, generationTimestamp, arrivalTimestamp, parsingTimestamp, LocalDateTime.now().toString()))
          
          // Collection gets cast to a DataFrame and written to redis.
          spark.createDataFrame(entrySeq)
          .write
          .format("org.apache.spark.sql.redis")
          .option("table", "entry")
          .mode(SaveMode.Append)
          .save();
          }
        }
    )
    ssc.start()
    ssc.awaitTermination()
  }
}
