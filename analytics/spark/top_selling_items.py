from pyspark.sql import SparkSession
from pyspark.sql.functions import col, sum as sum_, when

# Demo analytics job using exported inventory log CSV/parquet from HDFS.
spark = SparkSession.builder.appName("InventoryAnalyticsTopSelling").getOrCreate()

input_path = "hdfs://namenode:9000/data/inventory_logs.csv"
df = spark.read.option("header", True).csv(input_path)

result = (
    df.withColumn("quantity_changed", col("quantity_changed").cast("int"))
      .withColumn("sold_units", when(col("quantity_changed") < 0, -col("quantity_changed")).otherwise(0))
      .groupBy("item_id")
      .agg(sum_("sold_units").alias("total_sold"))
      .orderBy(col("total_sold").desc())
)

result.show(20, truncate=False)
spark.stop()
