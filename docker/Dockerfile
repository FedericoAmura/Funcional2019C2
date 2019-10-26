FROM openjdk:14-jdk-alpine AS base

RUN apk add --update --no-cache bash curl ca-certificates zip unzip git

RUN rm /bin/sh && ln -s `which bash` /bin/sh; bash

RUN curl -s "https://get.sdkman.io" | bash; source "$HOME/.sdkman/bin/sdkman-init.sh"; sdk install scala 2.13.1; sdk install sbt 1.3.3

WORKDIR /app

FROM scala-sbt/latest AS prod

ARG serviceDir

COPY ./$serviceDir /app