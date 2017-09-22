FROM ubuntu:xenial

RUN apt-get update \
  && apt-get upgrade -y \
  && apt-get install git maven openjdk-8-jdk -y

RUN apt-get clean \
  && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

RUN git clone https://github.com/AKSW/RDFUnit.git \
  && cd RDFUnit/ \
  && mvn -pl rdfunit-validate -am clean install

WORKDIR /RDFUnit

ENTRYPOINT ["/RDFUnit/bin/rdfunit"]
