FROM ubuntu:18.04@sha256:b58746c8a89938b8c9f5b77de3b8cf1fe78210c696ab03a1442e235eea65d84f

ARG project_name
ENV PROJECT_NAME $project_name

RUN apt-get update

# Install java.
RUN apt-get -y install openjdk-8-jdk
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

# Install support programs.
RUN apt-get install --yes git make wget zip
# Install Scala Native dependencies.
RUN apt-get install --yes clang libgc-dev
RUN rm -rf /var/lib/apt/lists/*

# Install sbt.
RUN mkdir -p /home/user/bin
WORKDIR /home/user/bin
RUN wget -O sbt.zip -- https://github.com/sbt/sbt/releases/download/v1.4.5/sbt-1.4.5.zip
RUN unzip sbt.zip
RUN rm sbt.zip
ENV PATH $PATH:/home/user/bin/sbt/bin

# Install commit `c27fa07f757b0a83f54bdf817514db3920c64ad4` at `scala-native`.
WORKDIR /tmp
RUN git clone https://github.com/scala-native/scala-native
RUN cd ./scala-native \
    && git reset --hard c27fa07f757b0a83f54bdf817514db3920c64ad4 \
    && echo "ZGlmZiAtLWdpdCBhL25pci9zcmMvbWFpbi9zY2FsYS9zY2FsYS9zY2FsYW5hdGl2ZS9uaXIvVmVyc2lvbnMuc2NhbGEgYi9uaXIvc3JjL21haW4vc2NhbGEvc2NhbGEvc2NhbGFuYXRpdmUvbmlyL1ZlcnNpb25zLnNjYWxhCmluZGV4IDAwZGE3OGEuLjZjZDViZjMgMTAwNjQ0Ci0tLSBhL25pci9zcmMvbWFpbi9zY2FsYS9zY2FsYS9zY2FsYW5hdGl2ZS9uaXIvVmVyc2lvbnMuc2NhbGEKKysrIGIvbmlyL3NyYy9tYWluL3NjYWxhL3NjYWxhL3NjYWxhbmF0aXZlL25pci9WZXJzaW9ucy5zY2FsYQpAQCAtMjUsNSArMjUsNSBAQCBvYmplY3QgVmVyc2lvbnMgewogICBmaW5hbCB2YWwgcmV2aXNpb246IEludCA9IDggLy8gYS5rLmEuIE1JTk9SIHZlcnNpb24KIAogICAvKiBDdXJyZW50IHB1YmxpYyByZWxlYXNlIHZlcnNpb24gb2YgU2NhbGEgTmF0aXZlLiAqLwotICBmaW5hbCB2YWwgY3VycmVudDogU3RyaW5nID0gIjAuNC4wLVNOQVBTSE9UIgorICBmaW5hbCB2YWwgY3VycmVudDogU3RyaW5nID0gIjAuNC4wLU0yIgogfQo=" \
        | base64 -d \
        | git apply \
    && sbt ';++2.11.12;update;compile;'
RUN cd ./scala-native \
    && sbt ';++2.11.12;publishLocal;'
RUN rm -rf ./scala-native

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
