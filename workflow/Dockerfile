FROM maven:3.8-openjdk-17-slim AS deps

WORKDIR /opt/app

COPY workflow/settings.xml /root/.m2/settings.xml
COPY pom.xml .
COPY ["common/pom.xml","common/pom.xml","workflow/pom.xml","workflow/pom.xml"]

RUN mvn -B dependency:resolve
RUN mvn -B verify

FROM maven:3.8-openjdk-17-slim AS build

WORKDIR /code

RUN mkdir -p /root/.m2
COPY workflow/settings.xml /root/.m2/settings.xml

COPY ["pooi-parent", "/code"]

# COPY workflow/pom.xml /code/pom.xml
# RUN mvn --batch-mode dependency:resolve
# RUN mvn --batch-mode verify

# Adding source, compile and package into a fat jar
# COPY ["src/main", "/code/src/main"]
# RUN mvn -B -q dependency:resolve
# RUN mvn -B -q verify
RUN mvn -B -DskipTests=true package -pl workflow -am

FROM openjdk:17-slim-buster
COPY --from=build /code/workflow/target/package.jar /package.jar
CMD ["java", "-jar", "/package.jar"]
