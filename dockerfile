FROM ubuntu:18.04@sha256:b58746c8a89938b8c9f5b77de3b8cf1fe78210c696ab03a1442e235eea65d84f

ARG project_name
ENV PROJECT_NAME $project_name

RUN apt-get update

# Install java.
RUN apt-get -y install openjdk-8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Install support programs.
RUN apt-get install --yes git make wget zip parallel
# Install Scala Native dependencies.
RUN apt-get install --yes clang libgc-dev
RUN rm -rf /var/lib/apt/lists/*

# Install sbt.
RUN mkdir -p /home/user/bin
WORKDIR /home/user/bin
RUN wget -O sbt.zip -- https://github.com/sbt/sbt/releases/download/v1.4.1/sbt-1.4.1.zip
RUN unzip sbt.zip
RUN rm sbt.zip
ENV PATH $PATH:/home/user/bin/sbt/bin

# # Install commit `e0498e8b3b97f8c60e290fa78c63dec70a9654b6` at `scala-native`.
# WORKDIR /tmp
# RUN git clone https://github.com/scala-native/scala-native
# RUN cd ./scala-native \
#     && git reset --hard e0498e8b3b97f8c60e290fa78c63dec70a9654b6 \
#     && sbt '+ publishLocal'
# RUN rm -rf ./scala-native

WORKDIR /home/user/
RUN mkdir ./${PROJECT_NAME}
COPY ./${PROJECT_NAME} ./${PROJECT_NAME}
RUN find ${PROJECT_NAME} -regextype 'egrep' \( \! -iregex '.*(\.properties|\.sbt|/version)' \) -type f -print0 | xargs -0 rm --verbose -rf
RUN find ${PROJECT_NAME} -type d -print0 | xargs -0 rmdir --parents || true
RUN cd ./${PROJECT_NAME} && sbt update
RUN rm -rf ./${PROJECT_NAME}
COPY . .
RUN make publishlocal
RUN make clean

CMD bash
ENTRYPOINT bash

# vim: set filetype=dockerfile fileformat=unix nowrap:
