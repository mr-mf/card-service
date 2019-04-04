FROM hirokimatsumoto/alpine-openjdk-11:latest

VOLUME /tmp

COPY target/client-account-service-jar-with-dependencies.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
