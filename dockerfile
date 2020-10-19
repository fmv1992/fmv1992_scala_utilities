FROM ubuntu:18.04@sha256:b58746c8a89938b8c9f5b77de3b8cf1fe78210c696ab03a1442e235eea65d84f

RUN apt-get update

# Install java.
RUN apt-get -y install openjdk-8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

RUN apt-get install --yes wget zip make

RUN rm -rf /var/lib/apt/lists/*

# Install sbt
RUN mkdir -p /home/user/bin
WORKDIR /home/user/bin
RUN wget -O sbt.zip -- https://github.com/sbt/sbt/releases/download/v1.3.13/sbt-1.3.13.zip
RUN unzip sbt.zip
RUN rm sbt.zip
ENV PATH $PATH:/home/user/bin/sbt/bin

WORKDIR /home/user/
COPY . .
RUN make publishlocal

CMD bash
ENTRYPOINT bash

# vim: set filetype=dockerfile fileformat=unix nowrap:
