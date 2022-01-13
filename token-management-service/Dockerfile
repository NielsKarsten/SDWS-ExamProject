FROM adoptopenjdk:11-jre-hotspot
COPY target/token-service-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar token-service-1.0.0-runner.jar
