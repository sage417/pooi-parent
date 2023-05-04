FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /code

RUN mkdir -p /root/.m2
COPY pooi-parent/workflow/settings.xml /root/.m2/settings.xml

COPY ["pooi-parent", "/code"]

# COPY workflow/pom.xml /code/pom.xml
RUN mvn --batch-mode dependency:resolve
RUN mvn --batch-mode verify

# Adding source, compile and package into a fat jar
# COPY ["src/main", "/code/src/main"]
RUN mvn --batch-mode -pl workflow -amd package 

FROM openjdk:17-slim-buster

COPY --from=build /code/target/package.jar /package.jar

CMD ["java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/package.jar"]
