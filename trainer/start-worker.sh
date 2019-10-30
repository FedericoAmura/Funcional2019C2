#!/bin/sh

/spark/bin/spark-class org.apache.spark.deploy.worker.Worker \
    --ip $SPARK_LOCAL_IP \
    --webui-port $SPARK_WORKER_WEBUI_PORT \
    $SPARK_MASTER
