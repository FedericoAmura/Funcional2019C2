/spark/bin/spark-submit --master spark://spark-master:7077 --class \
    org.apache.spark.examples.SparkPi \
    --conf spark.driver.host=172.23.0.15 \
    /spark/examples/jars/spark-examples_2.12-2.4.4.jar 10

    # --conf spark.driver.port=$SPARK_DRIVER_PORT \
