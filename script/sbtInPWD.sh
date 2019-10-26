#!/usr/bin/env bash
# se ejecuta en la carpeta de cada proyecto y nos quedamos con una shell que puede ejecutar sbt en ese directorio
docker run -it --rm -p 5005:5005 -v $PWD:/app -w /app openjdk:14-jdk-alpine sh