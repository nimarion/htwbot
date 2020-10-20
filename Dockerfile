FROM maven:3.6.3-openjdk-15 AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package -Dmaven.test.skip=true

FROM openjdk:15-jdk-slim
COPY --from=build /usr/src/app/target/c0debase-*-SNAPSHOT-shaded.jar htwbot.jar

ENTRYPOINT ["java", "-jar", "htwbot.jar"]