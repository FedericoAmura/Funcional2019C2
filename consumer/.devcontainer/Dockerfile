FROM openjdk:14-jdk-alpine

LABEL maintainer="pcarossi@fi.uba.ar"

# Or your actual UID, GID on Linux if not the default 1000
ARG USERNAME=vscode
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Configure apk
RUN set -x \
  && apk --update add --no-cache --virtual .build-deps curl \
  #
  # Install shadow, bash, bash-completion, openssh, rsync, git, process tools, python
  && apk add shadow \
  && apk add bash \
  && apk add bash-completion \
  && apk add openssh \
  && apk add rsync \
  && apk add git \
  && apk add procps \
  && apk add python \
  #
  # Allow for a consistant java home location for settings - image is changing over time
  && if [ ! -d "/docker-java-home" ]; then ln -s "${JAVA_HOME}" /docker-java-home; fi \
  #
  # Install sbt
  && SBT_VER="1.3.3" \
  && SBT_ESUM="fe64a24ecd26ae02ac455336f664bbd7db6a040144b3106f1c45ebd42e8a476c" \
  && SBT_URL="https://piccolo.link/sbt-${SBT_VER}.tgz" \
  && curl -Ls ${SBT_URL} > /tmp/sbt-${SBT_VER}.tgz \
  && sha256sum /tmp/sbt-${SBT_VER}.tgz \
  && (echo "${SBT_ESUM}  /tmp/sbt-${SBT_VER}.tgz" | sha256sum -c -) \
  && mkdir /opt/sbt \
  && tar -zxf /tmp/sbt-${SBT_VER}.tgz -C /opt/sbt \
  && rm "/opt/sbt/sbt/bin/"*.bat \
  && sed -i -r 's#run \"\$\@\"#unset JAVA_TOOL_OPTIONS\nrun \"\$\@\"#g' /opt/sbt/sbt/bin/sbt \
  #
  # Install Scala
  && SCALA_VER="2.13.1" \
  && SCALA_ESUM="6918ccc494e34810a7254ad2c4e6f0e1183784c22e7b4801b7dbc8d1994a04db" \
  && SCALA_URL="https://downloads.lightbend.com/scala/${SCALA_VER}/scala-${SCALA_VER}.tgz" \
  && curl -Ls ${SCALA_URL} > /tmp/scala-${SCALA_VER}.tgz \
  && sha256sum /tmp/scala-${SCALA_VER}.tgz \
  && (echo "${SCALA_ESUM}  /tmp/scala-${SCALA_VER}.tgz" | sha256sum -c -) \
  && mkdir /opt/scala \
  && tar -zxf /tmp/scala-${SCALA_VER}.tgz -C /opt/scala \
  && mv "/opt/scala/scala"* "/opt/scala/scala" \
  && rm "/opt/scala/scala/bin/"*.bat \
  && sed -i -r 's#run \"\$\@\"#unset JAVA_TOOL_OPTIONS\nrun \"\$\@\"#g' /opt/scala/scala/bin/scala \
  #
  # Install Bloop
  && BLOOP_VER="1.3.4" \
  && curl -Ls https://github.com/scalacenter/bloop/releases/download/v${BLOOP_VER}/install.py | python - --dest=/opt/bloop \
  #
  # Create a non-root user to use if preferred - see https://aka.ms/vscode-remote/containers/non-root-user.
  && groupadd --gid $USER_GID $USERNAME \
  && useradd -s /bin/bash --uid $USER_UID --gid $USER_GID -m $USERNAME \
  #
  # [Optional] Add sudo support for non-root user
  && apk add sudo \
  && echo $USERNAME ALL=\(root\) NOPASSWD:ALL > /etc/sudoers.d/$USERNAME \
  && chmod 0440 /etc/sudoers.d/$USERNAME \
  #
  # Clean up
  && apk del --purge .build-deps \
  && rm -rf /tmp/sbt-${SBT_VER}.tgz /tmp/scala-${SCALA_VER}.tgz /var/cache/apk/*
  #

# Environment
ENV PATH="/opt/scala/scala/bin:/opt/sbt/sbt/bin:/opt/bloop:$PATH" \
    PS1="\[\033]0;\u@\h:\w\007\]\[\033[01;32m\]\u@\h\[\033[01;34m\]:\w\$\[\033[00m\]" \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:+UseTransparentHugePages -XX:+UseNUMA -Dfile.encoding=UTF-8" \
    SBT_OPTS="-Xmx2048M -Xss2M" \
    METALS_ENABLED="true"
