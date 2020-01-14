# Image to build the jar
FROM maven:3-jdk-8 as build

COPY . /app
WORKDIR /app

RUN mvn -pl rdfunit-validate -am clean package -P cli-standalone -DskipTests=true


# Final image to run the jar
FROM openjdk:8-jdk-slim

COPY --from=build /app/rdfunit-validate/target/rdfunit-validate-*-standalone.jar /app/rdfunit-validate.jar
WORKDIR /app

ENTRYPOINT ["java","-jar","/app/rdfunit-validate.jar"]