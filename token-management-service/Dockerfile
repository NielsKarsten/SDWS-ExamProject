FROM adoptopenjdk:11-jre-hotspot
COPY target/token-management-service-1.0.0-runner.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx64m -jar token-management-service-1.0.0-runner.jar
