#FROM ubuntu:latest
#LABEL authors="minahabib"
FROM openjdk:17

ENV TZ=US/Eastern

RUN mkdir /app
COPY target/dotty-0.0.1-SNAPSHOT.jar /app
WORKDIR /app
CMD ["java", "-jar", "dotty-0.0.1-SNAPSHOT.jar"]

#ENTRYPOINT ["top", "-b"]