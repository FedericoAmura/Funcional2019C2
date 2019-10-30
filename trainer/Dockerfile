FROM openjdk:8-alpine

RUN apk --update add wget tar bash

RUN wget -q https://www.apache.org/dist/hadoop/common/hadoop-3.2.1/hadoop-3.2.1.tar.gz && \
  tar -xzf hadoop-3.2.1.tar.gz && \
  mv hadoop-3.2.1 /hadoop && \
  rm hadoop-3.2.1.tar.gz

RUN wget -q apache.dattatec.com/spark/spark-2.4.4/spark-2.4.4-bin-without-hadoop-scala-2.12.tgz && \
    tar -xzf spark-2.4.4-bin-without-hadoop-scala-2.12.tgz && \
    mv spark-2.4.4-bin-without-hadoop-scala-2.12 /spark && \
    rm spark-2.4.4-bin-without-hadoop-scala-2.12.tgz

COPY start-master.sh /start-master.sh
COPY start-worker.sh /start-worker.sh

ENV SPARK_DIST_CLASSPATH="/hadoop/etc/hadoop:/hadoop/share/hadoop/common/lib/*:/hadoop/share/hadoop/common/*:/hadoop/share/hadoop/hdfs:/hadoop/share/hadoop/hdfs/lib/*:/hadoop/share/hadoop/hdfs/*:/hadoop/share/hadoop/mapreduce/lib/*:/hadoop/share/hadoop/mapreduce/*:/hadoop/share/hadoop/yarn:/hadoop/share/hadoop/yarn/lib/*:/hadoop/share/hadoop/yarn/*"

ENV SPARK_MASTER_PORT 7077
ENV SPARK_MASTER_WEBUI_PORT 8080
ENV SPARK_LOCAL_IP spark-master

EXPOSE 8080 8081 8082 7077 6066 20020
