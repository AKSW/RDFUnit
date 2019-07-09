FROM openjdk:8-jdk-slim

mkdir -p /usr/share/man/man1mkdir -p /usr/share/man/man1

RUN apt-get update \
  && apt-get upgrade -y \
  && apt-get install maven -y \
  && apt-get clean \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp

RUN mvn -pl rdfunit-validate -am clean install -DskipTests=true
ENTRYPOINT ["bin/rdfunit"]
