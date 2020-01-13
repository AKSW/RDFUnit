# Image to build the jar
FROM maven:3-jdk-8 as build

COPY . /usr/src/myapp
WORKDIR /usr/src/myapp

RUN mvn -pl rdfunit-validate -am clean package -P cli-standalone -DskipTests=true


# Final image to run the jar
FROM openjdk:8-jdk-slim

COPY --from=build /usr/src/myapp/rdfunit-validate/target/rdfunit-validate-*-standalone.jar /usr/src/myapp/rdfunit-validate.jar
WORKDIR /usr/src/myapp

ENTRYPOINT ["java","-jar","/usr/src/myapp/rdfunit-validate.jar"]