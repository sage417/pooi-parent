FROM maven:3.8-openjdk-17-slim AS deps

WORKDIR /opt/app

COPY workflow/settings.xml /root/.m2/settings.xml

COPY pom.xml .
COPY common/pom.xml common/pom.xml
COPY model/pom.xml model/pom.xml
COPY workflow/pom.xml workflow/pom.xml

RUN mvn -B -q dependency:go-offline

FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /code

RUN mkdir -p /root/.m2

COPY --from=deps /root/.m2 /root/.m2

COPY --from=deps /opt/app/ /opt/app
COPY common/src /opt/app/common/src
COPY model/src /opt/app/model/src
COPY workflow/src /opt/app/workflow/src

RUN mvn -B -o -DskipTests=true package -pl workflow -am

FROM openjdk:17-slim-buster
COPY --from=build /code/workflow/target/package.jar /package.jar
CMD ["java", "-jar", "/package.jar"]
