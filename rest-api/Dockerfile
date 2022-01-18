FROM adoptopenjdk:11-jre-hotspot
COPY target/quarkus-app/quarkus-run.jar /usr/src/
WORKDIR /usr/src/
CMD java -Xmx32m -jar quarkus-run.jar
