FROM amazoncorretto:17-alpine-jdk

WORKDIR /app
EXPOSE 8888
ADD ./target/congif-server-0.0.1-SNAPSHOT.jar congif-server.jar

ENTRYPOINT [ "java", "-jar", "congif-server.jar" ]
