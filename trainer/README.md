
La imagen está basada en [este artículo](https://towardsdatascience.com/a-journey-into-big-data-with-apache-spark-part-1-5dfcc2bccdd2) y este [dockerfile](https://github.com/gettyimages/docker-spark/blob/master/Dockerfile).

## Correr master y worker:

```bash
docker-compose build trainer-spark
docker-compose up spark-worker
# en otra consola:
docker-compose run spark-submit
```

## Correr local

TBD


### Ejemplo de código

```scala
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf

val conf = new SparkConf().setAppName("some-app").setMaster("spark://spark-master:7077")
//.set("spark.driver.host", "172.23.0.4")
val sc = new SparkContext(conf)

val data = Array(1, 2, 3, 4, 5)
val distData = sc.parallelize(data)
distData.reduce(_ + _)
```
