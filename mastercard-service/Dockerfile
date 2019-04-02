FROM hirokimatsumoto/alpine-openjdk-11:latest

VOLUME /tmp

COPY target/master-card-service-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
