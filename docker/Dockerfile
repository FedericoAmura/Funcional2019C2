FROM openjdk:8-jdk-alpine AS base

RUN apk add --update --no-cache bash curl ca-certificates zip unzip git

RUN rm /bin/sh && ln -s `which bash` /bin/sh; bash

RUN curl -s "https://get.sdkman.io" | bash; source "$HOME/.sdkman/bin/sdkman-init.sh"; sdk install scala 2.12.8; sdk install sbt 1.2.8

WORKDIR /app

FROM scala-sbt/latest AS prod

COPY ./ /app